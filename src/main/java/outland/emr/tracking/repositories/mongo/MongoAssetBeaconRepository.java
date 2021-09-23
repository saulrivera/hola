package outland.emr.tracking.repositories.mongo;

import outland.emr.tracking.models.mongo.AssetBeacon;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoAssetBeaconRepository extends MongoRepository<AssetBeacon, String> {
}
