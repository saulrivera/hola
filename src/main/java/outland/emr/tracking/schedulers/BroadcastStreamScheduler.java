package outland.emr.tracking.schedulers;

import org.joda.time.DateTime;
import outland.emr.tracking.managers.StreamManager;
import outland.emr.tracking.managers.ThreadManager;
import outland.emr.tracking.websockets.TrackingSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class BroadcastStreamScheduler {
    private final Logger logger = LoggerFactory.getLogger(BroadcastStreamScheduler.class);
    @Autowired
    private final StreamManager streamManager;
    @Autowired
    private final TrackingSocket trackingSocket;
    @Autowired
    private final ThreadManager threadManager;

    public BroadcastStreamScheduler(StreamManager streamManager, TrackingSocket trackingSocket, ThreadManager threadManager) {
        this.streamManager = streamManager;
        this.trackingSocket = trackingSocket;
        this.threadManager = threadManager;
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

        Date dt = DateTime.now().minusMillis(1000).toDate();
        streamQueue
            .stream()
            .filter(it -> it.getTimestamp().after(dt))
            .forEach(it -> {
                try {
                    trackingSocket.broadcastTracking(it);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        streamManager.clearStreamStack();
    }

    @Scheduled(fixedRate = 3000)
    public void flushThreads() {
        threadManager.flushThreads();
    }
}
