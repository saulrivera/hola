package com.emr.tracking.manager

import com.emr.tracking.model.KontaktGatewayResponse
import com.emr.tracking.model.RedisGatewayNode
import com.emr.tracking.model.RedisStreamReading
import com.emr.tracking.repository.Neo4jGatewayRepository
import com.emr.tracking.repository.RedisGatewayNodeRepository
import com.emr.tracking.repository.RedisStreamRepository
import com.google.gson.Gson
import org.springframework.stereotype.Component
import java.io.File
import java.time.Instant

@Component
class TracingManager(
    private val redisStreamRepository: RedisStreamRepository,
    private val redisGatewayNodeRepository: RedisGatewayNodeRepository,
    private val neo4jGatewayRepository: Neo4jGatewayRepository,
    private val kontaktGatewayManager: KontaktGatewayManager
) {
    suspend fun traceBeacons() {
        val gatewayIds = kontaktGatewayManager.getListOfGateways()
        val detections = kontaktGatewayManager.retrieveDataForGateway(gatewayIds)

        val groupedDevices = detections.groupBy { it.uniqueId }

//        saveData(groupedDevices)

        groupedDevices.forEach { v ->
            val deviceId = v.key
            val lastStream = redisStreamRepository.findById(deviceId)
            val detectedGateways = v.value

            val maxGateway: KontaktGatewayResponse? = if (lastStream.isPresent) {
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

                val filteredGateways = detectedGateways.filter { nearGateways.contains(it.sourceId) }

                filteredGateways.maxByOrNull { it.rssi }

            } else {
                detectedGateways.maxByOrNull { it.rssi }
            }

            if (maxGateway != null) {
                redisStreamRepository.save(RedisStreamReading(deviceId, maxGateway.sourceId, maxGateway.rssi))
            }
        }

        print("\u001b[H\u001b[2J")
        println("")
        redisStreamRepository.findAll().sortedBy { it.deviceId }.forEach {
            println("Device ${it.deviceId} is close to ${it.gatewayId} with px ${it.rssi}")
        }
    }

    fun saveData(data: Map<String, List<KontaktGatewayResponse>>) {
        val file = File("./data/${Instant.now()}")
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        val jsonData = Gson().toJson(data)
        file.writeText(jsonData)
    }
}