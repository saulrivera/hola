package com.emr.tracking.model

data class KontaktTelemetryResponse(
    var content: List<KontaktGatewayResponse>
)

data class KontaktGatewayResponse(
    var sourceId: String,
    var trackingId: String,
    var rssi: Int,
    var model: Int,
    var uniqueId: String
)
