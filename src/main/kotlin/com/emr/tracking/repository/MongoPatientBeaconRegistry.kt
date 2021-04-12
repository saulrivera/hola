package com.emr.tracking.repository

import com.emr.tracking.model.PatientBeaconRegistry
import org.springframework.data.mongodb.repository.MongoRepository

interface MongoPatientBeaconRegistry: MongoRepository<PatientBeaconRegistry, String> {
}