package com.emr.tracing.repositories.redis;

import com.emr.tracing.models.BeaconType;
import com.emr.tracing.models.redis.StreamHistory;
import com.emr.tracing.models.socket.Stream;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RedisStreamHistoryRepository implements IRedisStreamHistoryRepository {
    private static final String table = "StreamHistory";
    private final RedisTemplate<String, StreamHistory> redisTemplate;
    private final HashOperations hashOperations;

    public RedisStreamHistoryRepository(RedisTemplate<String, StreamHistory> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void removeFromTypeBeacon(BeaconType beaconType, String beaconId) {
        var streamListByType = (StreamHistory) hashOperations.get(table, beaconType.name());
        streamListByType.getStreams().remove(beaconId);

        hashOperations.put(table, beaconType.name(), streamListByType);
    }

    @Override
    public void addStreamFromType(BeaconType beaconType, Stream stream) {
        var streamListByType = (StreamHistory) hashOperations.get(table, beaconType.name());

        if (streamListByType == null) {
            streamListByType = new StreamHistory();
            streamListByType.setBeaconType(beaconType.name());
        }

        streamListByType.getStreams().put(stream.getMac(), stream);

        hashOperations.put(table, beaconType.name(), streamListByType);
    }

    @Override
    public Map<BeaconType, List<Stream>> getHistory() {
        var dictionary = new HashMap<BeaconType, List<Stream>>();

        StreamHistory assetHistory = (StreamHistory) hashOperations.get(table, BeaconType.ASSET.name());
        StreamHistory patientHistory = (StreamHistory) hashOperations.get(table, BeaconType.PATIENT.name());
        StreamHistory staffHistory = (StreamHistory) hashOperations.get(table, BeaconType.STAFF.name());

        if (assetHistory != null) {
            dictionary.put(BeaconType.ASSET, new ArrayList<>(assetHistory.getStreams().values()));
        }
        if (patientHistory != null) {
            dictionary.put(BeaconType.PATIENT, new ArrayList<>(patientHistory.getStreams().values()));
        }
        if (staffHistory != null) {
            dictionary.put(BeaconType.STAFF, new ArrayList<>(staffHistory.getStreams().values()));
        }

        return dictionary;
    }
}
