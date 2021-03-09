package com.emr.tracking.model

data class KontaktTelemetryResponse(
    var content: List<KontaktGatewayResponse>
)

data class KontaktGatewayResponse(
    var sourceId: String,
    var rssi: Double,
    var calibratedRssi1m: Double,
    var model: Int,
    var uniqueId: String
)
