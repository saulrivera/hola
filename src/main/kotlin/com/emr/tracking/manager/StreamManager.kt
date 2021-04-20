package com.emr.tracking.manager

import com.emr.tracking.model.Patient
import com.emr.tracking.model.PatientBeaconRegistry
import com.emr.tracking.model.StreamSocket
import com.emr.tracking.model.StreamSocketGateway
import com.emr.tracking.repository.GatewayRepository
import com.emr.tracking.repository.MongoPatientBeaconRegistry
import com.emr.tracking.repository.MongoPatientRepository
import com.emr.tracking.repository.StreamRepository
import com.emr.tracking.websocket.TrackingSocket
import org.springframework.stereotype.Component

@Component
class StreamManager(
    private val mongoPatientBeaconRegistry: MongoPatientBeaconRegistry,
    private val streamRepository: StreamRepository,
    private val gatewayRepository: GatewayRepository,
    private val trackingSocket: TrackingSocket
) {
    companion object {
        @Volatile private var streamSocketsQueue = mutableMapOf<String, StreamSocket>()
    }

    fun createStreamSocketForPatient(patient: Patient): StreamSocket? {
        val patientBeaconRegistry = mongoPatientBeaconRegistry
            .findAll()
            .firstOrNull { it.active && it.patientId == patient.id } ?: return null
        val streamReading = streamRepository
            .findByBeaconMac(patientBeaconRegistry.beaconId) ?: return null
        val gateway = gatewayRepository
            .findByMac(streamReading.gatewayId) ?: return null

        return StreamSocket(
            streamReading.trackingId,
            streamReading.rssi,
            streamReading.calibratedRssi1m,
            StreamSocketGateway(
                gateway.mac,
                listOf(gateway.position.first, gateway.position.second, gateway.floor.toDouble())
            ),
            patient
        )
    }

    fun  createStreamSocketFor(patient: Patient, patientBeaconRegistry: PatientBeaconRegistry): StreamSocket? {
        val streamReading = streamRepository.findByBeaconMac(patientBeaconRegistry.beaconId) ?: return null
        val gateway = gatewayRepository.findByMac(streamReading.gatewayId) ?: return null

        return StreamSocket(
            streamReading.trackingId,
            streamReading.rssi,
            streamReading.calibratedRssi1m,
            StreamSocketGateway(
                gateway.mac,
                listOf(gateway.position.first, gateway.position.second, gateway.floor.toDouble())
            ),
            patient
        )
    }

    fun add(streamSocket: StreamSocket) {
        streamSocketsQueue[streamSocket.mac] = streamSocket
    }

    fun broadcastStreams() {
        streamSocketsQueue.forEach {
            trackingSocket.broadcastTracking(it.value)
        }
        streamSocketsQueue.clear()
    }
}