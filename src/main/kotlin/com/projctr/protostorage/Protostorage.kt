package com.projctr.protostorage

import com.google.protobuf.GeneratedMessage
import com.projctr.protostorage.configuration.StorageConfiguration
import com.projctr.protostorage.migration.StorageMigrationExecutor
import com.projctr.protostorage.queries.StorageQuery
import com.projctr.protostorage.writer.StorageWriter
import org.skife.jdbi.v2.DBI
import java.util.*
import kotlin.reflect.KClass

class Protostorage private constructor(val configuration: StorageConfiguration, val connectionUrl: String, val connectionProperties: Properties) {
    companion object Factory {
        fun create(configuration: StorageConfiguration, migrateDatabase: Boolean = true): Protostorage {
            with(configuration.connectionConfiguration) {
                val connectionUrl = "jdbc:postgresql://$host/$databaseName"
                val properties = Properties().apply {
                    setProperty("user", username)
                    setProperty("password", password)
                    setProperty("ssl", useSsl.toString())
                }

                val instance = Protostorage(configuration, connectionUrl, properties)

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

    fun <T: GeneratedMessage> insert(obj: T) {
        val writer = StorageWriter<T>(configuration.getObjectConfiguration(obj.javaClass.kotlin), getDbi())
        writer.insert(obj)
    }

    private fun getDbi(): DBI {
        return DBI(connectionUrl, connectionProperties)
    }
}