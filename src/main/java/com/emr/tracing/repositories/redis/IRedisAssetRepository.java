package com.emr.tracing.repositories.redis;

import com.emr.tracing.models.redis.Asset;

public interface IRedisAssetRepository {
    Asset findById(String id);
    void add(Asset asset);
}
