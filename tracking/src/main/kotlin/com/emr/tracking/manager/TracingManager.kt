package com.emr.tracking.manager

import com.emr.tracking.configuration.AppProperties
import com.emr.tracking.configuration.WebSocketConfiguration
import com.emr.tracking.model.*
import com.emr.tracking.repository.Neo4jGatewayRepository
import com.emr.tracking.repository.RedisBeaconRepository
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
    private val appProperties: AppProperties,
    private val webSocketConfiguration: WebSocketConfiguration,
    private val kontaktioManager: KontaktioManager
) {
    suspend fun processBeaconStream(stream: KontaktTelemetryResponse) {
        if (!kontaktioManager.beaconList().any { it.mac == stream.trackingId })  {
            return
        }

        val streamMemoryOptional = redisStreamRepository.findById(stream.trackingId)

        val streamMemory = if (streamMemoryOptional.isPresent) {
            streamMemoryOptional.get()
        } else {
            RedisStreamReading(
                stream.trackingId,
                stream.sourceId,
                stream.rssi,
                stream.calibratedRssi1m,
                mutableMapOf()
            )
        }

        val lastGatewayId = streamMemory.gatewayId

        val redisGateway = redisGatewayNodeRepository.findById(lastGatewayId)
        val nearGateways = if (redisGateway.isPresent) {
            redisGateway.get().siblings.toMutableList()
        } else {
            neo4jGatewayRepository.findNearSiblingsByUniqueId(lastGatewayId)
                .map { it.uniqueId }
                .toMutableList()
        }

        if (redisGateway.isEmpty) {
            nearGateways.add(lastGatewayId)
            redisGatewayNodeRepository.save(RedisGatewayNode(lastGatewayId, nearGateways))
        }

        if (!nearGateways.contains(stream.sourceId)) {
            return
        }

        val kalmanFilter = if (!streamMemoryOptional.isPresent) {
            KalmanFilter(
                appProperties.appTracingKalmanFilterR.toDouble(),
                appProperties.appTracingKalmanFilterQ.toDouble()
            )
        } else {
            val parameterForThisGateway = streamMemory.gatewayHistories[streamMemory.gatewayId]
            if (parameterForThisGateway != null) {
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
        }

        stream.rssi = kalmanFilter.filter(stream.rssi)

        val parameters = RedisGatewayParameters(
            kalmanFilter.A,
            kalmanFilter.B,
            kalmanFilter.C,
            kalmanFilter.cov,
            kalmanFilter.x
        )

        streamMemory.gatewayHistories[stream.sourceId] = parameters

        if (stream.rssi > streamMemory.rssi) {
            streamMemory.rssi = stream.rssi
            streamMemory.gatewayId = stream.sourceId
            streamMemory.calibratedRssi1m = stream.calibratedRssi1m
        }

//        if (isRelevantIncidence(streamMemory)) {
            webSocketConfiguration
                .tracingHandler()
                .broadcastTracking(mapOf(
                    "trackingId" to streamMemory.trackingId,
                    "gatewayId" to streamMemory.gatewayId,
                    "rssi" to streamMemory.rssi,
                    "calibratedRssi1m" to streamMemory.calibratedRssi1m
                ))
//        }

        redisStreamRepository.save(streamMemory)
    }

    fun isRelevantIncidence(streamReading: RedisStreamReading): Boolean {
        val streamMemoryOptional = redisStreamRepository.findById(streamReading.trackingId)
        if (streamMemoryOptional.isEmpty) {
            return true
        }
        val streamMemory = streamMemoryOptional.get()
        if (streamMemory.gatewayId != streamReading.gatewayId) {
            return true
        }
        return false
    }

    private fun calculateDistance(txRSSI: Double, pwRSSI: Double): Double {
        return 10.0.pow((txRSSI - pwRSSI) / (-10.0 * appProperties.appTracingEnvironmentFactor.toDouble()))
    }
}