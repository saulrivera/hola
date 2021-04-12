package com.emr.tracking.repository

import com.emr.tracking.model.Neo4jGateway
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.data.repository.query.Param

interface Neo4jGatewayRepository : Neo4jRepository<Neo4jGateway, Long> {
    @Query("MATCH (Gateway { mac: \$mac })-[:SIBLINGS*1..2]-(s:Gateway) RETURN collect(s)")
    fun findNearSiblingsByMac(@Param("mac") mac: String): List<Neo4jGateway>
}