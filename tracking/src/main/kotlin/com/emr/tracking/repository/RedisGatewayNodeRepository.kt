package com.emr.tracking.repository

import com.emr.tracking.model.RedisGatewayNode
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RedisGatewayNodeRepository : CrudRepository<RedisGatewayNode, String> {}