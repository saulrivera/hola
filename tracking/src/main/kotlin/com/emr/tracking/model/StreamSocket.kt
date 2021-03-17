package com.emr.tracking.model

data class StreamSocket(
    val trackingId: String,
    val rssi: Double,
    val calibratedRssi1m: Double,
    val gateway: StreamSocketGateway
)

data class StreamSocketGateway(
    val gatewayId: String,
    val coordinateX: Double,
    val coordinateY: Double,
    val floor: Int
)