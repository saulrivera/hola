package outland.emr.tracking.logic;

import outland.emr.tracking.repositories.redis.RedisRecordStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecordStateLogic {
    @Autowired
    private final RedisRecordStateRepository redisRecordStateRepository;

    public RecordStateLogic(RedisRecordStateRepository redisRecordStateRepository) {
        this.redisRecordStateRepository = redisRecordStateRepository;
    }

    public void flushTable() {
        redisRecordStateRepository.flush();
    }
}
