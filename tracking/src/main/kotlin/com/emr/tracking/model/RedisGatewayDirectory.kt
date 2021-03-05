package com.emr.tracking.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("GatewayDirectory")
data class RedisGatewayDirectory(
    @Id
    var uniqueId: String
)