package com.emr.tracking.model

import org.springframework.data.annotation.Id

data class Beacon(
    @Id
    val trackingId: String,
    val uniqueId: String
)