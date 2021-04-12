package com.emr.tracking.repository

import com.emr.tracking.configuration.AppProperties
import com.emr.tracking.manager.RethinkManager
import com.emr.tracking.manager.RethinkManager.Companion.r
import com.emr.tracking.model.Gateway
import com.google.gson.Gson
import com.rethinkdb.net.Cursor
import org.json.simple.JSONObject
import org.springframework.stereotype.Component

@Component
class GatewayRepository(
    private val rethinkManager: RethinkManager,
    private val appProperties: AppProperties
) {
    companion object {
        private const val table = "gateway"
    }

    fun count(): Long {
        val connection = rethinkManager.createConnection()
        val count: Long = r.db(appProperties.rethinkDatabase).table(table).count().run(connection)
        connection.close()
        return count
    }

    fun saveAll(gateways: List<Gateway>) {
        val connection = rethinkManager.createConnection()
        gateways.forEach {
            r.db(appProperties.rethinkDatabase).table(table).insert(it).run(connection)
        }
        connection.close()
    }

    fun findByUniqueId(uniqueId: String): Gateway? {
        val connection = rethinkManager.createConnection()
        val listIterator = r.db(appProperties.rethinkDatabase).table(table)
            .getAll(uniqueId).optArg("index", "uniqueId")
            .run<Cursor<HashMap<Any, Any>>>(connection).toList()
        connection.close()

        val element = listIterator.firstOrNull() ?: return null
        val json = JSONObject(element).toString()
        return Gson().fromJson(json, Gateway::class.java)
    }

    fun findByMac(mac: String): Gateway? {
        val connection = rethinkManager.createConnection()
        val listIterator = r.db(appProperties.rethinkDatabase).table(table)
            .getAll(mac).optArg("index", "mac")
            .run<Cursor<HashMap<Any, Any>>>(connection).toList()
        connection.close()

        val element = listIterator.firstOrNull() ?: return null
        val json = JSONObject(element).toString()
        return Gson().fromJson(json, Gateway::class.java)
    }
}
