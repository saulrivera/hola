package com.emr.tracing.logic;

import com.emr.tracing.managers.StreamManager;
import com.emr.tracing.models.Stream;
import com.emr.tracing.models.mongo.Beacon;
import com.emr.tracing.models.mongo.PatientBeacon;
import com.emr.tracing.repositories.mongo.MongoBeaconRepository;
import com.emr.tracing.repositories.mongo.MongoPatientBeaconRepository;
import com.emr.tracing.repositories.redis.RedisBeaconRepository;
import com.emr.tracing.repositories.redis.RedisPatientBeaconRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BeaconLogic {
    @Autowired
    private final MongoBeaconRepository beaconRepository;
    @Autowired
    private final MongoPatientBeaconRepository patientBeaconRepository;
    @Autowired
    private final RedisBeaconRepository redisBeaconRepository;
    @Autowired
    private final RedisPatientBeaconRepository redisPatientBeaconRepository;
    @Autowired
    private final StreamManager streamManager;

    public BeaconLogic(
            MongoBeaconRepository beaconRepository,
            MongoPatientBeaconRepository patientBeaconRepository,
            RedisBeaconRepository redisBeaconRepository,
            RedisPatientBeaconRepository redisPatientBeaconRepository,
            StreamManager streamManager
            ) {
        this.beaconRepository = beaconRepository;
        this.patientBeaconRepository = patientBeaconRepository;
        this.redisBeaconRepository = redisBeaconRepository;
        this.redisPatientBeaconRepository = redisPatientBeaconRepository;
        this.streamManager = streamManager;
    }

    public boolean isTableEmpty() {
        return !((long) beaconRepository.findAll().size() > 0);
    }

    public Beacon getBeaconById(String id) {
        Optional<Beacon> beacon = beaconRepository.findById(id);
        return beacon.orElse(null);
    }

    public Beacon getBeaconRelatedToPatientId(String patientId) {
        PatientBeacon patientBeacon = patientBeaconRepository.findByActiveAndPatientId(true, patientId);
        if (patientBeacon == null) {
            return null;
        }
        return getBeaconById(patientBeacon.getBeaconId());
    }

    public List<Stream> getActiveBeaconStreams() {
        return redisPatientBeaconRepository.findAll().values()
                .stream()
                .map(streamManager::createStreamForPatient)
                .collect(Collectors.toList());
    }

    public List<Beacon> getAvailable() {
        List<PatientBeacon> lockedBeacons = patientBeaconRepository.findByActive(true);
        List<String> lockedBeaconIds = lockedBeacons.stream().map(PatientBeacon::getBeaconId)
                .collect(Collectors.toList());
        return beaconRepository.findAll()
                .stream()
                .filter(beacon -> !lockedBeaconIds.contains(beacon.getId()))
                .collect(Collectors.toList());
    }

    public void add(Beacon beacon) {
        beacon.setId(UUID.randomUUID().toString());

        beaconRepository.save(beacon);
        redisBeaconRepository.add(createBeaconRedis(beacon));
    }

    public Stream associate(String patientId, String beaconLabel) {
        Beacon beacon = beaconRepository.findByLabel(beaconLabel);

        PatientBeacon oldRegistry = patientBeaconRepository.findByActiveAndPatientId(true, patientId);

        PatientBeacon patientBeacon = new PatientBeacon(
                patientId,
                beacon.getId(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        patientBeaconRepository.save(patientBeacon);
        var redisPatientBeacon = createPatientBeacon(patientBeacon);
        redisPatientBeaconRepository.save(redisPatientBeacon);

        if (oldRegistry != null) {
            oldRegistry.setActive(false);
            oldRegistry.setUpdatedAt(LocalDateTime.now());
            patientBeaconRepository.save(oldRegistry);
        }

        assert redisPatientBeacon != null;
        return streamManager.createStreamForPatient(redisPatientBeacon);
    }

    public Stream disassociate(String beaconLabel) {
        Beacon beacon = beaconRepository.findByLabel(beaconLabel);
        PatientBeacon patientBeacon = patientBeaconRepository.findByActiveAndBeaconId(true, beacon.getId());

        Stream stream = streamManager.createStreamForPatient(patientBeacon.getPatientId());

        patientBeacon.setActive(false);
        patientBeacon.setUpdatedAt(LocalDateTime.now());
        patientBeaconRepository.save(patientBeacon);

        var redisPatientBeacon = createPatientBeacon(patientBeacon);

        assert redisPatientBeacon != null;
        redisPatientBeacon.setActive(false);

        redisPatientBeaconRepository.save(redisPatientBeacon);

        return stream;
    }

    public void syncWithRedis() {
        beaconRepository.findAll()
                .stream()
                .map(this::createBeaconRedis)
                .forEach(redisBeaconRepository::add);
        patientBeaconRepository.findAll()
                .stream()
                .map(this::createPatientBeacon)
                .forEach(redisPatientBeaconRepository::save);
    }

    private com.emr.tracing.models.redis.Beacon createBeaconRedis(Beacon beacon) {
        return new com.emr.tracing.models.redis.Beacon(
                beacon.getMac(),
                beacon.getLabel(),
                beacon.getType()
        );
    }

    private com.emr.tracing.models.redis.PatientBeacon createPatientBeacon(PatientBeacon patientBeacon) {
        Optional<Beacon> beacon = beaconRepository.findById(patientBeacon.getBeaconId());
        if (beacon.isEmpty()) return null;
        String beaconMac = beacon.get().getMac();
        return new com.emr.tracing.models.redis.PatientBeacon(
                patientBeacon.getPatientId(),
                beaconMac,
                true
        );
    }
}
