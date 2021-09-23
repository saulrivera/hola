package outland.emr.tracking.schedulers;

import outland.emr.tracking.managers.StreamManager;
import outland.emr.tracking.websockets.TracingSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BroadcastStreamScheduler {
    private final Logger logger = LoggerFactory.getLogger(BroadcastStreamScheduler.class);
    @Autowired
    private final StreamManager streamManager;
    @Autowired
    private final TracingSocket tracingSocket;

    public BroadcastStreamScheduler(StreamManager streamManager, TracingSocket tracingSocket) {
        this.streamManager = streamManager;
        this.tracingSocket = tracingSocket;
    }

    @Scheduled(fixedRate = 1000)
    public void emit() {
        broadcastStreams();
    }

    public void broadcastStreams() {
        var streamQueue = streamManager.getStreams();
        if(!streamQueue.isEmpty()) {
            logger.info("Emitting elements");
        }
        streamQueue.forEach(it -> {
            try {
                tracingSocket.broadcastTracking(it);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        streamManager.clearStreamStack();
    }
}
