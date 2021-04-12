package com.emr.tracking.repository

import com.emr.tracking.model.Patient
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface MongoPatientRepository: MongoRepository<Patient, String> {
}