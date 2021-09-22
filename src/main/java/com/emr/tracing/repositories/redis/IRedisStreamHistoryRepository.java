package com.emr.tracing.repositories.redis;

import com.emr.tracing.models.BeaconType;
import com.emr.tracing.models.socket.Stream;

import java.util.List;
import java.util.Map;

public interface IRedisStreamHistoryRepository {
    void removeFromTypeBeacon(BeaconType beaconType, String beaconId);
    void addStreamFromType(BeaconType beaconType, Stream stream);
    Map<BeaconType, List<Stream>> getHistory();
}
