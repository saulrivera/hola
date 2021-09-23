package outland.emr.tracking.repositories.redis;

import outland.emr.tracking.models.redis.Staff;

public interface IRedisStaffRepository {
    Staff findById(String id);
    void add(Staff staff);
}
