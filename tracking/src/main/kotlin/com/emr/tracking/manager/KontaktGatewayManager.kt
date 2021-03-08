package com.emr.tracking.manager

import com.emr.tracking.configuration.AppProperties
import com.emr.tracking.model.*
import com.emr.tracking.repository.RedisGatewayDirectoryRepository
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.springframework.stereotype.Component
import java.io.File
import java.lang.Exception
import java.time.Instant

@Component
class KontaktGatewayManager(
    private val redisGatewayDirectoryRepository: RedisGatewayDirectoryRepository,
    private val appProperties: AppProperties
) {
    private var _listOfGateways = listOf<String>()

    var listOfGateways: List<String>
        get() {
            if (_listOfGateways.isEmpty()) {
                _listOfGateways = redisGatewayDirectoryRepository.findAll().map { it.uniqueId }
            }
            return _listOfGateways
        }
        set(value) {
            val entities = value.map { RedisGatewayDirectory(uniqueId = it) }
            redisGatewayDirectoryRepository.saveAll(entities)
            _listOfGateways = value
        }

    suspend fun retrieveDataForGateway(gatewayIds: List<String>): List<KontaktGatewayResponse> {
        try {
            val httpClient = HttpClient(CIO)
            val response = httpClient.get<HttpResponse>("https://apps-api.prod.kontakt.io/v3/telemetry/latest") {
                headers {
                    append("Api-Key", appProperties.kontaktApiKey)
                }
                parameter("page", "0")
                parameter("size", "9999")
                parameter("startTime", Instant.now().toString())
                gatewayIds.map {
                    parameter("sourceIds", it)
                }
            }
            httpClient.close()

            val data = Gson().fromJson(response.readText(), KontaktTelemetryResponse::class.java)
            val beacons = data.content.filter { it.model == 27 }
//            saveData(beacons.groupBy { it.uniqueId })

            return beacons
        } catch (error: Exception) {
            print(error)
            return emptyList()
        }
    }

    suspend fun getListOfGateways(): List<String> {
        if (listOfGateways.count() > 0) {
            return listOfGateways
        }

        try {
            val httpClient = HttpClient(CIO)
            val response = httpClient.get<HttpResponse>("https://api.kontakt.io/device") {
                headers {
                    append("Api-Key", appProperties.kontaktApiKey)
                    append("Accept", "application/vnd.com.kontakt+json;version=10")
                }
                parameter("deviceType", "GATEWAY")
            }
            httpClient.close()

            val data = Gson().fromJson(response.readText(), KontaktDeviceResponse::class.java)
            listOfGateways = data.devices.map { it.uniqueId }

            return listOfGateways
        } catch (error: Exception) {
            print(error)
            return emptyList()
        }
    }

    private fun saveData(data: Map<String, List<KontaktGatewayResponse>>) {
        val file = File("./data/${Instant.now()}")
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        val jsonData = Gson().toJson(data)
        file.writeText(jsonData)
    }
}