package outland.emr.tracking.repositories.redis;

import outland.emr.tracking.models.mongo.Reading;
import outland.emr.tracking.models.redis.Beacon;
import outland.emr.tracking.models.redis.RecordState;

public interface IRedisRecordStateRepository {
    void flush();
    RecordState findOrCreate(Reading stream, Beacon beacon);
    RecordState findByBeaconMac(String beaconMac);
    void update(RecordState recordState);
}
