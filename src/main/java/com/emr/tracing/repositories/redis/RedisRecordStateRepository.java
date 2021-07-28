package com.emr.tracing.repositories.redis;

import com.emr.tracing.models.mongo.Reading;
import com.emr.tracing.models.redis.Beacon;
import com.emr.tracing.models.redis.RecordState;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

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
    public void flush() {
        Map<String, RecordState> records = hashOperations.entries(table);
        records.keySet().forEach(key -> hashOperations.delete(table, key));
    }

    @Override
    public RecordState findOrCreate(Reading stream, Beacon beacon) {
        RecordState recordState = findByBeaconMac(stream.getTrackingMac());
        if (recordState != null) return recordState;

        recordState = new RecordState(stream.getTrackingMac(), stream.getGatewayMac(), stream.getRssi(), beacon.getType(), stream.getCalibratedRssi1m());
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
