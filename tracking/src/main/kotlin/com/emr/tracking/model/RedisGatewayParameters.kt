package com.emr.tracking.model

import java.io.Serializable

data class RedisGatewayParameters(
    var a: Double,
    var b: Double,
    var c: Double,
    var cov: Double,
    var x: Double
) : Serializable
