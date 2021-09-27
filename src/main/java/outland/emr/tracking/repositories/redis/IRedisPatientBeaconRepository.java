package outland.emr.tracking.repositories.redis;

import outland.emr.tracking.models.redis.PatientBeacon;

import java.util.List;
import java.util.Map;

public interface IRedisPatientBeaconRepository {
    PatientBeacon findByActiveAndBeaconMac(String mac);
    PatientBeacon findByActiveAndPatientId(String patientID);
    List<PatientBeacon> findByActive();
    Map<String, PatientBeacon> findAll();
    void add(PatientBeacon patientBeacon);
    void deleteByBeaconMac(String beaconId);
    void flush();
}
