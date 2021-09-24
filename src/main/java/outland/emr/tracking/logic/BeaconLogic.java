package outland.emr.tracking.logic;

import outland.emr.tracking.managers.StreamManager;
import outland.emr.tracking.models.socket.PatientStream;
import outland.emr.tracking.models.socket.Stream;
import outland.emr.tracking.models.mongo.Beacon;
import outland.emr.tracking.models.mongo.PatientBeacon;
import outland.emr.tracking.repositories.mongo.MongoBeaconRepository;
import outland.emr.tracking.repositories.mongo.MongoPatientBeaconRepository;
import outland.emr.tracking.repositories.redis.RedisBeaconRepository;
import outland.emr.tracking.repositories.redis.RedisPatientBeaconRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import outland.emr.tracking.repositories.redis.RedisStreamHistoryRepository;

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
    @Autowired
    private final RedisStreamHistoryRepository streamHistoryRepository;

    public BeaconLogic(
            MongoBeaconRepository beaconRepository,
            MongoPatientBeaconRepository patientBeaconRepository,
            RedisBeaconRepository redisBeaconRepository,
            RedisPatientBeaconRepository redisPatientBeaconRepository,
            StreamManager streamManager,
            RedisStreamHistoryRepository streamHistoryRepository
            ) {
        this.beaconRepository = beaconRepository;
        this.patientBeaconRepository = patientBeaconRepository;
        this.redisBeaconRepository = redisBeaconRepository;
        this.redisPatientBeaconRepository = redisPatientBeaconRepository;
        this.streamManager = streamManager;
        this.streamHistoryRepository = streamHistoryRepository;
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
        redisPatientBeaconRepository.add(redisPatientBeacon);

        if (oldRegistry != null) {
            oldRegistry.setActive(false);
            oldRegistry.setUpdatedAt(LocalDateTime.now());
            patientBeaconRepository.save(oldRegistry);
        }

        assert redisPatientBeacon != null;

        Stream patientStream = streamHistoryRepository.findPatientHistoryByBeaconMac(beacon.getMac());
        if (patientStream != null) {
            streamManager.add(patientStream);
        }

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

        redisPatientBeaconRepository.add(redisPatientBeacon);

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
                .forEach(redisPatientBeaconRepository::add);
    }

    private outland.emr.tracking.models.redis.Beacon createBeaconRedis(Beacon beacon) {
        return new outland.emr.tracking.models.redis.Beacon(
                beacon.getMac(),
                beacon.getLabel(),
                beacon.getType()
        );
    }

    private outland.emr.tracking.models.redis.PatientBeacon createPatientBeacon(PatientBeacon patientBeacon) {
        Optional<Beacon> beacon = beaconRepository.findById(patientBeacon.getBeaconId());
        if (beacon.isEmpty()) return null;
        String beaconMac = beacon.get().getMac();
        return new outland.emr.tracking.models.redis.PatientBeacon(
                patientBeacon.getPatientId(),
                beaconMac,
                true
        );
    }
}
