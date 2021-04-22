package com.emr.tracing.repositories.redis;

import com.emr.tracing.models.Reading;
import com.emr.tracing.models.redis.RecordState;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisRecordStateRepository implements IRedisRecordStateRepository {
    private final static String table = "RecordState";

    private final RedisTemplate<String, RecordState> redisTemplate;
    private final HashOperations hashOperations;

    public RedisRecordStateRepository(RedisTemplate<String, RecordState> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public RecordState findOrCreate(Reading stream) {
        RecordState recordState = findByBeaconMac(stream.getTrackingMac());
        if (recordState != null) return recordState;

        recordState = new RecordState(stream.getTrackingMac(), stream.getGatewayMac(), stream.getRssi(), stream.getCalibratedRssi1m());
        hashOperations.put(table, recordState.getTrackingMac(), recordState);
        return recordState;
    }

    @Override
    public RecordState findByBeaconMac(String beaconMac) {
        return (RecordState) hashOperations.get(table, beaconMac);
    }

    @Override
    public void update(RecordState recordState) {
        hashOperations.put(table, recordState.getTrackingMac(), recordState);
    }
}
