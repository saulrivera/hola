package com.emr.tracking.repository

import com.emr.tracking.configuration.AppProperties
import com.emr.tracking.manager.RethinkManager
import com.emr.tracking.model.KontaktTelemetryResponse
import com.emr.tracking.model.StreamReading
import com.google.gson.Gson
import com.rethinkdb.RethinkDB.r
import org.springframework.stereotype.Component
import com.rethinkdb.net.Cursor
import org.json.simple.JSONObject

@Component
class StreamRepository(
    private val appProperties: AppProperties,
    private val rethinkManager: RethinkManager
) {
    companion object {
        private val table = "streamreading"
    }

    fun findById(stream: KontaktTelemetryResponse): StreamReading {
        val connection = rethinkManager.createConnection()

        r.db(appProperties.rethinkDatabase).table(table).sync().run<Any>(connection)

        val listIterator = r.db(appProperties.rethinkDatabase).table(table)
            .getAll(stream.trackingId).optArg("index", "trackingId")
            .run<Cursor<HashMap<Any, Any>>>(connection).toList()
        connection.close()

        val firstIncidence = listIterator.firstOrNull()
        if (firstIncidence == null) {
            val streamReading = StreamReading(
                stream.trackingId,
                stream.sourceId,
                stream.rssi,
                stream.calibratedRssi1m,
                mutableMapOf()
            )
            insert(streamReading)
            return streamReading
        }

        return Gson().fromJson(JSONObject(firstIncidence).toJSONString(), StreamReading::class.java)
    }

    fun update(stream: StreamReading) {
        val connection = rethinkManager.createConnection()
        r.db(appProperties.rethinkDatabase).table(table).sync().run<Any>(connection)

        r.db(appProperties.rethinkDatabase).table(table)
            .getAll(stream.trackingId)
            .optArg("index", "trackingId")
            .update(stream)
            .run<Any>(connection)
        connection.close()
    }

    fun insert(stream: StreamReading) {
        val connection = rethinkManager.createConnection()

        r.db(appProperties.rethinkDatabase).table(table)
            .insert(stream)
            .run<Any>(connection)
        connection.close()
    }
}
