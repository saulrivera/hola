package outland.emr.tracking.managers;

import org.joda.time.DateTime;
import outland.emr.tracking.config.TrackingConfProperties;
import outland.emr.tracking.models.DetectionType;
import outland.emr.tracking.models.mongo.Reading;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import outland.emr.tracking.models.mongo.Record;
import software.amazon.kinesis.exceptions.InvalidStateException;
import software.amazon.kinesis.exceptions.ShutdownException;
import software.amazon.kinesis.lifecycle.events.*;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class KinesisManager implements ShardRecordProcessor {
    private static final String SHARD_ID_MDC_KEY = "ShardId";
    private static String shardId = "";
    private static final Logger logger = LoggerFactory.getLogger(KinesisManager.class);

    @Autowired
    private final TrackingManager trackingManager;
    @Autowired
    private final TrackingConfProperties trackingConfProperties;
    @Autowired
    private final ThreadManager threadManager;

    public KinesisManager(TrackingManager trackingManager, TrackingConfProperties trackingConfProperties, ThreadManager threadManager) {
        this.trackingManager = trackingManager;
        this.trackingConfProperties = trackingConfProperties;
        this.threadManager = threadManager;
    }

    @Override
    public void initialize(InitializationInput initializationInput) {
        if (initializationInput != null) {
            shardId = initializationInput.shardId();
            MDC.put(SHARD_ID_MDC_KEY, shardId);
            try {
                logger.info("Initializing @ sequence: " + initializationInput.extendedSequenceNumber());
            } finally {
                MDC.remove(SHARD_ID_MDC_KEY);
            }
        }
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        if (processRecordsInput != null) {
            MDC.put(SHARD_ID_MDC_KEY, shardId);
            logger.info("Processing " + processRecordsInput.records().size());

            for(int i = 0; i < processRecordsInput.records().size(); i++) {
                KinesisClientRecord record = processRecordsInput.records().get(i);
                var thread  = new Thread(() -> {
                    CharBuffer originalData = StandardCharsets.UTF_8.decode(record.data());
                    try {
                        Reading[] responses = new ObjectMapper().readValue(originalData.toString(), Reading[].class);

                        Date dt = DateTime.now().minusMillis(1000).toDate();
                        var filteredResponses = Arrays.stream(responses)
                                .filter(it -> it.getTimestamp().after(dt))
                                .collect(Collectors.toList());

                        Optional<Reading> gatewayDetection = Arrays.stream(responses).filter(it -> it.getType().equals(DetectionType.Gateway)).findFirst();

                        if (gatewayDetection.isEmpty()) {
                            return;
                        }

                        filteredResponses.stream().filter(it -> it.getType().equals(DetectionType.iBeacon)).forEach(reading -> {
                            reading.setGatewayMac(gatewayDetection.get().getTrackingMac());

                            try {
                                if (reading.getUuid().equals(trackingConfProperties.getBeaconAlertId())) {
                                    trackingManager.alertEmittedBy(reading);
                                } else {
                                    trackingManager.processBeaconStream(reading);
                                }
                            } catch (Exception e) {
                                logger.error(e.getMessage());
                            }
                        });
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
                threadManager.addToThread(thread);
            }
            MDC.remove(SHARD_ID_MDC_KEY);
        }
    }

    @Override
    public void leaseLost(LeaseLostInput leaseLostInput) {
        MDC.put(SHARD_ID_MDC_KEY, shardId);
        try {
            logger.info("Lost lease, so terminating " + leaseLostInput);
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY);
        }
    }

    @Override
    public void shardEnded(ShardEndedInput shardEndedInput) {
        MDC.put(SHARD_ID_MDC_KEY, shardId);
        try {
            logger.info("Reached shard end checkpoint.");
            shardEndedInput.checkpointer().checkpoint();
        } catch (ShutdownException | InvalidStateException e) {
            logger.error("Exception while checkpointing at shard end. Giving up. " + e);
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY);
        }
    }

    @Override
    public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
        MDC.put(SHARD_ID_MDC_KEY, shardId);
        try {
            logger.info("Scheduler is shutting down, checkpointing.");
            shutdownRequestedInput.checkpointer().checkpoint();
        } catch (ShutdownException | InvalidStateException e) {
            logger.error("Exception while checkpointing at requested shutdown. Giving up. " + e);
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY);
        }
    }
}
