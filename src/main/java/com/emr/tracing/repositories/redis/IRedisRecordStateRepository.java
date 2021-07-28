package com.emr.tracing.repositories.redis;

import com.emr.tracing.models.mongo.Reading;
import com.emr.tracing.models.redis.Beacon;
import com.emr.tracing.models.redis.RecordState;

public interface IRedisRecordStateRepository {
    void flush();
    RecordState findOrCreate(Reading stream, Beacon beacon);
    RecordState findByBeaconMac(String beaconMac);
    void update(RecordState recordState);
}
