package outland.emr.tracking.managers;

import outland.emr.tracking.config.TrackingConfProperties;
import outland.emr.tracking.logic.ProcesserThreads;
import outland.emr.tracking.models.Detection;
import outland.emr.tracking.models.DetectionType;
import outland.emr.tracking.models.mongo.Reading;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.kinesis.exceptions.InvalidStateException;
import software.amazon.kinesis.exceptions.ShutdownException;
import software.amazon.kinesis.lifecycle.events.*;
import software.amazon.kinesis.processor.ShardRecordProcessor;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

@Component
public class KinesisManager implements ShardRecordProcessor {
    private static final String SHARD_ID_MDC_KEY = "ShardId";
    private static String shardId = "";
    private static final Logger logger = LoggerFactory.getLogger(KinesisManager.class);

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

            var processerThreads = ProcesserThreads.getSingleton();
            processerThreads.run();

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
