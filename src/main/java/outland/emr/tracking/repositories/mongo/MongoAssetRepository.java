package outland.emr.tracking.repositories.mongo;

import outland.emr.tracking.models.mongo.Asset;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoAssetRepository extends MongoRepository<Asset, String> {
}
