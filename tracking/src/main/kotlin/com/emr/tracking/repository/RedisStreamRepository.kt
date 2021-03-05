package com.emr.tracking.repository

import com.emr.tracking.model.RedisStreamReading
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisStreamRepository : CrudRepository<RedisStreamReading, String> {}