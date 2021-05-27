package com.emr.tracing.repositories.mongo;

import com.emr.tracing.models.mongo.AssetBeacon;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoAssetBeaconRepository extends MongoRepository<AssetBeacon, String> {
}
