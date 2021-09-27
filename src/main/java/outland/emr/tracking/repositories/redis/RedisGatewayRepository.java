package outland.emr.tracking.repositories.redis;

import outland.emr.tracking.models.redis.Gateway;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import outland.emr.tracking.models.redis.RecordState;

import java.util.Map;

@Repository
public class RedisGatewayRepository implements IRedisGatewayRepository {
    private final static String table = "Gateway";

    private final RedisTemplate<String, Gateway> redisTemplate;
    private final HashOperations hashOperations;

    public RedisGatewayRepository(RedisTemplate<String, Gateway> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public Gateway findByMac(String mac) {
        return (Gateway) hashOperations.get(table, mac);
    }

    @Override
    public void add(Gateway gateway) {
        hashOperations.put(table, gateway.getMac(), gateway);
    }

    @Override
    public void flush() {
        Map<String, RecordState> records = hashOperations.entries(table);
        records.keySet().forEach(key -> hashOperations.delete(table, key));
    }
}
