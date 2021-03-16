package com.emr.tracking

import com.emr.tracking.manager.KontaktioManager
import com.emr.tracking.model.Beacon
import com.emr.tracking.model.Gateway
import com.emr.tracking.model.RedisBeacon
import com.emr.tracking.model.RedisGateway
import com.emr.tracking.repository.*
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataInit(
    private val gatewayRepository: GatewayRepository,
    private val kontaktioManager: KontaktioManager,
    private val redisBeaconRepository: RedisBeaconRepository,
    private val beaconRepository: BeaconRepository,
    private val redisGatewayRepository: RedisGatewayRepository,
    private val neo4jGatewayRepository: Neo4jGatewayRepository
) {

    @Bean
    fun populateGateways() {
        runBlocking {
            val gatewayDevices = kontaktioManager.gatewayList()
            if (gatewayRepository.count() == 0.toLong()) {
                val gateways = gatewayDevices.map { Gateway(it.uniqueId, it.properties!!.mac.toLowerCase()) }
                gatewayRepository.saveAll(gateways)
            }

            if (redisGatewayRepository.count() == 0.toLong()) {
                val redisGateway = gatewayDevices.map {
                    val siblings = neo4jGatewayRepository
                        .findNearSiblingsByUniqueId(it.uniqueId)
                        .map { g -> g.uniqueId }
                        .toMutableList()
                    siblings.add(it.uniqueId)
                    val gateway = gatewayRepository.findById(it.uniqueId).get()
                    RedisGateway(it.uniqueId, gateway.position, siblings)
                }
                redisGatewayRepository.saveAll(redisGateway)
            }
        }
    }

    @Bean
    fun populateBeacons() {
        runBlocking {
            val beaconDevices = kontaktioManager.beaconList()
            if (redisBeaconRepository.count() == 0.toLong()) {
                val redisBeacons = beaconDevices.map { RedisBeacon(it.mac!!.toLowerCase(), it.uniqueId) }
                redisBeaconRepository.saveAll(redisBeacons)
            }

            if (beaconRepository.count() == 0.toLong()) {
                val beacons = beaconDevices.map { Beacon(it.mac!!.toLowerCase(), it.uniqueId) }
                beaconRepository.saveAll(beacons)
            }
        }
    }
}