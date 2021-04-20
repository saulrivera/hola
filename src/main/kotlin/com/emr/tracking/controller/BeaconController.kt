package com.emr.tracking.controller

import com.emr.tracking.manager.StreamManager
import com.emr.tracking.model.*
import com.emr.tracking.repository.MongoBeaconRepository
import com.emr.tracking.repository.MongoPatientBeaconRegistry
import com.emr.tracking.repository.MongoPatientRepository
import com.emr.tracking.websocket.TrackingSocket
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/beacon")
class BeaconController(
    private val mongoPatientRepository: MongoPatientRepository,
    private val mongoBeaconRepository: MongoBeaconRepository,
    private val mongoPatientBeaconRegistry: MongoPatientBeaconRegistry,
    private val trackingSocket: TrackingSocket,
    private val streamManager: StreamManager
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

        val oldRegistry = mongoPatientBeaconRegistry.findAll().firstOrNull { it.active && it.patientId == patientId }
        val patientBeaconRegistry = PatientBeaconRegistry(UUID.randomUUID().toString(), patientId, beacon.mac, true, LocalDateTime.now(), LocalDateTime.now())
        mongoPatientBeaconRegistry.save(patientBeaconRegistry)

        if (oldRegistry != null) {
            oldRegistry.active = false
            mongoPatientBeaconRegistry.save(oldRegistry)
        }

        val stream = streamManager.createStreamSocketFor(patient, patientBeaconRegistry)
        if (stream != null) {
            trackingSocket.emitBeaconUpdate(stream)
        }

        patient.trackingDeviceId = beacon.uniqueId
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

        val streamSocket = streamManager.createStreamSocketForPatient(patient)
        if (streamSocket != null) {
            trackingSocket.emitBeaconDetachment(streamSocket)
        }

        patientBeaconRegistry.active = false
        patientBeaconRegistry.updatedAt = LocalDateTime.now()

        mongoPatientBeaconRegistry.save(patientBeaconRegistry)

        patient.trackingDeviceId = ""
        return mongoPatientRepository.save(patient)
    }

    @PostMapping
    fun addDevices(@RequestBody devices: List<Beacon>): List<Beacon> {
        return mongoBeaconRepository.saveAll(devices)
    }
}