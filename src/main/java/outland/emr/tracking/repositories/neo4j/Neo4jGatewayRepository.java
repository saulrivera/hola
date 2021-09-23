package outland.emr.tracking.repositories.neo4j;

import outland.emr.tracking.models.neo4j.Gateway;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Neo4jGatewayRepository extends Neo4jRepository<Gateway, Long> {
    @Query("MATCH (Gateway { mac: $mac })-[:siblings*1..2]-(s:Gateway) RETURN collect(s)")
    public List<Gateway> findNearSiblingsByMac(@Param("mac") String mac);
}
