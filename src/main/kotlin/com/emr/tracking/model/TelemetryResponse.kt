package com.emr.tracking.model

import com.google.gson.annotations.SerializedName

data class TelemetryResponse(
    @SerializedName("TimeStamp")
    var timestamp: String,
    @SerializedName("BLEMac(hex)")
    var trackingId: String,
    @SerializedName("DeviceMac(hex)")
    var sourceId: String,
    @SerializedName("RSSI(dBm)")
    var rssi: Double,
    @SerializedName("RSSI@1m(dBm)")
    var calibratedRssi1m: Double
)
