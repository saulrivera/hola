package com.emr.tracking.manager

import com.emr.tracking.configuration.AppProperties
import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.lang.RuntimeException
import java.util.concurrent.TimeoutException

@Component
class RethinkManager(
    private val appProperties: AppProperties
) {
    companion object {
        val r: RethinkDB = RethinkDB.r
    }

    fun createConnection(): Connection {
        try {
            return r
                .connection()
                .hostname(appProperties.rethinkHost)
                .port(appProperties.rethinkPort.toInt())
                .connect()
        } catch (error: TimeoutException) {
            throw RuntimeException(error)
        }
    }
}