package com.emr.tracking.model

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship

@Node(primaryLabel = "Gateway")
data class Neo4jGateway (
    @Id
    @GeneratedValue
    var id: Long? = null,
    var uniqueId: String,
    @Relationship(type = "SIBLINGS")
    var siblings: MutableList<Neo4jGateway>? = null
)