package outland.emr.tracking.repositories.redis;

import outland.emr.tracking.models.redis.Patient;

public interface IRedisPatientRepository {
    Patient findById(String id);
    void add(Patient patient);
}
