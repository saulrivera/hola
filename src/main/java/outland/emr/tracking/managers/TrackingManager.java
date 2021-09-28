package outland.emr.tracking.managers;

import outland.emr.tracking.config.TrackingConfProperties;
import outland.emr.tracking.models.BeaconType;
import outland.emr.tracking.models.mongo.Reading;
import outland.emr.tracking.models.redis.*;
import outland.emr.tracking.models.socket.AssetStream;
import outland.emr.tracking.models.socket.PatientStream;
import outland.emr.tracking.models.socket.StaffStream;
import outland.emr.tracking.repositories.mongo.MongoReadingRepository;
import outland.emr.tracking.repositories.redis.*;
import outland.emr.tracking.utils.KalmanFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.Collator;
import java.util.*;

@Component
public class TrackingManager {
    @Autowired
    private final TrackingConfProperties _trackingConfigProperties;
    @Autowired
    private final RedisBeaconRepository _redisBeaconRepository;
    @Autowired
    private final RedisPatientBeaconRepository _redisPatientBeaconRepository;
    @Autowired
    private final RedisRecordStateRepository _redisRecordStateRepository;
    @Autowired
    private final RedisGatewayRepository _redisGatewayRepository;
    @Autowired
    private final RedisPatientRepository _redisPatientRepository;
    @Autowired
    private final RedisStaffRepository _redisStaffRepository;
    @Autowired
    private final RedisStaffBeaconRepository _redisStaffBeaconRepository;
    @Autowired
    private final RedisAssetBeaconRepository _redisAssetBeaconRepository;
    @Autowired
    private final RedisAssetRepository _redisAssetRepository;
    @Autowired
    private final MongoReadingRepository _mongoReadingRepository;
    @Autowired
    private final StreamManager _streamManager;

    private static final Logger logger = LoggerFactory.getLogger(TrackingManager.class);

    public TrackingManager(
            TrackingConfProperties trackingConfProperties,
            RedisBeaconRepository redisBeaconRepository,
            RedisPatientBeaconRepository redisPatientBeaconRepository,
            RedisRecordStateRepository redisRecordStateRepository,
            RedisGatewayRepository redisGatewayRepository,
            RedisPatientRepository redisPatientRepository,
            RedisStaffRepository redisStaffRepository,
            RedisStaffBeaconRepository redisStaffBeaconRepository,
            RedisAssetRepository redisAssetRepository,
            RedisAssetBeaconRepository redisAssetBeaconRepository,
            MongoReadingRepository mongoReadingRepository,
            StreamManager streamManager
    ) {
        _trackingConfigProperties = trackingConfProperties;
        _redisBeaconRepository = redisBeaconRepository;
        _redisPatientBeaconRepository = redisPatientBeaconRepository;
        _redisRecordStateRepository = redisRecordStateRepository;
        _redisGatewayRepository = redisGatewayRepository;
        _redisPatientRepository = redisPatientRepository;
        _redisStaffRepository = redisStaffRepository;
        _redisStaffBeaconRepository = redisStaffBeaconRepository;
        _redisAssetBeaconRepository = redisAssetBeaconRepository;
        _redisAssetRepository = redisAssetRepository;
        _mongoReadingRepository = mongoReadingRepository;
        _streamManager = streamManager;
    }

