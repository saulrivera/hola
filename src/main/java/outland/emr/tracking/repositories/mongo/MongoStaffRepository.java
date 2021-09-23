package outland.emr.tracking.repositories.mongo;

import outland.emr.tracking.models.mongo.Staff;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoStaffRepository extends MongoRepository<Staff, String> {
}
