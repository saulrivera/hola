package com.emr.tracking.repository

import com.emr.tracking.model.RedisBeacon
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisBeaconRepository : CrudRepository<RedisBeacon, String> {}