package outland.emr.tracking.repositories.mongo;

import outland.emr.tracking.models.mongo.StaffBeacon;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoStaffBeaconRepository extends MongoRepository<StaffBeacon, String> {
}
