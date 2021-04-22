package com.emr.tracing.repositories.redis;

import com.emr.tracing.models.Reading;
import com.emr.tracing.models.redis.RecordState;

public interface IRedisRecordStateRepository {
    RecordState findOrCreate(Reading stream);
    RecordState findByBeaconMac(String beaconMac);
    void update(RecordState recordState);
}
