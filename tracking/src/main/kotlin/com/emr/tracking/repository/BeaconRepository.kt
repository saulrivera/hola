package com.emr.tracking.repository

import com.emr.tracking.configuration.AppProperties
import com.emr.tracking.manager.RethinkManager
import com.emr.tracking.manager.RethinkManager.Companion.r
import com.emr.tracking.model.Beacon
import com.rethinkdb.net.Cursor
import org.springframework.stereotype.Component
import java.util.*

@Component
class BeaconRepository(
    private val rethinkManager: RethinkManager,
    private val appProperties: AppProperties
) {
    companion object {
        private val database = "beacon"
    }
    fun count(): Long {
        val connection = rethinkManager.createConnection()
        val count: Long = r.db(appProperties.rethinkDatabase).table(database).count().run(connection)
        connection.close()
        return count
    }

    fun saveAll(beacons: List<Beacon>) {
        val connection = rethinkManager.createConnection()
        beacons.forEach {
            r.db(appProperties.rethinkDatabase).table(database).insert(it).run(connection)
        }
        connection.close()
    }

    fun isBeaconPresent(mac: String): Boolean {
        val connection = rethinkManager.createConnection()
        val isPresent = r.db(appProperties.rethinkDatabase)
            .table(database)
            .getAll(mac)
            .optArg("index", "mac")
            .run<Cursor<Any>>(connection)
            .count() > 0
        connection.close()
        return isPresent
    }
}