package com.emr.tracking.repository

import com.emr.tracking.model.RedisGateway
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisGatewayRepository : CrudRepository<RedisGateway, String> {}