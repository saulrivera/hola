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
import com.emr.tracing.websockets.TracingSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StreamManager {
    private volatile Map<String, Stream> streamQueue = new HashMap<>();

    @Autowired
    private final RedisGatewayRepository redisGatewayRepository;
    @Autowired
    private final RedisRecordStateRepository redisRecordStateRepository;
    @Autowired
    private final RedisPatientBeaconRepository redisPatientBeaconRepository;
    @Autowired
    private final RedisPatientRepository redisPatientRepository;
    @Autowired
    private final TracingSocket tracingSocket;

    public StreamManager(
            RedisGatewayRepository redisGatewayRepository,
            RedisRecordStateRepository redisRecordStateRepository,
            RedisPatientBeaconRepository redisPatientBeaconRepository,
            RedisPatientRepository redisPatientRepository,
            TracingSocket tracingSocket
            ) {
        this.redisGatewayRepository = redisGatewayRepository;
        this.redisRecordStateRepository = redisRecordStateRepository;
        this.redisPatientBeaconRepository = redisPatientBeaconRepository;
        this.redisPatientRepository = redisPatientRepository;
        this.tracingSocket = tracingSocket;
    }

    public Stream createStreamForPatient(Patient patient) {
        PatientBeacon patientBeacon = redisPatientBeaconRepository.findByActiveAndPatientId(patient.getId());
        if (patientBeacon == null) return null;
        return createStreamForPatient(patient, patientBeacon);
    }

    public Stream createStreamForPatient(Patient patient, PatientBeacon patientBeacon) {
        RecordState recordState = redisRecordStateRepository.findByBeaconMac(patientBeacon.getBeaconId());
        if (recordState == null) return null;
        Gateway gateway = redisGatewayRepository.findByMac(recordState.getGatewayMac());
        if (gateway == null) return null;

        return new Stream(
                recordState.getTrackingMac(),
                recordState.getRssi(),
                recordState.getCalibratedRssi1m(),
                recordState.getGatewayMac(),
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
            return createStreamForPatient(patient);
        }).collect(Collectors.toList());
    }

    public void add(Stream stream) {
        streamQueue.put(stream.getMac(), stream);
    }

    public void broadcastStreams() {
        streamQueue.values().forEach(it -> {
            try {
                tracingSocket.broadcastTracking(it);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        streamQueue.clear();
    }
}
