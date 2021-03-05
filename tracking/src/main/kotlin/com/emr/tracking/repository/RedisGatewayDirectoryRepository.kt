package com.emr.tracking.repository

import com.emr.tracking.model.RedisGatewayDirectory
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisGatewayDirectoryRepository : CrudRepository<RedisGatewayDirectory, String> {}