package outland.emr.tracking.repositories.redis;

import outland.emr.tracking.models.redis.Gateway;

public interface IRedisGatewayRepository {
    Gateway findByMac(String mac);
    void add(Gateway gateway);
    void flush();
}
