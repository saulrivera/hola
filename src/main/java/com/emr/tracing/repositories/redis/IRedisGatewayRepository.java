package com.emr.tracing.repositories.redis;

import com.emr.tracing.models.redis.Gateway;

public interface IRedisGatewayRepository {
    Gateway findByMac(String mac);
}
