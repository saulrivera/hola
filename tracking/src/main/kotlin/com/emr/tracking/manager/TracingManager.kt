package com.emr.tracking.manager

import com.emr.tracking.configuration.AppProperties
import com.emr.tracking.model.KontaktGatewayResponse
import com.emr.tracking.model.RedisGatewayNode
import com.emr.tracking.model.RedisGatewayParameters
import com.emr.tracking.model.RedisStreamReading
import com.emr.tracking.repository.Neo4jGatewayRepository
import com.emr.tracking.repository.RedisGatewayNodeRepository
import com.emr.tracking.repository.RedisStreamRepository
import com.emr.tracking.utils.KalmanFilter
import com.google.gson.Gson
import org.springframework.stereotype.Component
import java.io.File
import java.time.Instant
import kotlin.math.abs

@Component
class TracingManager(
    private val redisStreamRepository: RedisStreamRepository,
    private val redisGatewayNodeRepository: RedisGatewayNodeRepository,
    private val neo4jGatewayRepository: Neo4jGatewayRepository,
    private val kontaktGatewayManager: KontaktGatewayManager,
    private val firebaseMessageManager: FirebaseMessageManager,
    private val appProperties: AppProperties
) {
    suspend fun traceBeacons() {
        val gatewayIds = kontaktGatewayManager.getListOfGateways()
        val detections = kontaktGatewayManager.retrieveDataForGateway(gatewayIds)

        val groupedDevices = detections.groupBy { it.uniqueId }

//        saveData(groupedDevices)

        val readings = groupedDevices.map { v ->
            val deviceId = v.key
            val lastStream = redisStreamRepository.findById(deviceId)
            val detectedGateways = v.value

            val maxGateway: KontaktGatewayResponse?
            val parameters: Map<String, RedisGatewayParameters>?

            if (!lastStream.isPresent) {
                parameters = detectedGateways.map {
                    val filter = KalmanFilter(
                        appProperties.appTracingKalmanFilterR.toDouble(),
                        appProperties.appTracingKalmanFilterQ.toDouble()
                    )
                    it.rssi = filter.filter(it.rssi)
                    it.sourceId to RedisGatewayParameters(filter.A, filter.B, filter.C, filter.cov, filter.x)
                }.toMap()

                maxGateway = detectedGateways.maxByOrNull { it.rssi }
            } else {
                val lastGatewayId = lastStream.get().gatewayId
                val nearGateways: MutableList<String>

                val redisGateway = redisGatewayNodeRepository.findById(lastGatewayId)
                if (redisGateway.isPresent) {
                    nearGateways = redisGateway.get().siblings.toMutableList()
                } else {
                    nearGateways = neo4jGatewayRepository.findNearSiblingsByUniqueId(lastGatewayId)
                        .map { it.uniqueId }
                        .toMutableList()
                    redisGatewayNodeRepository.save(RedisGatewayNode(lastGatewayId, nearGateways))
                }

                nearGateways.add(lastGatewayId)
                val filteredGateways = detectedGateways.filter { nearGateways.contains(it.sourceId) }.toMutableList()

                val lastNewReading = filteredGateways.firstOrNull { it.sourceId == lastGatewayId }

                if (lastNewReading == null) {
                    filteredGateways.add(
                        KontaktGatewayResponse(
                            lastGatewayId,
                            lastStream.get().rssi,
                            27,
                            lastStream.get().deviceId)
                    )
                }

                parameters = filteredGateways.map {
                    val parameterForThisGateway = lastStream.get().gatewayHistories[it.sourceId]
                    val filter: KalmanFilter = if (parameterForThisGateway != null) {
                        KalmanFilter(
                            appProperties.appTracingKalmanFilterR.toDouble(),
                            appProperties.appTracingKalmanFilterQ.toDouble(),
                            parameterForThisGateway.a,
                            parameterForThisGateway.b,
                            parameterForThisGateway.c,
                            parameterForThisGateway.cov,
                            parameterForThisGateway.x
                        )
                    } else {
                        KalmanFilter(
                            appProperties.appTracingKalmanFilterR.toDouble(),
                            appProperties.appTracingKalmanFilterQ.toDouble()
                        )
                    }
                    it.rssi = filter.filter(it.rssi)
                    it.sourceId to RedisGatewayParameters(filter.A, filter.B, filter.C, filter.cov, filter.x)
                }.toMap()

                maxGateway = filteredGateways.maxByOrNull { it.rssi }
            }

             deviceId to RedisStreamReading(deviceId, maxGateway!!.sourceId, maxGateway.rssi, parameters)
        }.toMap()

//        val relevantIncidences = processRelevantIncidences(readings)
        firebaseMessageManager.publishReadingUpdates(readings.values.toList())
        redisStreamRepository.saveAll(readings.values.toList())

        print("\u001b[H\u001b[2J")
        println("")
        redisStreamRepository.findAll().sortedBy { it.deviceId }.forEach {
            println("Device ${it.deviceId} is close to gateway ${it.gatewayId} with power ${it.rssi}")
        }
    }

//    fun processRelevantIncidences(newReadings: Map<String, RedisStreamReading>): List<RedisStreamReading> {
//        val oldReadings = redisStreamRepository.findAll().map { it.deviceId to it }.toMap()
//
//        val upgradeReadings = mutableListOf<RedisStreamReading>()
//        newReadings.map { (deviceId, reading) ->
//             val oldReading = oldReadings[deviceId]
//
//             if (oldReading == null)
//                upgradeReadings.add(reading)
//
//             if (oldReading!!.gatewayId != reading.gatewayId)
//                 upgradeReadings.add(reading)
//        }
//
//        return upgradeReadings
//    }

    fun saveData(data: Map<String, List<KontaktGatewayResponse>>) {
        val file = File("./data/${Instant.now()}")
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        val jsonData = Gson().toJson(data)
        file.writeText(jsonData)
    }
}