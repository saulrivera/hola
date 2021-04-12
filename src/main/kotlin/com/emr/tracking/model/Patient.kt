package com.emr.tracking.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*
import javax.annotation.processing.Generated

data class Patient(
    @Id
    @Field("_id")
    var id: String?,
    var firstname: String,
    var middlename: String,
    var lastname: String,
    var room: String,
    var trackingDeviceId: String,
    var contactInfo: PatientContactInfo,
) {
    fun fullName(): String {
        if (this.middlename.isEmpty()) {
            return "${this.firstname} ${this.lastname}".toLowerCase()
        }
        return "${this.firstname} ${this.middlename} ${this.lastname}".toLowerCase()
    }
}

data class PatientContactInfo(
    var phone: String,
    var email: String
)