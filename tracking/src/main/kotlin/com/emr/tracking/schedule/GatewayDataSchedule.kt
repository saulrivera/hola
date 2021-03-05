package com.emr.tracking.schedule

import com.emr.tracking.configuration.AppProperties
import com.emr.tracking.manager.TracingManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.*
import kotlin.concurrent.schedule

@Component
class GatewayDataSchedule(
    private val tracingManager: TracingManager,
    private val appProperties: AppProperties
) {
    @Bean
    fun main() {
        Timer().schedule(0, appProperties.appTracingFrequency.toLong()) {
            GlobalScope.launch {
                tracingManager.traceBeacons()
            }
        }
    }
}