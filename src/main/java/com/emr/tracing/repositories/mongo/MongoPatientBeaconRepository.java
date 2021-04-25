package com.emr.tracing.repositories.mongo;

import com.emr.tracing.models.mongo.PatientBeacon;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoPatientBeaconRepository extends MongoRepository<PatientBeacon, String> {
    List<PatientBeacon> findByActive(boolean active);
    PatientBeacon findByPatientId(String id);
    PatientBeacon findByActiveAndPatientId(boolean active, String patientId);
    PatientBeacon findByActiveAndBeaconId(boolean active, String beaconId);
}
