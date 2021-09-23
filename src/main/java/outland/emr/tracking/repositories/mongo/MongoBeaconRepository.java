package outland.emr.tracking.repositories.mongo;

import outland.emr.tracking.models.mongo.Beacon;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoBeaconRepository extends MongoRepository<Beacon, String> {
    Beacon findByLabel(String label);
}
