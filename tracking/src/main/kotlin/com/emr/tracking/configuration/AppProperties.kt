package com.emr.tracking.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppProperties {
    @Value("\${redis.host}")
    lateinit var redisHost: String

    @Value("\${redis.port}")
    lateinit var redisPort: String

    @Value("\${kontakt.io.apiKey}")
    lateinit var kontaktApiKey: String

    @Value("\${app.tracing.frequency}")
    lateinit var appTracingFrequency: String
}