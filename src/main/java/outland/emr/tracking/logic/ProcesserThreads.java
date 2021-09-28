package outland.emr.tracking.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.jni.Proc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import outland.emr.tracking.config.TrackingConfProperties;
import outland.emr.tracking.managers.StreamManager;
import outland.emr.tracking.managers.TrackingManager;
import outland.emr.tracking.models.DetectionType;
import outland.emr.tracking.models.mongo.Reading;
import software.amazon.kinesis.exceptions.InvalidStateException;
import software.amazon.kinesis.exceptions.ShutdownException;
import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

@Component
public class ProcesserThreads extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ProcesserThreads.class);

    private static ApplicationContext applicationContext;

    private ProcessRecordsInput processRecordsInput;

    @Autowired
    private final TrackingManager trackingManager;
    @Autowired
    private final TrackingConfProperties trackingConfProperties;

    public ProcesserThreads(TrackingManager trackingManager, TrackingConfProperties trackingConfProperties) {
        this.trackingManager = trackingManager;
        this.trackingConfProperties = trackingConfProperties;
    }

    public void setProcessRecordsInput(ProcessRecordsInput processRecordsInput) {
        this.processRecordsInput = processRecordsInput;
    }

    public static ProcesserThreads getSingleton() {
        return applicationContext.getBean(ProcesserThreads.class);
    }

    public void run() {
        processRecordsInput.records().forEach(record -> {
            CharBuffer originalData = StandardCharsets.UTF_8.decode(record.data());
            try {
                Reading[] responses = new ObjectMapper().readValue(originalData.toString(), Reading[].class);
                Optional<Reading> gatewayDetection = Arrays.stream(responses).filter(it -> it.getType().equals(DetectionType.Gateway)).findFirst();

                if (gatewayDetection.isEmpty()) {
                    return;
                }

                Arrays.stream(responses).filter(it -> it.getType().equals(DetectionType.iBeacon)).forEach(reading -> {
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
                processRecordsInput.checkpointer().checkpoint();
            } catch (JsonProcessingException | ShutdownException | InvalidStateException e) {
                e.printStackTrace();
            }
        });
    }
}
