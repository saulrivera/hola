package com.emr.tracing.repositories.mongo;

import com.emr.tracing.models.mongo.Beacon;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoBeaconRepository extends MongoRepository<Beacon, String> {
}
