package outland.emr.tracking.logic;

import outland.emr.tracking.models.mongo.Patient;
import outland.emr.tracking.repositories.mongo.MongoPatientRepository;
import outland.emr.tracking.repositories.redis.RedisPatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PatientLogic {
    @Autowired
    private final MongoPatientRepository patientRepository;
    @Autowired
    private final RedisPatientRepository redisPatientRepository;

    public PatientLogic(
            MongoPatientRepository patientRepository,
            RedisPatientRepository redisPatientRepository) {
        this.patientRepository = patientRepository;
        this.redisPatientRepository = redisPatientRepository;
    }

    public boolean isTableEmpty() {
        return !(patientRepository.findAll().size() > 0);
    }

    public List<Patient> getAll() {
        return this.patientRepository.findAll();
    }

    public List<Patient> searchByName(String name) {
        return this.patientRepository.findAll()
                .stream()
                .filter(patient -> patient.fullName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Patient add(Patient patient) {
        patient.setId(UUID.randomUUID().toString());

        patientRepository.save(patient);
        redisPatientRepository.add(createRedisPatient(patient));

        return patient;
    }

    public Patient update(Patient patient) throws Exception {
        Optional<Patient> oldPatientOptional = patientRepository.findById(patient.getId());
        if (oldPatientOptional.isEmpty()) {
            throw new Exception("Patient not found");
        }
        Patient oldPatient = oldPatientOptional.get();

        oldPatient.setFirstName(patient.getFirstName());
        oldPatient.setMiddleName(patient.getMiddleName());
        oldPatient.setLastName(patient.getLastName());
        oldPatient.setRoom(patient.getRoom());
        oldPatient.setContactEmail(patient.getContactEmail());
        oldPatient.setContactPhone(patient.getContactPhone());

        patientRepository.save(oldPatient);
        redisPatientRepository.add(createRedisPatient(oldPatient));

        return oldPatient;
    }

    public void syncWithRedis() {
        patientRepository.findAll()
                .stream()
                .map(this::createRedisPatient)
                .forEach(redisPatientRepository::add);
    }

    private outland.emr.tracking.models.redis.Patient createRedisPatient(Patient patient) {
        return new outland.emr.tracking.models.redis.Patient(
                patient.getId(),
                patient.getFirstName(),
                patient.getMiddleName(),
                patient.getLastName(),
                patient.getRoom()
        );
    }

    public void flushTable() {

    }
}