    public void processBeaconStream(Reading reading) throws Exception {
        _mongoReadingRepository.save(reading);

        Beacon beacon = _redisBeaconRepository.findBeaconByMac(reading.getTrackingMac());
        if (beacon == null) {
            System.out.println("Beacon doesn't exist: " + reading.getTrackingMac());
            return;
        }

        PatientBeacon patientBeacon = null;
        if (beacon.getType() == BeaconType.PATIENT) {
            patientBeacon = _redisPatientBeaconRepository.findByActiveAndBeaconMac(reading.getTrackingMac());
            if (patientBeacon == null) {
                System.out.println("Beacon has not been registered for tracing");
                return;
            }
        }

        boolean isPresent = _redisRecordStateRepository.findByBeaconMac(reading.getTrackingMac()) != null;

        RecordState recordState = _redisRecordStateRepository.findOrCreate(reading, beacon);

        Gateway readingGateway = _redisGatewayRepository.findByMac(reading.getGatewayMac());
        String lastGatewayMac = recordState.getGatewayMac();
        Gateway lastGateway = _redisGatewayRepository.findByMac(lastGatewayMac);

        if (lastGateway == null) {
            System.out.println("Gateway from incoming reading is not found.");
            return;
        }

        KalmanFilter kalmanFilter;
        if (recordState.getGatewayParameters().isEmpty()) {
            kalmanFilter = new KalmanFilter(
                    _trackingConfigProperties.getrKalmanFilter(),
                    _trackingConfigProperties.getqKalmanFilter()
            );
        } else {
            RecordStateGatewayParameters savedParameters = recordState.getGatewayParameters().get(reading.getGatewayMac());
            if (savedParameters != null) {
                kalmanFilter = new KalmanFilter(
                        _trackingConfigProperties.getrKalmanFilter(),
                        _trackingConfigProperties.getqKalmanFilter(),
                        savedParameters.getA(),
                        savedParameters.getB(),
                        savedParameters.getC(),
                        savedParameters.getCov(),
                        savedParameters.getX()
                );
            } else {
                kalmanFilter = new KalmanFilter(
                        _trackingConfigProperties.getrKalmanFilter(),
                        _trackingConfigProperties.getqKalmanFilter()
                );
            }
        }

        double clearedRssi = kalmanFilter.filter(computeGatewaySignal(reading.getRssi(), readingGateway));

        if (isPresent && !surpassedThreshold(clearedRssi, recordState.getRssi())) {
            throw new Exception("Received signal strength not enough to consider change.");
        }

        reading.setRssi(clearedRssi);

        RecordStateGatewayParameters updatedParameters = new RecordStateGatewayParameters(
                kalmanFilter.getA(),
                kalmanFilter.getB(),
                kalmanFilter.getC(),
                kalmanFilter.getCov(),
                clearedRssi
        );
        recordState.getGatewayParameters().put(reading.getGatewayMac(), updatedParameters);

        Optional<Map.Entry<String, RecordStateGatewayParameters>> minimumReadingOptional = recordState.getGatewayParameters()
                .entrySet()
                .stream()
                .max(Comparator.comparingDouble(a -> a.getValue().getX()));
        if (minimumReadingOptional.isEmpty()) {
            throw new Exception("Nor closest gateway could be found");
        }

        Map.Entry<String, RecordStateGatewayParameters> minimumReading = minimumReadingOptional.get();

        Collator collator = Collator.getInstance();
        boolean needsBroadcast = !collator.equals(minimumReading.getKey(), recordState.getGatewayMac());

        recordState.setRssi(minimumReading.getValue().getX());

        if (!isPresent || needsBroadcast) {
            recordState.setGatewayMac(minimumReading.getKey());

            Gateway gateway = _redisGatewayRepository.findByMac(recordState.getGatewayMac());

            switch (beacon.getType()) {
                case PATIENT:
                    assert patientBeacon != null;
                    Patient patient = _redisPatientRepository.findById(patientBeacon.getPatientId());

                    PatientStream patientStream = new PatientStream(
                            recordState.getTrackingMac(),
                            recordState.getRssi(),
                            recordState.getType(),
                            minimumReading.getKey(),
                            gateway.getLabel(),
                            gateway.getFloor(),
                            gateway.getCoordinateX(),
                            gateway.getCoordinateY(),
                            patient
                    );
                    _streamManager.add(patientStream);
                    break;
                case STAFF:
                    StaffBeacon staffBeacon = _redisStaffBeaconRepository.findByBeaconMac(beacon.getMac());
                    Staff staff = _redisStaffRepository.findById(staffBeacon.getStaffId());

                    StaffStream staffStream = new StaffStream(
                            recordState.getTrackingMac(),
                            recordState.getRssi(),
                            recordState.getType(),
                            minimumReading.getKey(),
                            gateway.getLabel(),
                            gateway.getFloor(),
                            gateway.getCoordinateX(),
                            gateway.getCoordinateY(),
                            staff
                    );
                    _streamManager.add(staffStream);
                    break;
                case ASSET:
                    AssetBeacon assetBeacon = _redisAssetBeaconRepository.findByBeaconMac(beacon.getMac());
                    Asset asset = _redisAssetRepository.findById(assetBeacon.getAssetId());

                    AssetStream assetStream = new AssetStream(
                            recordState.getTrackingMac(),
                            recordState.getRssi(),
                            recordState.getType(),
                            minimumReading.getKey(),
                            gateway.getLabel(),
                            gateway.getFloor(),
                            gateway.getCoordinateX(),
                            gateway.getCoordinateY(),
                            asset
                    );
                    _streamManager.add(assetStream);
                    break;
            }
        }

        _redisRecordStateRepository.update(recordState);
    }

    public void alertEmittedBy(Reading reading) throws Exception {
        var patientBeacon = _redisPatientBeaconRepository.findByActiveAndBeaconMac(reading.getTrackingMac());
        if (patientBeacon == null) {
            throw new Exception("Patient not related to this beacon");
        }

        RecordState recordState = _redisRecordStateRepository.findByBeaconMac(reading.getTrackingMac());
        if (recordState == null) {
            throw new Exception("Not record found, so patient cannot be localized");
        }

        var patient = _redisPatientRepository.findById(patientBeacon.getPatientId());
        if (patient == null) {
            throw new Exception("Patient not related to this beacon found");
        }
        Gateway gateway = _redisGatewayRepository.findByMac(reading.getGatewayMac());
        if (gateway == null) {
            throw new Exception("Gateway not found");
        }

        PatientStream stream = new PatientStream(
                recordState.getTrackingMac(),
                recordState.getRssi(),
                recordState.getType(),
                recordState.getGatewayMac(),
                gateway.getLabel(),
                gateway.getFloor(),
                gateway.getCoordinateX(),
                gateway.getCoordinateY(),
                patient
        );

        _streamManager.sendPatientAlert(stream);
    }

    private double computeGatewaySignal(double signal, Gateway gateway) {
        return gateway.getA() * signal + gateway.getB();
    }

    private boolean surpassedThreshold(double signal, double lastSignal) {
        return Math.abs(signal - lastSignal) > Math.abs(lastSignal * _trackingConfigProperties.getThresholdSignal());
    }
}
