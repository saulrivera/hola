package outland.emr.tracking.repositories.redis;

import outland.emr.tracking.models.redis.AssetBeacon;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class RedisAssetBeaconRepository implements IRedisAssetBeaconRepository {
    private static final String table = "AssetBeacons";
    private final RedisTemplate<String, AssetBeacon> redisTemplate;
    private final HashOperations hashOperations;

    public RedisAssetBeaconRepository(RedisTemplate<String, AssetBeacon> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public Map<String, AssetBeacon> findAll() {
        return hashOperations.entries(table);
    }

    @Override
    public AssetBeacon findByBeaconMac(String mac) {
        return (AssetBeacon) hashOperations.get(table, mac);
    }

    @Override
    public AssetBeacon findByAssetId(String id) {
        Optional<AssetBeacon> optional = findAll().values()
                .stream()
                .filter(it -> it.getAssetId().equals(id))
                .findFirst();
        if (optional.isEmpty()) return null;
        return optional.get();
    }

    @Override
    public void add(AssetBeacon assetBeacon) {
        hashOperations.put(table, assetBeacon.getBeaconId(), assetBeacon);
    }
}
