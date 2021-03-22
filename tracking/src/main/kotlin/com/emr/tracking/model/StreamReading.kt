package com.emr.tracking.model

import org.springframework.data.annotation.Id
import java.io.Serializable
import java.util.*

data class StreamReading(
    var trackingId: String,
    var gatewayId: String,
    var rssi: Double,
    var calibratedRssi1m: Double,
    var gatewayHistories: MutableMap<String, GatewayParameters>
) : Serializable

data class GatewayParameters(
    var a: Double,
    var b: Double,
    var c: Double,
    var cov: Double,
    var x: Double,
    var rssi: Double
) : Serializable
