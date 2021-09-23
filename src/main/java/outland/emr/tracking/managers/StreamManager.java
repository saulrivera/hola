package outland.emr.tracking.managers;

import outland.emr.tracking.models.BeaconType;
import outland.emr.tracking.models.socket.PatientStream;
import outland.emr.tracking.models.socket.Stream;
import outland.emr.tracking.models.redis.Gateway;
import outland.emr.tracking.models.redis.Patient;
import outland.emr.tracking.models.redis.PatientBeacon;
import outland.emr.tracking.models.redis.RecordState;
import outland.emr.tracking.repositories.redis.*;
import outland.emr.tracking.websockets.NotificationSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
    @Autowired
    private final RedisStreamHistoryRepository streamHistoryRepository;

    public StreamManager(
            RedisGatewayRepository redisGatewayRepository,
            RedisRecordStateRepository redisRecordStateRepository,
            RedisPatientBeaconRepository redisPatientBeaconRepository,
            RedisPatientRepository redisPatientRepository,
            NotificationSocket notificationSocket,
            RedisStreamHistoryRepository streamHistoryRepository
            ) {
        this.redisGatewayRepository = redisGatewayRepository;
        this.redisRecordStateRepository = redisRecordStateRepository;
        this.redisPatientBeaconRepository = redisPatientBeaconRepository;
        this.redisPatientRepository = redisPatientRepository;
        this.notificationSocket = notificationSocket;
        this.streamHistoryRepository = streamHistoryRepository;
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
        return streamHistoryRepository.getHistory();
    }

    public void removeStreamFromHistory(String beaconMac) {
        streamHistoryRepository.removeFromTypeBeacon(BeaconType.PATIENT, beaconMac);
    }

    public void add(Stream stream) {
        streamHistoryRepository.addStreamFromType(stream.getType(), stream);
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
