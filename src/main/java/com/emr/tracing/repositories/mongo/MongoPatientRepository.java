package com.emr.tracing.repositories.mongo;

import com.emr.tracing.models.mongo.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoPatientRepository extends MongoRepository<Patient, String> {
}
