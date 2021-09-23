package outland.emr.tracking.repositories.redis;

import outland.emr.tracking.models.redis.StaffBeacon;

public interface IRedisStaffBeaconRepository {
    StaffBeacon findByBeaconMac(String mac);
    StaffBeacon findByStaffId(String id);
    void add(StaffBeacon staffBeacon);
}
