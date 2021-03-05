package com.emr.tracking.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable

@RedisHash("StreamReading")
data class RedisStreamReading(
    @Id
    var deviceId: String,
    var gatewayId: String,
    var rssi: Int
) : Serializable
