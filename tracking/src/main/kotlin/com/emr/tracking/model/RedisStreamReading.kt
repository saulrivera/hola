package com.emr.tracking.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable

@RedisHash("StreamReading")
data class RedisStreamReading(
    @Id
    var deviceId: String,
    var gatewayId: String,
    var rssi: Double,
    var gatewayHistories: Map<String, RedisGatewayParameters>
) : Serializable

data class RedisGatewayParameters(
    var a: Double,
    var b: Double,
    var c: Double,
    var cov: Double,
    var x: Double
) : Serializable
