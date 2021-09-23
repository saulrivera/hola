package outland.emr.tracking.repositories.redis;

import outland.emr.tracking.models.redis.StaffBeacon;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class RedisStaffBeaconRepository implements IRedisStaffBeaconRepository {
    private static final String table = "StaffBeacons";
    private final RedisTemplate<String, StaffBeacon> redisTemplate;
    private final HashOperations hashOperations;

    public RedisStaffBeaconRepository(RedisTemplate<String, StaffBeacon> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public Map<String, StaffBeacon> findAll() {
        return hashOperations.entries(table);
    }

    @Override
    public StaffBeacon findByBeaconMac(String mac) {
        return (StaffBeacon) hashOperations.get(table, mac);
    }

    @Override
    public StaffBeacon findByStaffId(String id) {
        Optional<StaffBeacon> optional = findAll().values()
                .stream()
                .filter(it -> it.getStaffId().equals(id))
                .findFirst();
        if (optional.isEmpty()) return null;
        return optional.get();
    }

    @Override
    public void add(StaffBeacon staffBeacon) {
        hashOperations.put(table, staffBeacon.getBeaconId(), staffBeacon);
    }
}
