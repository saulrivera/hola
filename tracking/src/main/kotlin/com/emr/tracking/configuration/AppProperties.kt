package com.emr.tracking.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppProperties {
    @Value("\${application.name}")
    lateinit var applicationName: String

    @Value("\${redis.host}")
    lateinit var redisHost: String

    @Value("\${redis.port}")
    lateinit var redisPort: String

    @Value("\${kontakt.io.apiKey}")
    lateinit var kontaktApiKey: String

    @Value("\${app.tracing.frequency}")
    lateinit var appTracingFrequency: String

    @Value("\${app.tracing.kalmanfilter.r}")
    lateinit var appTracingKalmanFilterR: String

    @Value("\${app.tracing.kalmanfilter.q}")
    lateinit var appTracingKalmanFilterQ: String

    @Value("\${app.tracing.environmentFactor}")
    lateinit var appTracingEnvironmentFactor: String

    @Value("\${aws.region}")
    lateinit var awsRegion: String

    @Value("\${aws.stream_name}")
    lateinit var awsStreamName: String
}