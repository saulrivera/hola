package com.emr.tracing.schedulers;

import com.emr.tracing.managers.StreamManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BroadcastStream {
    @Autowired
    private final StreamManager streamManager;

    private static final Logger logger = LoggerFactory.getLogger(BroadcastStream.class);

    public BroadcastStream(StreamManager streamManager) {
        this.streamManager = streamManager;
    }

    @Scheduled(fixedRate = 1000)
    public void emit() {
        logger.info("Emitting elements");
        streamManager.broadcastStreams();
    }
}
