package com.emr.tracing.repositories.redis;

import com.emr.tracing.models.redis.Staff;

public interface IRedisStaffRepository {
    Staff findById(String id);
    void add(Staff staff);
}
