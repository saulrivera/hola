package com.emr.tracking.controller

import com.emr.tracking.configuration.WebSocketConfiguration
import com.emr.tracking.model.Beacon
import com.emr.tracking.model.Patient
import com.emr.tracking.model.PatientBeaconRegistry
import com.emr.tracking.repository.MongoBeaconRepository
import com.emr.tracking.repository.MongoPatientBeaconRegistry
import com.emr.tracking.repository.MongoPatientRepository
import com.emr.tracking.repository.StreamRepository
import com.emr.tracking.websocket.TrackingSocket
import org.springframework.web.bind.annotation.*
import org.springframework.web.socket.config.annotation.DelegatingWebSocketConfiguration
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/beacon")
class BeaconController(
    private val mongoPatientRepository: MongoPatientRepository,
    private val mongoBeaconRepository: MongoBeaconRepository,
    private val mongoPatientBeaconRegistry: MongoPatientBeaconRegistry,
    private val streamRepository: StreamRepository,
    private val trackingSocket: TrackingSocket
) {

    @GetMapping("/available")
    fun getAll(): List<Beacon> {
        val lockedBeacons = mongoPatientBeaconRegistry.findAll().filter { it.active }.map { it.beaconId }
        return mongoBeaconRepository.findAll().filter { !lockedBeacons.contains(it.mac) }
    }

    @GetMapping("/activated")
    fun getActivated(): List<Beacon> {
        val lockedBeaconIds = mongoPatientBeaconRegistry.findAll().filter { it.active }.map { it.beaconId }
        return mongoBeaconRepository.findAll().filter { lockedBeaconIds.contains(it.mac) }
    }

    @PutMapping("/associate")
    fun associate(@RequestParam patientId: String, @RequestParam uniqueId: String): Patient {
        val patientPromise = mongoPatientRepository.findById(patientId)
        if (patientPromise.isEmpty) {
            throw Error("Patient not found")
        }
        val beacon: Beacon = mongoBeaconRepository.findAll().first { it.uniqueId == uniqueId }
            ?: throw Error("Beacon not found")
        val patient = patientPromise.get()

        val patientBeaconRegistry = PatientBeaconRegistry(UUID.randomUUID().toString(), patientId, beacon.mac, true, LocalDateTime.now(), LocalDateTime.now())
        patient.trackingDeviceId = beacon.uniqueId
        mongoPatientBeaconRegistry.save(patientBeaconRegistry)

        return mongoPatientRepository.save(patient)
    }

    @PutMapping("/deassociate")
    fun deassociate(@RequestParam patientId: String): Patient {
        val patientPromise = mongoPatientRepository.findById(patientId)
        if (patientPromise.isEmpty) {
            throw Error("Patient not found")
        }
        val patient = patientPromise.get()

        val patientBeaconRegistry = mongoPatientBeaconRegistry.findAll().first { it.active && it.patientId == patientId }
        patientBeaconRegistry.active = false
        patientBeaconRegistry.updatedAt = LocalDateTime.now()

        mongoPatientBeaconRegistry.save(patientBeaconRegistry)

        val stream = streamRepository.findByBeaconMac(patientBeaconRegistry.beaconId)!!
        trackingSocket.emitBeaconDetachment(stream)

        patient.trackingDeviceId = ""
        return mongoPatientRepository.save(patient)
    }

    @PostMapping
    fun addDevices(@RequestBody devices: List<Beacon>): List<Beacon> {
        return mongoBeaconRepository.saveAll(devices)
    }
}