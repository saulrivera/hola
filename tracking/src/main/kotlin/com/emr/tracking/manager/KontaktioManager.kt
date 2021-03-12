package com.emr.tracking.manager

import com.emr.tracking.configuration.AppProperties
import com.emr.tracking.model.KontaktBeaconResponse
import com.emr.tracking.model.RedisBeacon
import com.emr.tracking.repository.RedisBeaconRepository
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.springframework.stereotype.Component


@Component
class KontaktioManager(
    private val redisBeaconRepository: RedisBeaconRepository,
    private val appProperties: AppProperties
) {
    suspend fun beaconList(): List<RedisBeacon> {
        val redisBeacons = retrieveBeaconsFromRedis()

        return if (redisBeacons.isEmpty()) {
            val kontactBeacons = retrieveBeaconsFromKontakt()
            kontactBeacons.forEach { it.mac = it.mac.toLowerCase() }
            redisBeaconRepository.saveAll(kontactBeacons)
            kontactBeacons
        } else {
            redisBeacons
        }
    }

    private fun retrieveBeaconsFromRedis(): List<RedisBeacon> {
        return redisBeaconRepository.findAll().toList()
    }

    private suspend fun retrieveBeaconsFromKontakt(): List<RedisBeacon> {
        try {
            val httpClient = HttpClient(CIO)
            val response = httpClient.get<HttpResponse>("https://api.kontakt.io/device") {
                headers {
                    append("Api-Key", appProperties.kontaktApiKey)
                    append("Accept", "application/vnd.com.kontakt+json;version=10")
                }
                parameter("deviceType", "BEACON")
            }
            httpClient.close()

            return Gson().fromJson(response.readText(), KontaktBeaconResponse::class.java).devices
        } catch (error: Exception) {
            println(error)
            return listOf()
        }
    }
}