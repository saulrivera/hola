package com.emr.tracking.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable

@RedisHash("Gateway")
data class RedisGatewayNode(
    @Id
    val gatewayId: String,
    val siblings: List<String>
) : Serializable