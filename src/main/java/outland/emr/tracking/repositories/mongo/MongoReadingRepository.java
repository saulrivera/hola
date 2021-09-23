package outland.emr.tracking.repositories.mongo;

import outland.emr.tracking.models.mongo.Reading;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoReadingRepository extends MongoRepository<Reading, String> {
}
