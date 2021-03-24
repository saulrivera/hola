package com.emr.tracking

import com.emr.tracking.manager.KontaktioManager
import com.emr.tracking.model.Beacon
import com.emr.tracking.model.Gateway
import com.emr.tracking.repository.*
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@Configuration
@DependsOn("rethinkConfigurationStarter")
class DataInit(
    private val gatewayRepository: GatewayRepository,
    private val kontaktioManager: KontaktioManager,
    private val beaconRepository: BeaconRepository,
    private val neo4jGatewayRepository: Neo4jGatewayRepository
) {

    @Bean
    fun populateGateways() {
        runBlocking {
            if (gatewayRepository.count() == 0.toLong()) {
                val gatewayDevices = kontaktioManager.gatewayList()
                val gateways = gatewayDevices.map {
                    val siblings = neo4jGatewayRepository
                        .findNearSiblingsByUniqueId(it.uniqueId)
                        .map { g -> g.uniqueId }
                        .toMutableList()
                    siblings.add(it.uniqueId)
                    Gateway(it.properties!!.mac.toLowerCase(), it.uniqueId, 0, siblings)
                }
                gatewayRepository.saveAll(gateways)
            }
        }
    }

    @Bean
    fun populateBeacons() {
        runBlocking {
            if (beaconRepository.count() == 0.toLong()) {
                val beaconDevices = kontaktioManager.beaconList()
                val beacons = beaconDevices.map { Beacon(it.mac!!.toLowerCase(), it.uniqueId) }
                beaconRepository.saveAll(beacons)
            }
        }
    }
}