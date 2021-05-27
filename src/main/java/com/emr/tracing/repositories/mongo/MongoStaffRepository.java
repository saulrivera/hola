package com.emr.tracing.repositories.mongo;

import com.emr.tracing.models.mongo.Staff;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoStaffRepository extends MongoRepository<Staff, String> {
}
