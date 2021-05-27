package com.emr.tracing.repositories.mongo;

import com.emr.tracing.models.mongo.StaffBeacon;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoStaffBeaconRepository extends MongoRepository<StaffBeacon, String> {
}
