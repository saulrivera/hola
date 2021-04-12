package com.emr.tracking.controller

import com.emr.tracking.model.Patient
import com.emr.tracking.repository.MongoPatientRepository
import com.fasterxml.jackson.databind.type.MapType
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/patient")
class PatientController(
    private val mongoPatientRepository: MongoPatientRepository
) {

    @GetMapping
    fun getPatients(): List<Patient> {
        return mongoPatientRepository.findAll()
    }

    @PostMapping
    fun addPatient(@RequestBody patient: Patient): Patient {
        patient.id = UUID.randomUUID().toString()
        return mongoPatientRepository.save(patient)
    }

    @PutMapping
    fun updatePatient(@RequestBody patient: Patient): Patient {
        val oldPatientPromise = mongoPatientRepository.findById(patient.id!!)

        val oldPatient: Patient
        if (oldPatientPromise.isPresent) {
           oldPatient = oldPatientPromise.get()
        } else {
            throw Error("Patient not found")
        }

        oldPatient.firstname = patient.firstname
        oldPatient.middlename = patient.middlename
        oldPatient.lastname = patient.lastname
        oldPatient.room = patient.room
        oldPatient.contactInfo.phone = patient.contactInfo.phone
        oldPatient.contactInfo.email = patient.contactInfo.email

        return mongoPatientRepository.save(oldPatient)
    }

    @GetMapping("/searchByName")
    fun searchByName(@RequestParam name: String): ResponseEntity<List<Patient>> {
        val found = mongoPatientRepository.findAll().filter {
            it.fullName().contains(name.toLowerCase())
        }
        return ResponseEntity.ok(found)
    }
}