package com.emr.tracing.repositories.redis;

import com.emr.tracing.models.redis.StaffBeacon;

public interface IRedisStaffBeaconRepository {
    StaffBeacon findByBeaconMac(String mac);
    StaffBeacon findByStaffId(String id);
    void add(StaffBeacon staffBeacon);
}
