package com.emr.tracking

import com.emr.tracking.model.Gateway
import com.emr.tracking.model.Neo4jGateway
import com.emr.tracking.repository.*
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@Configuration
@DependsOn("rethinkConfigurationStarter")
class DataInit(
    private val gatewayRepository: GatewayRepository,
    private val beaconRepository: BeaconRepository,
    private val neo4jGatewayRepository: Neo4jGatewayRepository,
    private val mongoBeaconRepository: MongoBeaconRepository
) {
    @Bean
    fun populateGateways() {
        runBlocking {
            if (gatewayRepository.count() == 0.toLong()) {
                val gatewayDevices = neo4jGatewayRepository.findAll()
                val gateways = gatewayDevices.map {
                    val siblings = neo4jGatewayRepository
                        .findNearSiblingsByMac(it.mac)
                        .map { g -> g.mac }
                        .toMutableList()
                    siblings.add(it.mac)
                    Gateway(it.mac, it.uniqueId, it.floor.toLong(), siblings, Pair(it.position[0], it.position[1]))
                }
                gatewayRepository.saveAll(gateways)
            }
        }
    }

    @Bean
    fun populateBeacons() {
        runBlocking {
            if (beaconRepository.count() == 0.toLong()) {
                val beaconDevices = mongoBeaconRepository.findAll()
                beaconRepository.saveAll(beaconDevices)
            }
        }
    }
}