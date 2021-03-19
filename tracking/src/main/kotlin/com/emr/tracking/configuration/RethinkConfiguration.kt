package com.emr.tracking.configuration

import com.emr.tracking.manager.RethinkManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.rethinkdb.RethinkDB.r
import org.springframework.stereotype.Component

@Configuration
class RethinkConfiguration(
    private val rethinkManager: RethinkManager,
    private val appProperties: AppProperties
) {
    @Bean("rethinkConfigurationStarter")
    fun configureDatabase() {
        val connection = rethinkManager.createConnection()
        val list: List<String> = r.dbList().run(connection)

        if (!list.contains(appProperties.rethinkDatabase)) {
            r.dbCreate(appProperties.rethinkDatabase).run<String>(connection)
        }

        val tables: List<String> = r.db(appProperties.rethinkDatabase).tableList().run(connection)

        if (!tables.contains("beacon")) {
            r.db(appProperties.rethinkDatabase)
                .tableCreate("beacon")
                .run<String>(connection)
            r.db(appProperties.rethinkDatabase)
                .table("beacon")
                .indexCreate("mac")
                .run<String>(connection)
        }
        if (!tables.contains("gateway")) {
            r.db(appProperties.rethinkDatabase)
                .tableCreate("gateway")
                .run<String>(connection)
            r.db(appProperties.rethinkDatabase)
                .table("gateway")
                .indexCreate("mac")
                .run<String>(connection)
            r.db(appProperties.rethinkDatabase)
                .table("gateway")
                .indexCreate("uniqueId")
                .run<Any>(connection)
        }
        if (!tables.contains("streamreading")) {
            r.db(appProperties.rethinkDatabase)
                .tableCreate("streamreading")
                .run<String>(connection)
            r.db(appProperties.rethinkDatabase)
                .table("streamreading")
                .indexCreate("trackingId")
                .run<String>(connection)
        }
    }
}