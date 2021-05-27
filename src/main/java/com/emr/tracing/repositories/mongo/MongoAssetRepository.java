package com.emr.tracing.repositories.mongo;

import com.emr.tracing.models.mongo.Asset;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoAssetRepository extends MongoRepository<Asset, String> {
}
