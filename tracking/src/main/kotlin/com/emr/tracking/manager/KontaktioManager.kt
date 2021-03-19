package com.emr.tracking.manager

import com.emr.tracking.configuration.AppProperties
import com.emr.tracking.model.KontaktDevice
import com.emr.tracking.model.KontaktDeviceResponse
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.springframework.stereotype.Component

@Component
class KontaktioManager(
    private val appProperties: AppProperties
) {
    suspend fun beaconList(): List<KontaktDevice> {
        return retrieveDeviceOfType(DeviceType.BEACON)
    }

    suspend fun gatewayList(): List<KontaktDevice> {
        return retrieveDeviceOfType(DeviceType.GATEWAY)
    }

    private suspend fun retrieveDeviceOfType(type: DeviceType): List<KontaktDevice> {
        try {
            val httpClient = HttpClient(CIO)
            val response = httpClient.get<HttpResponse>("https://api.kontakt.io/device") {
                headers {
                    append("Api-Key", appProperties.kontaktApiKey)
                    append("Accept", "application/vnd.com.kontakt+json;version=10")
                }
                when(type) {
                    DeviceType.BEACON -> parameter("deviceType", "BEACON")
                    DeviceType.GATEWAY -> parameter("deviceType", "GATEWAY")
                }
            }
            httpClient.close()

            return Gson().fromJson(response.readText(), KontaktDeviceResponse::class.java).devices
        } catch (error: Exception) {
            println(error)
            return listOf()
        }
    }

    enum class DeviceType {
        BEACON, GATEWAY
    }
}