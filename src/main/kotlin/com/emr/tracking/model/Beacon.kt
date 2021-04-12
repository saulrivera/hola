package com.emr.tracking.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field

data class Beacon(
    @Id
    @Field("_id")
    val mac: String,
    val uniqueId: String
)