package com.emr.tracking.model

import java.util.*

data class StreamSocket(
    val mac: String,
    val rssi: Double,
    val calibratedRssi1m: Double,
    val gateway: StreamSocketGateway
)

data class StreamSocketGateway(
    val gatewayId: String,
    val coordinateX: Double,
    val coordinateY: Double,
    val floor: Long
)