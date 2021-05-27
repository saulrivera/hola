package com.emr.tracing.repositories.redis;

import com.emr.tracing.models.redis.AssetBeacon;

public interface IRedisAssetBeaconRepository {
    AssetBeacon findByBeaconMac(String mac);
    AssetBeacon findByAssetId(String id);
    void add(AssetBeacon assetBeacon);
}
