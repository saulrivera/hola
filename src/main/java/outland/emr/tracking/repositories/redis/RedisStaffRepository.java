package outland.emr.tracking.repositories.redis;

import outland.emr.tracking.models.redis.RecordState;
import outland.emr.tracking.models.redis.Staff;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class RedisStaffRepository implements IRedisStaffRepository {
    private static final String table = "Staff";
    private final RedisTemplate<String, Staff> redisTemplate;
    private final HashOperations hashOperations;

    public RedisStaffRepository(RedisTemplate<String, Staff> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public Staff findById(String id) {
        return (Staff) hashOperations.get(table, id);
    }

    @Override
    public void add(Staff staff) {
        hashOperations.put(table, staff.getId(), staff);
    }

    @Override
    public void flush() {
        Map<String, RecordState> records = hashOperations.entries(table);
        records.keySet().forEach(key -> hashOperations.delete(table, key));
    }
}
