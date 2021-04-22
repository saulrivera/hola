package com.emr.tracing.repositories.redis;

import com.emr.tracing.models.redis.Beacon;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisBeaconRepository implements IRedisBeaconRepository {
    private final RedisTemplate<String, Beacon> redisTemplate;
    private final HashOperations hashOperations;

    public RedisBeaconRepository(RedisTemplate<String, Beacon> redisTemplate) {
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public boolean isBeaconPresent(String mac) {
        var value = (Beacon)hashOperations.get("USER", mac);
        return value != null;
    }
}
