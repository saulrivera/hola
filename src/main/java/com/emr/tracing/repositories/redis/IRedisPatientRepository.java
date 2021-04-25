package com.emr.tracing.repositories.redis;

import com.emr.tracing.models.redis.Patient;

public interface IRedisPatientRepository {
    Patient findById(String id);
    void add(Patient patient);
}
