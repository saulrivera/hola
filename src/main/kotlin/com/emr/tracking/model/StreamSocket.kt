package com.emr.tracking.model

data class StreamSocket(
    val mac: String,
    val rssi: Double,
    val calibratedRssi1m: Double,
    val gateway: StreamSocketGateway,
    val patient: Patient
)

data class StreamSocketGateway(
    val id: String,
    val coordinates: List<Double>
)