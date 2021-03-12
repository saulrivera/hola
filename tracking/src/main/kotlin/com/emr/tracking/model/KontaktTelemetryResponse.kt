package com.emr.tracking.model

data class KontaktTelemetryResponse(
    var trackingId: String,
    var sourceId: String,
    var rssi: Double,
    var calibratedRssi1m: Double,
    var ibeaconProximity: String?
)
