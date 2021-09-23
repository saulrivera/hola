package outland.emr.tracking.logic;

import outland.emr.tracking.models.mongo.Staff;
import outland.emr.tracking.models.mongo.StaffBeacon;
import outland.emr.tracking.repositories.mongo.MongoStaffBeaconRepository;
import outland.emr.tracking.repositories.mongo.MongoStaffRepository;
import outland.emr.tracking.repositories.redis.RedisStaffBeaconRepository;
import outland.emr.tracking.repositories.redis.RedisStaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StaffLogic {
    @Autowired
    private final MongoStaffRepository mongoStaffRepository;
    @Autowired
    private final MongoStaffBeaconRepository mongoStaffBeaconRepository;
    @Autowired
    private final RedisStaffRepository redisStaffRepository;
    @Autowired
    private final RedisStaffBeaconRepository redisStaffBeaconRepository;

    public StaffLogic(
            MongoStaffRepository mongoStaffRepository,
            MongoStaffBeaconRepository mongoStaffBeaconRepository,
            RedisStaffRepository redisStaffRepository,
            RedisStaffBeaconRepository redisStaffBeaconRepository
    ) {
        this.mongoStaffRepository = mongoStaffRepository;
        this.mongoStaffBeaconRepository = mongoStaffBeaconRepository;
        this.redisStaffRepository = redisStaffRepository;
        this.redisStaffBeaconRepository = redisStaffBeaconRepository;
    }

    public boolean isTableEmpty() {
        return !((long) mongoStaffRepository.findAll().size() > 0);
    }

    public void add(Staff staff) {
        this.mongoStaffRepository.save(staff);
        this.redisStaffRepository.add(createRedis(staff));
    }

    public void associate(Staff staff, String beaconMac) {
        StaffBeacon staffBeacon = new StaffBeacon(staff.getId(), beaconMac);
        this.mongoStaffBeaconRepository.save(staffBeacon);
        this.redisStaffBeaconRepository.add(createRedis(staffBeacon));
    }

    public outland.emr.tracking.models.redis.Staff createRedis(Staff staff) {
        return new outland.emr.tracking.models.redis.Staff(
                staff.getId(),
                staff.getFirstName(),
                staff.getMiddleName(),
                staff.getLastName(),
                staff.getKind()
        );
    }

    public outland.emr.tracking.models.redis.StaffBeacon createRedis(StaffBeacon staffBeacon) {
        return new outland.emr.tracking.models.redis.StaffBeacon(
                staffBeacon.getStaffId(),
                staffBeacon.getBeaconId()
        );
    }

    public void syncWithRedis() {
        this.mongoStaffRepository.findAll().stream().map(this::createRedis).forEach(this.redisStaffRepository::add);
        this.mongoStaffBeaconRepository.findAll().stream().map(this::createRedis).forEach(this.redisStaffBeaconRepository::add);
    }
}
