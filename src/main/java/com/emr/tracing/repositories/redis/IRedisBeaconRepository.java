package com.emr.tracing.repositories.redis;

import com.emr.tracing.models.redis.Beacon;

import java.util.Map;

public interface IRedisBeaconRepository {
    boolean isBeaconPresent(String mac);
    void add(Beacon beacon);
}
