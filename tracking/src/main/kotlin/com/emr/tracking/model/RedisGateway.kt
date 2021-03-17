package com.emr.tracking.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable

@RedisHash("Gateway")
data class RedisGateway(
    @Id
    val gatewayId: String,
    val position: Pair<Double, Double>,
    val siblings: List<String>,
    val floor: Int
) : Serializable