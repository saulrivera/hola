package com.emr.tracing.repositories.redis;

import com.emr.tracing.models.redis.PatientBeacon;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class RedisPatientBeaconRepository implements IRedisPatientBeaconRepository {
    private final static String table = "PatientBeacon";

    private final RedisTemplate<String, PatientBeacon> redisTemplate;
    private final HashOperations hashOperations;

    public RedisPatientBeaconRepository(RedisTemplate<String, PatientBeacon> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public Map<String, PatientBeacon> findAll() {
        return hashOperations.entries(table);
    }

    @Override
    public void save(PatientBeacon patientBeacon) {
        hashOperations.put(table, patientBeacon.getBeaconMac(), patientBeacon);
    }

    @Override
    public void deleteByBeaconMac(String beaconMac) {
        hashOperations.delete(table, beaconMac);
    }

    @Override
    public PatientBeacon findByActiveAndBeaconMac(String mac) {
        var all = findAll();
        var data = all.values()
                .stream()
                .filter(patientBeacon -> patientBeacon.isActive() && patientBeacon.getBeaconMac().equals(mac))
                .findFirst();
        if (data.isEmpty()) return null;
        return data.get();
    }

    @Override
    public PatientBeacon findByActiveAndPatientId(String patientID) {
        var data = findAll().values()
                .stream()
                .filter(patientBeacon -> patientBeacon.isActive() && patientBeacon.getPatientId().equals(patientID))
                .findFirst();
        if (data.isEmpty()) return null;
        return data.get();
    }

    @Override
    public List<PatientBeacon> findByActive() {
        return findAll().values()
                .stream()
                .filter(PatientBeacon::isActive)
                .collect(Collectors.toList());
    }
}
