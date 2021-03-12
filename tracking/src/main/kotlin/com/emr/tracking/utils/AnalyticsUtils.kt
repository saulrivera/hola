package com.emr.tracking.utils

import com.emr.tracking.model.SocketTracingStream
import com.google.gson.Gson
import org.springframework.stereotype.Component
import java.io.File
import java.time.Instant

@Component
class AnalyticsUtils {
    private val rawData = mutableMapOf<String, MutableList<List<SocketTracingStream>>>()
    private val processedData = mutableMapOf<String, MutableList<List<SocketTracingStream>>>()

//    fun saveRowData(data: Map<String, List<KontaktGatewayResponse>>) {
//        data.forEach { (deviceId, kontaktResponse) ->
//            val listData = if (rawData[deviceId] != null) {
//                rawData[deviceId]!!
//            } else {
//                mutableListOf()
//            }
//
//            val newData = kontaktResponse.map {
//                SocketTracingStream(it.uniqueId, it.sourceId, it.rssi, it.calibratedRssi1m)
//            }
//
//            listData.add(newData)
//
//            rawData[deviceId] = listData
//        }
//
//        saveData(rawData, "row")
//    }
//
//    fun saveProcessedData(deviceId: String, data: List<KontaktGatewayResponse>) {
//        val listData = if(processedData[deviceId] != null) {
//            processedData[deviceId]!!
//        } else {
//            mutableListOf()
//        }
//
//        val newData = data.map {
//            SocketTracingStream(it.uniqueId, it.sourceId, it.rssi, it.calibratedRssi1m)
//        }
//
//        listData.add(newData)
//
//        processedData[deviceId] = listData
//
//        saveData(processedData, "processed")
//    }

    private fun saveData(data: MutableMap<String, MutableList<List<SocketTracingStream>>>, source: String) {
        val file = File("./data/analytics/${source}.json")
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        val jsonData = Gson().toJson(data)
        file.writeText(jsonData)
    }
}