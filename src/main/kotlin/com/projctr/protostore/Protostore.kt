package com.projctr.protostore

import com.google.protobuf.GeneratedMessage
import com.projctr.protostore.configuration.StorageConfiguration
import com.projctr.protostore.migration.StorageMigrationExecutor
import com.projctr.protostore.queries.StorageQuery
import org.skife.jdbi.v2.DBI
import java.util.*
import kotlin.reflect.KClass

class Protostore private constructor(val configuration: StorageConfiguration, val connectionUrl: String, val connectionProperties: Properties) {
    companion object Factory {
        fun create(configuration: StorageConfiguration, migrateDatabase: Boolean = true): Protostore {
            with(configuration.connectionConfiguration) {
                val connectionUrl = "jdbc:postgresql://$host/$databaseName"
                val properties = Properties().apply {
                    setProperty("user", username)
                    setProperty("password", password)
                    setProperty("ssl", useSsl.toString())
                }

                val instance = Protostore(configuration, connectionUrl, properties)

                if (migrateDatabase) {
                    instance.migrate()
                }

                return instance
            }
        }
    }

    fun migrate() {
        val migrator = StorageMigrationExecutor(configuration.getObjectConfigurations(), DBI(connectionUrl, connectionProperties))
        migrator.migrate()
    }

    fun <T: GeneratedMessage> createQuery(cls: KClass<out T>): StorageQuery<T> {
        return StorageQuery(configuration.getObjectConfiguration(cls), getDbi())
    }

    private fun getDbi(): DBI {
        return DBI(connectionUrl, connectionProperties)
    }
}