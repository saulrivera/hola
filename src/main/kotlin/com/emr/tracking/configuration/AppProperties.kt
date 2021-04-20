package com.emr.tracking.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppProperties {
    @Value("\${application.name}")
    lateinit var applicationName: String

    @Value("\${app.tracing.frequency}")
    lateinit var appTracingFrequency: String

    @Value("\${app.tracing.kalmanfilter.r}")
    lateinit var appTracingKalmanFilterR: String

    @Value("\${app.tracing.kalmanfilter.q}")
    lateinit var appTracingKalmanFilterQ: String

    @Value("\${app.tracing.environmentFactor}")
    lateinit var appTracingEnvironmentFactor: String

    @Value("\${app.tracing.thresholdBeaconChange}")
    lateinit var appTracingThresholdBeaconChange: String

    @Value("\${aws.region}")
    lateinit var awsRegion: String

    @Value("\${aws.stream_name}")
    lateinit var awsStreamName: String

    @Value("\${rethink.host}")
    lateinit var rethinkHost: String

    @Value("\${rethink.port}")
    lateinit var rethinkPort: String

    @Value("\${rethink.database}")
    lateinit var rethinkDatabase: String
}