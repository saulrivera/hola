package outland.emr.tracking.repositories.redis;

import outland.emr.tracking.models.redis.Asset;

public interface IRedisAssetRepository {
    Asset findById(String id);
    void add(Asset asset);
}
