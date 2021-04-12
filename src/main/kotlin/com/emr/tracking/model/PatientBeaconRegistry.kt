package com.emr.tracking.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime
import java.util.*

data class PatientBeaconRegistry(
    @Id
    @Field("_id")
    val id: String,
    var patientId: String,
    var beaconId: String,
    var active: Boolean,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
)
