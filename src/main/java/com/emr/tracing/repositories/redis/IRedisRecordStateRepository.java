package com.emr.tracing.repositories.redis;

import com.emr.tracing.models.Reading;
import com.emr.tracing.models.redis.Beacon;
import com.emr.tracing.models.redis.RecordState;

import java.util.List;

public interface IRedisRecordStateRepository {
    void flush();
    RecordState findOrCreate(Reading stream, Beacon beacon);
    RecordState findByBeaconMac(String beaconMac);
    void update(RecordState recordState);
}
