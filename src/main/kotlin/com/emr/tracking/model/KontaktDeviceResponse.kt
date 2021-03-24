package com.emr.tracking.model

data class KontaktDeviceResponse(
    val devices: List<KontaktDevice>
)

data class KontaktDevice(
    val uniqueId: String,
    val mac: String? = null,
    val properties: KontaktDeviceProperty? = null
)

data class KontaktDeviceProperty(
    val mac: String
)