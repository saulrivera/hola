package outland.emr.tracking.repositories.mongo;

import outland.emr.tracking.models.mongo.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoPatientRepository extends MongoRepository<Patient, String> {
}
