package com.emr.tracking.model

data class KontaktDeviceResponse(
    var devices: List<KontaktDevice>
)

data class KontaktDevice(
    var id: String,
    var uniqueId: String
)