package com.emr.tracking.model

data class SocketTracingStream(
    val trackingId: String,
    val gatewayId: String,
    val rssi: Double,
    val calibratedRssi: Double
)