package com.emr.tracing.managers;

import com.emr.tracing.config.TracingConfProperties;
import com.emr.tracing.models.Reading;
import com.emr.tracing.models.Stream;
import com.emr.tracing.models.redis.*;
import com.emr.tracing.repositories.redis.*;
import com.emr.tracing.utils.KalmanFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class TracingManager {
    @Autowired
    private final TracingConfProperties _tracingConfigProperties;
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

    public TracingManager(
            TracingConfProperties tracingConfProperties,
            RedisBeaconRepository redisBeaconRepository,
            RedisPatientBeaconRepository redisPatientBeaconRepository,
            RedisRecordStateRepository redisRecordStateRepository,
            RedisGatewayRepository redisGatewayRepository,
            RedisPatientRepository redisPatientRepository
    ) {
        _tracingConfigProperties = tracingConfProperties;
        _redisBeaconRepository = redisBeaconRepository;
        _redisPatientBeaconRepository = redisPatientBeaconRepository;
        _redisRecordStateRepository = redisRecordStateRepository;
        _redisGatewayRepository = redisGatewayRepository;
        _redisPatientRepository = redisPatientRepository;
    }

    public void processBeaconStream(Reading reading) throws Exception {
        if (!_redisBeaconRepository.isBeaconPresent(reading.getTrackingMac())) {
            throw new Exception("Beacon doesn't exist");
        }

        PatientBeacon patientBeacon = _redisPatientBeaconRepository.findByActiveAndBeaconMac(reading.getTrackingMac());
        if (patientBeacon == null) {
            throw new Exception("Beacon has not been registered for tracing");
        }

        RecordState recordState = _redisRecordStateRepository.findOrCreate(reading);

        String lastGatewayMac = recordState.getGatewayMac();
        Gateway lastGateway = _redisGatewayRepository.findByMac(lastGatewayMac);
        if (lastGateway == null) {
            throw new Exception("Gateway from incoming reading is not found.");
        }
        List<String> nearMacGateways = lastGateway.getSiblings();

        if (!nearMacGateways.contains(reading.getGatewayMac())) {
            throw new Exception("Gateways is far from last seen source.");
        }

        KalmanFilter kalmanFilter;
        if (recordState.getGatewayParameters().isEmpty()) {
            kalmanFilter = new KalmanFilter(
                    _tracingConfigProperties.getrKalmanFilter(),
                    _tracingConfigProperties.getqKalmanFilter()
            );
        } else {
            RecordStateGatewayParameters savedParameters = recordState.getGatewayParameters().get(reading.getGatewayMac());
            if (savedParameters != null) {
                kalmanFilter = new KalmanFilter(
                        _tracingConfigProperties.getrKalmanFilter(),
                        _tracingConfigProperties.getqKalmanFilter(),
                        savedParameters.getA(),
                        savedParameters.getB(),
                        savedParameters.getC(),
                        savedParameters.getCov(),
                        savedParameters.getX()
                );
            } else {
                kalmanFilter = new KalmanFilter(
                        _tracingConfigProperties.getrKalmanFilter(),
                        _tracingConfigProperties.getqKalmanFilter()
                );
            }
        }

        double clearedRssi = kalmanFilter.filter(reading.getRssi());

        if (Math.abs(clearedRssi - recordState.getRssi()) < Math.abs(recordState.getRssi() * _tracingConfigProperties.getThresholdSignal())) {
            throw new Exception("Received signal strength not enough to consider change.");
        }

        reading.setRssi(clearedRssi);

        RecordStateGatewayParameters updatedParameters = new RecordStateGatewayParameters(
                kalmanFilter.getA(),
                kalmanFilter.getB(),
                kalmanFilter.getC(),
                kalmanFilter.getCov(),
                kalmanFilter.getX()
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

        boolean needsBroadcast = !minimumReading.getKey().equals(recordState.getGatewayMac());

        recordState.setRssi(minimumReading.getValue().getX());
        recordState.setGatewayMac(minimumReading.getKey());
        recordState.setCalibratedRssi1m(reading.getCalibratedRssi1m());

        if (needsBroadcast) {
            Gateway gateway = _redisGatewayRepository.findByMac(recordState.getGatewayMac());
            Patient patient = _redisPatientRepository.findById(patientBeacon.getPatientId());

            Stream stream = new Stream(
                    recordState.getTrackingMac(),
                    recordState.getRssi(),
                    recordState.getCalibratedRssi1m(),
                    recordState.getGatewayMac(),
                    gateway.getFloor(),
                    gateway.getCoordinateX(),
                    gateway.getCoordinateY(),
                    patient
            );

            //TODO: Create broker to send message
        }

        _redisRecordStateRepository.update(recordState);
    }
}
