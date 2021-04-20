package com.emr.tracking.schedule

import com.emr.tracking.manager.StreamManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BroadcastEmitter(
    private val streamManager: StreamManager,
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(BroadcastEmitter::class.java)
    }
    @Scheduled(fixedRate = 1000)
    fun emmit() {
        logger.info("Emitting elements")
        streamManager.broadcastStreams()
    }
}