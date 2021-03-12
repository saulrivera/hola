package com.emr.tracking.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable

@RedisHash("Beacon")
data class RedisBeacon(
    @Id
    var mac: String,
    var uniqueId: String
) : Serializable