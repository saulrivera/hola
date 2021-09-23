package outland.emr.tracking.repositories.redis;

import outland.emr.tracking.models.redis.AssetBeacon;

public interface IRedisAssetBeaconRepository {
    AssetBeacon findByBeaconMac(String mac);
    AssetBeacon findByAssetId(String id);
    void add(AssetBeacon assetBeacon);
}
