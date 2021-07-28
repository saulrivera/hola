package com.emr.tracing.repositories.mongo;

import com.emr.tracing.models.mongo.Reading;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoReadingRepository extends MongoRepository<Reading, String> {
}
