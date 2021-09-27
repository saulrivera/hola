package outland.emr.tracking.repositories.redis;

import outland.emr.tracking.models.redis.Patient;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import outland.emr.tracking.models.redis.RecordState;

import java.util.Map;

@Repository
public class RedisPatientRepository implements IRedisPatientRepository {
    private final static String table = "Patient";

    private final RedisTemplate<String, Patient> redisTemplate;
    private final HashOperations hashOperations;

    public RedisPatientRepository(RedisTemplate<String, Patient> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public Patient findById(String id) {
        return (Patient) hashOperations.get(table, id);
    }

    @Override
    public void add(Patient patient) {
        hashOperations.put(table, patient.getId(), patient);
    }

    @Override
    public void flush() {
        Map<String, RecordState> records = hashOperations.entries(table);
        records.keySet().forEach(key -> hashOperations.delete(table, key));
    }
}
