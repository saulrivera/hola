package com.emr.tracking.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable
import javax.annotation.Generated

@RedisHash("StreamReading")
data class RedisStreamReading(
    @Id
    var trackingId: String,
    var gatewayId: String,
    var rssi: Double,
    var calibratedRssi1m: Double,
    var gatewayHistories: MutableMap<String, RedisGatewayParameters>
) : Serializable