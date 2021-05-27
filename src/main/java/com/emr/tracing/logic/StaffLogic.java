package com.emr.tracing.logic;

import com.emr.tracing.models.mongo.Staff;
import com.emr.tracing.models.mongo.StaffBeacon;
import com.emr.tracing.repositories.mongo.MongoStaffBeaconRepository;
import com.emr.tracing.repositories.mongo.MongoStaffRepository;
import com.emr.tracing.repositories.redis.RedisStaffBeaconRepository;
import com.emr.tracing.repositories.redis.RedisStaffRepository;
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

    public com.emr.tracing.models.redis.Staff createRedis(Staff staff) {
        return new com.emr.tracing.models.redis.Staff(
                staff.getId(),
                staff.getFirstName(),
                staff.getMiddleName(),
                staff.getLastName(),
                staff.getKind()
        );
    }

    public com.emr.tracing.models.redis.StaffBeacon createRedis(StaffBeacon staffBeacon) {
        return new com.emr.tracing.models.redis.StaffBeacon(
                staffBeacon.getStaffId(),
                staffBeacon.getBeaconId()
        );
    }

    public void syncWithRedis() {
        this.mongoStaffRepository.findAll().stream().map(this::createRedis).forEach(this.redisStaffRepository::add);
        this.mongoStaffBeaconRepository.findAll().stream().map(this::createRedis).forEach(this.redisStaffBeaconRepository::add);
    }
}
