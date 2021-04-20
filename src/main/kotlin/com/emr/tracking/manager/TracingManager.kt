package com.emr.tracking.manager

import com.emr.tracking.configuration.AppProperties
import com.emr.tracking.model.*
import com.emr.tracking.repository.*
import com.emr.tracking.utils.KalmanFilter
import org.springframework.stereotype.Component
import kotlin.math.abs

@Component
class TracingManager(
    private val appProperties: AppProperties,
    private val streamRepository: StreamRepository,
    private val beaconRepository: BeaconRepository,
    private val gatewayRepository: GatewayRepository,
    private val mongoPatientBeaconRegistry: MongoPatientBeaconRegistry,
    private val mongoPatientRepository: MongoPatientRepository,
    private val streamManager: StreamManager,
) {
    @Synchronized fun processBeaconStream(stream: TelemetryResponse) {
        if (!beaconRepository.isBeaconPresent(stream.trackingId)) {
            return
        }

        val patientBeaconRegistry = mongoPatientBeaconRegistry.findAll().find { it.active && it.beaconId == stream.trackingId }
            ?: return

        val streamMemory = streamRepository.findById(stream)

        // Discards all nodes that are not closed to the last seen position
        val lastGatewayId = streamMemory.gatewayId
        val rethinkGateway = gatewayRepository.findByMac(lastGatewayId) ?: return
        val nearGateways = rethinkGateway.siblings
        if (!nearGateways.contains(stream.sourceId))
            return

        // Loads all kalman parameters
        val kalmanFilter = if (streamMemory.gatewayHistories.isEmpty()) {
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

        // Recalculates and updates history of record from specific gateway
        val clearedRssi = kalmanFilter.filter(stream.rssi)

        if (abs(clearedRssi - streamMemory.rssi) < abs(streamMemory.rssi * appProperties.appTracingThresholdBeaconChange.toFloat())) {
            return
        }

        streamMemory.rssi = clearedRssi

        val parameters = GatewayParameters(
            kalmanFilter.A,
            kalmanFilter.B,
            kalmanFilter.C,
            kalmanFilter.cov,
            kalmanFilter.x,
            streamMemory.rssi
        )
        streamMemory.gatewayHistories[stream.sourceId] = parameters

        // Find the closest reading among those who has detected lastly and assigns it as source
        val minimumReading = streamMemory.gatewayHistories.maxByOrNull { it.value.rssi }

        val needsBroadcastResult = minimumReading?.key ?: "" != streamMemory.gatewayId

        streamMemory.rssi = minimumReading!!.value.rssi
        streamMemory.gatewayId = minimumReading.key
        streamMemory.calibratedRssi1m = stream.calibratedRssi1m

        if (needsBroadcastResult) {
            val gateway = gatewayRepository.findByMac(streamMemory.gatewayId) ?: return
            val patient = mongoPatientRepository.findById(patientBeaconRegistry.patientId).get()

            val webSocketMessage = StreamSocket(
                streamMemory.trackingId,
                streamMemory.rssi,
                streamMemory.calibratedRssi1m,
                StreamSocketGateway(
                    gateway.uniqueId,
                    listOf(gateway.position.first, gateway.position.second, gateway.floor.toDouble())
                ),
                patient
            )

            streamManager.add(webSocketMessage)
        }

        streamRepository.update(streamMemory)
    }
}