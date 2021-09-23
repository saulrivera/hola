package outland.emr.tracking.repositories.redis;

import outland.emr.tracking.models.redis.Asset;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisAssetRepository implements IRedisAssetRepository {
    private static final String table = "Assets";
    private final RedisTemplate<String, Asset> redisTemplate;
    private final HashOperations hashOperations;

    public RedisAssetRepository(RedisTemplate<String, Asset> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }


    @Override
    public Asset findById(String id) {
        return (Asset) hashOperations.get(table, id);
    }

    @Override
    public void add(Asset asset) {
        hashOperations.put(table, asset.getId(), asset);
    }
}
