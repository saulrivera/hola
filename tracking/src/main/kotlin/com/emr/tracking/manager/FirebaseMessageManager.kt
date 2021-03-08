package com.emr.tracking.manager

import com.emr.tracking.configuration.AppProperties
import com.emr.tracking.model.RedisStreamReading
import com.emr.tracking.repository.RedisStreamRepository
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import org.springframework.stereotype.Component

@Component
class FirebaseMessageManager(
    private val appProperties: AppProperties,
    private val redisStreamRepository: RedisStreamRepository
) {
    fun publishReadingUpdates(readings: List<RedisStreamReading>) {
        if (readings.count() == 0)
            return

        val transformedReading = readings.map { it.deviceId to it.gatewayId }.toMap()
        val message = Message.builder()
            .putAllData(transformedReading)
            .setTopic(appProperties.firebaseTopic)
            .build()

        FirebaseMessaging.getInstance().send(message)
    }
}