package outland.emr.tracking.repositories.redis;

import outland.emr.tracking.models.redis.Beacon;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import outland.emr.tracking.models.redis.RecordState;

import java.util.Map;

@Repository
public class RedisBeaconRepository implements IRedisBeaconRepository {
    private static final String table = "Beacon";
    private final RedisTemplate<String, Beacon> redisTemplate;
    private final HashOperations hashOperations;

    public RedisBeaconRepository(RedisTemplate<String, Beacon> redisTemplate) {
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public boolean isBeaconPresent(String mac) {
        var value = (Beacon)hashOperations.get(table, mac);
        return value != null;
    }

    @Override
    public Beacon findBeaconByMac(String mac) {
        return (Beacon) hashOperations.get(table, mac);
    }

    @Override
    public void add(Beacon beacon) {
        hashOperations.put(table, beacon.getMac(), beacon);
    }

    @Override
    public void flush() {
        Map<String, RecordState> records = hashOperations.entries(table);
        records.keySet().forEach(key -> hashOperations.delete(table, key));
    }
}
