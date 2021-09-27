package outland.emr.tracking.repositories.redis;

import outland.emr.tracking.models.BeaconType;
import outland.emr.tracking.models.socket.Stream;

import java.util.List;
import java.util.Map;

public interface IRedisStreamHistoryRepository {
    void removeFromTypeBeacon(BeaconType beaconType, String beaconId);
    void addStreamFromType(BeaconType beaconType, Stream stream);
    Map<BeaconType, List<Stream>> getHistory();
    void flush();
}
