package outland.emr.tracking.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import outland.emr.tracking.models.PersonnelTransport;
import outland.emr.tracking.models.mongo.Staff;
import outland.emr.tracking.models.mongo.StaffKind;
import outland.emr.tracking.repositories.mongo.MongoAssetRepository;
import outland.emr.tracking.repositories.mongo.MongoPatientBeaconRepository;
import outland.emr.tracking.repositories.mongo.MongoPatientRepository;
import outland.emr.tracking.repositories.mongo.MongoStaffRepository;

import java.util.List;

@Component
public class PersonnelManager {
    @Autowired
    private final MongoAssetRepository mongoAssetRepository;
    @Autowired
    private final MongoPatientBeaconRepository mongoPatientBeaconRepository;
    @Autowired
    private final MongoStaffRepository mongoStaffRepository;

    public PersonnelManager(
            MongoAssetRepository mongoAssetRepository,
            MongoPatientBeaconRepository mongoPatientBeaconRepository,
            MongoStaffRepository mongoStaffRepository
    ) {
        this.mongoAssetRepository = mongoAssetRepository;
        this.mongoPatientBeaconRepository = mongoPatientBeaconRepository;
        this.mongoStaffRepository = mongoStaffRepository;
    }

    public PersonnelTransport getPersonnel() {
        long assetCount = mongoAssetRepository.count();

        List<Staff> staffList = mongoStaffRepository.findAll();
        long providersCount = staffList.stream().filter(it -> it.getKind() == StaffKind.PROVIDER).count();
        long nursesCount = staffList.stream().filter(it -> it.getKind() == StaffKind.NURSE).count();
        long hospitalityCount = staffList.stream().filter(it -> it.getKind() == StaffKind.HOSPITALITY).count();

        long patientListCount = mongoPatientBeaconRepository.findByActive(true).size();

        return new PersonnelTransport(
                (int) nursesCount,
                (int) providersCount,
                (int) patientListCount,
                (int) hospitalityCount,
                (int) assetCount
        );
    }
}
