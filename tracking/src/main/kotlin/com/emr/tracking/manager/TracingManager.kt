package com.emr.tracking.manager

import com.emr.tracking.configuration.AppProperties
import com.emr.tracking.configuration.WebSocketConfiguration
import com.emr.tracking.model.*
import com.emr.tracking.repository.Neo4jGatewayRepository
import com.emr.tracking.repository.RedisGatewayNodeRepository
import com.emr.tracking.repository.RedisStreamRepository
import com.emr.tracking.utils.KalmanFilter
import org.springframework.stereotype.Component
import kotlin.math.pow

@Component
class TracingManager(
    private val redisStreamRepository: RedisStreamRepository,
    private val redisGatewayNodeRepository: RedisGatewayNodeRepository,
    private val neo4jGatewayRepository: Neo4jGatewayRepository,
    private val kontaktGatewayManager: KontaktGatewayManager,
    private val appProperties: AppProperties,
    private val webSocketConfiguration: WebSocketConfiguration
) {
    suspend fun traceBeacons() {
        val gatewayIds = kontaktGatewayManager.getListOfGateways()
        val detections = kontaktGatewayManager.retrieveDataForGateway(gatewayIds)

        val groupedDevices = detections.groupBy { it.uniqueId }

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
                    it.sourceId to RedisGatewayParameters(
                        filter.A,
                        filter.B,
                        filter.C,
                        filter.cov,
                        filter.x
                    )
                }.toMap()

                maxGateway = calculateClosestGateway(detectedGateways)
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
                val filteredGateways = detectedGateways
                    .filter { nearGateways.contains(it.sourceId) }
                    .toMutableList()

                val lastNewReading = filteredGateways.firstOrNull { it.sourceId == lastGatewayId }
                if (lastNewReading == null) {
                    filteredGateways.add(
                        KontaktGatewayResponse(
                            lastGatewayId,
                            lastStream.get().rssi,
                            lastStream.get().calibratedRssi1m,
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
                    it.sourceId to RedisGatewayParameters(
                        filter.A,
                        filter.B,
                        filter.C,
                        filter.cov,
                        filter.x
                    )
                }.toMap()

                maxGateway = calculateClosestGateway(filteredGateways)
            }

             deviceId to RedisStreamReading(
                 deviceId,
                 maxGateway!!.sourceId,
                 maxGateway.rssi,
                 maxGateway.calibratedRssi1m!!,
                 parameters
             )
        }.toMap()

        val relevantIncidencesForSocket = processRelevantIncidences(readings)
        if (relevantIncidencesForSocket.count() > 0) {
            webSocketConfiguration
                .tracingHandler()
                .broadcastTracking(relevantIncidencesForSocket)
        }

        redisStreamRepository.saveAll(readings.values.toList())
    }

    private fun calculateClosestGateway(gateways: List<KontaktGatewayResponse>): KontaktGatewayResponse? {
        return gateways.maxByOrNull { it.rssi }
    }

    fun processRelevantIncidences(newReadings: Map<String, RedisStreamReading>): List<SocketTracingStream> {
        val oldReadings = redisStreamRepository.findAll().map { it.deviceId to it }.toMap()

        val upgradeReadings = mutableListOf<RedisStreamReading>()
        newReadings.forEach { (deviceId, reading) ->
             val oldReading = oldReadings[deviceId]

             if (oldReading == null) {
                 upgradeReadings.add(reading)
             } else {
                 if (oldReading.gatewayId != reading.gatewayId) {
                     upgradeReadings.add(reading)
                 }
             }
        }

        return upgradeReadings.map {
            SocketTracingStream(it.deviceId, it.gatewayId, it.rssi, it.calibratedRssi1m)
        }
    }

    private fun calculateDistance(txRSSI: Double, pwRSSI: Double): Double {
        return 10.0.pow((txRSSI - pwRSSI) / (-10.0 * appProperties.appTracingEnvironmentFactor.toDouble()))
    }
}