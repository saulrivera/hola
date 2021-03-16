package com.emr.tracking.repository

import com.emr.tracking.model.Gateway
import org.springframework.data.mongodb.repository.MongoRepository

interface GatewayRepository: MongoRepository<Gateway, String> {}