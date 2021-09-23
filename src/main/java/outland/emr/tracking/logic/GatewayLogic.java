package outland.emr.tracking.logic;

import outland.emr.tracking.models.neo4j.Gateway;
import outland.emr.tracking.repositories.neo4j.Neo4jGatewayRepository;
import outland.emr.tracking.repositories.redis.RedisGatewayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GatewayLogic {
    @Autowired
    private final Neo4jGatewayRepository gatewayRepository;
    @Autowired
    private final RedisGatewayRepository redisGatewayRepository;

    public GatewayLogic(Neo4jGatewayRepository gatewayRepository, RedisGatewayRepository redisGatewayRepository) {
        this.gatewayRepository = gatewayRepository;
        this.redisGatewayRepository = redisGatewayRepository;
    }

    public boolean isTableEmpty() {
        return !(gatewayRepository.findAll().size() > 0);
    }

    public void add(Gateway gateway) {
        gatewayRepository.save(gateway);

        redisGatewayRepository.add(createGatewayRedis(gateway));
    }

    public void add(List<Gateway> gateways) {
        gatewayRepository.saveAll(gateways);
        gateways.stream()
                .map(this::createGatewayRedis)
                .forEach(redisGatewayRepository::add);
    }

    public void syncWithRedis() {
        gatewayRepository.findAll()
                .stream()
                .map(this::createGatewayRedis)
                .forEach(redisGatewayRepository::add);
    }

    private outland.emr.tracking.models.redis.Gateway createGatewayRedis(Gateway gateway) {
        Set<String> siblings = gatewayRepository.findNearSiblingsByMac(gateway.getMac())
                .stream()
                .map(Gateway::getMac)
                .collect(Collectors.toSet());
        return new outland.emr.tracking.models.redis.Gateway(
                gateway.getMac(),
                gateway.getLabel(),
                gateway.getFloor(),
                gateway.getCoordinateX(),
                gateway.getCoordinateY(),
                gateway.getA(),
                gateway.getB(),
                siblings
        );
    }
}
