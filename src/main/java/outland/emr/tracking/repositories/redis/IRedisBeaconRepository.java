package outland.emr.tracking.repositories.redis;

import outland.emr.tracking.models.redis.Beacon;

public interface IRedisBeaconRepository {
    boolean isBeaconPresent(String mac);
    Beacon findBeaconByMac(String mac);
    void add(Beacon beacon);
}
