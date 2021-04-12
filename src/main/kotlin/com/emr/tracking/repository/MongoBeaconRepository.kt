package com.emr.tracking.repository

import com.emr.tracking.model.Beacon
import org.springframework.data.mongodb.repository.MongoRepository

interface MongoBeaconRepository: MongoRepository<Beacon, String> {
}