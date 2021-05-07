package com.emr.tracing.managers;

import com.emr.tracing.models.Stream;
import com.emr.tracing.models.redis.Gateway;
import com.emr.tracing.models.redis.Patient;
import com.emr.tracing.models.redis.PatientBeacon;
import com.emr.tracing.models.redis.RecordState;
import com.emr.tracing.repositories.redis.RedisGatewayRepository;
import com.emr.tracing.repositories.redis.RedisPatientBeaconRepository;
import com.emr.tracing.repositories.redis.RedisPatientRepository;
import com.emr.tracing.repositories.redis.RedisRecordStateRepository;
import com.emr.tracing.websockets.NotificationSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class StreamManager {
    private final Map<String, Stream> streamQueue = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> alertPatientQueue = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(StreamManager.class);

    @Autowired
    private final RedisGatewayRepository redisGatewayRepository;
    @Autowired
    private final RedisRecordStateRepository redisRecordStateRepository;
    @Autowired
    private final RedisPatientBeaconRepository redisPatientBeaconRepository;
    @Autowired
    private final RedisPatientRepository redisPatientRepository;
    @Autowired
    private final NotificationSocket notificationSocket;

    public StreamManager(
            RedisGatewayRepository redisGatewayRepository,
            RedisRecordStateRepository redisRecordStateRepository,
            RedisPatientBeaconRepository redisPatientBeaconRepository,
            RedisPatientRepository redisPatientRepository,
            NotificationSocket notificationSocket
            ) {
        this.redisGatewayRepository = redisGatewayRepository;
        this.redisRecordStateRepository = redisRecordStateRepository;
        this.redisPatientBeaconRepository = redisPatientBeaconRepository;
        this.redisPatientRepository = redisPatientRepository;
        this.notificationSocket = notificationSocket;
    }

    public Stream createStreamForPatient(String patientId) {
        PatientBeacon patientBeacon = redisPatientBeaconRepository.findByActiveAndPatientId(patientId);
        if (patientBeacon == null) return null;
        return createStreamForPatient(patientBeacon);
    }

    public Stream createStreamForPatient(PatientBeacon patientBeacon) {
        Patient patient = redisPatientRepository.findById(patientBeacon.getPatientId());
        RecordState recordState = redisRecordStateRepository.findByBeaconMac(patientBeacon.getBeaconMac());
        if (recordState == null) return null;
        Gateway gateway = redisGatewayRepository.findByMac(recordState.getGatewayMac());
        if (gateway == null) return null;

        return new Stream(
                recordState.getTrackingMac(),
                recordState.getRssi(),
                recordState.getCalibratedRssi1m(),
                recordState.getGatewayMac(),
                gateway.getLabel(),
                gateway.getFloor(),
                gateway.getCoordinateX(),
                gateway.getCoordinateY(),
                patient
        );
    }

    public List<Stream> getActiveStreams() {
        List<PatientBeacon> patientBeacons = redisPatientBeaconRepository.findByActive();
        return patientBeacons.stream().map(patientBeacon -> {
            Patient patient = redisPatientRepository.findById(patientBeacon.getPatientId());
            return createStreamForPatient(patient.getId());
        }).collect(Collectors.toList());
    }

    public void add(Stream stream) {
        streamQueue.put(stream.getMac(), stream);
    }

    public List<Stream> getStreams() {
        return new ArrayList<>(streamQueue.values());
    }

    public void clearStreamStack() {
        streamQueue.clear();
    }

    public void sendPatientAlert(Stream stream) {
        String patientId = stream.getPatient().getId();
        if (alertPatientQueue.containsKey(patientId)) {
            LocalDateTime lastUpdate = alertPatientQueue.get(patientId);
            if (ChronoUnit.MINUTES.between(lastUpdate, LocalDateTime.now()) < 1) return;
        }

        alertPatientQueue.put(patientId, LocalDateTime.now());
        notificationSocket.emitBeaconAlert(stream);
    }
}
