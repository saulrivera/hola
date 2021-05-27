package com.emr.tracing.managers;

import com.emr.tracing.models.BeaconType;
import com.emr.tracing.models.socket.PatientStream;
import com.emr.tracing.models.socket.Stream;
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
    private final Map<BeaconType, Map<String, Stream>> streamHistory = new ConcurrentHashMap<>();
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

        return new PatientStream(
                recordState.getTrackingMac(),
                recordState.getRssi(),
                recordState.getCalibratedRssi1m(),
                recordState.getType(),
                recordState.getGatewayMac(),
                gateway.getLabel(),
                gateway.getFloor(),
                gateway.getCoordinateX(),
                gateway.getCoordinateY(),
                patient
        );
    }

    public Map<BeaconType, List<Stream>> getActiveStreams() {
        var dictionary = new HashMap<BeaconType, List<Stream>>();
        streamHistory.forEach((key, value) -> {
            dictionary.put(key, new ArrayList<>(value.values()));
        });
        return dictionary;
    }

    public void removeStreamFromHistory(String beaconMac) {
        streamHistory.remove(beaconMac);
    }

    public void add(Stream stream) {
        var streamHistoryForKey = streamHistory.getOrDefault(stream.getType(), new ConcurrentHashMap<>());
        streamHistoryForKey.put(stream.getMac(), stream);

        streamHistory.put(stream.getType(), streamHistoryForKey);
        streamQueue.put(stream.getMac(), stream);
    }

    public List<Stream> getStreams() {
        return new ArrayList<>(streamQueue.values());
    }

    public void clearStreamStack() {
        streamQueue.clear();
    }

    public void sendPatientAlert(PatientStream stream) {
        String patientId = stream.getPatient().getId();
        if (alertPatientQueue.containsKey(patientId)) {
            LocalDateTime lastUpdate = alertPatientQueue.get(patientId);
            if (ChronoUnit.MINUTES.between(lastUpdate, LocalDateTime.now()) < 1) return;
        }

        alertPatientQueue.put(patientId, LocalDateTime.now());
        notificationSocket.emitBeaconAlert(stream);
    }
}
