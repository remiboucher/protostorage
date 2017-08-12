package com.projctr.protostorage.writer

import com.google.protobuf.GeneratedMessage
import com.projctr.protostorage.configuration.StorageObjectConfiguration
import org.skife.jdbi.v2.DBI

internal class StorageWriter<in T: GeneratedMessage>(private val objectConfiguration: StorageObjectConfiguration, private val dbi: DBI) {
    fun insert(obj: T) {
        val builder: StringBuilder = StringBuilder()

        with(objectConfiguration) {
            val pkColumnNames = primaryKeyFields.map { "id_${it.name}" }.joinToString()
            val valueNames = mutableListOf(pkColumnNames)
            if (indexedColumns.size > 0) {
                val indexedColumnNames = indexedColumns.map { it.name }.joinToString()
                valueNames.add(indexedColumnNames)
            }
            val valuePlaceholders = valueNames.map { ":$it" }
            val pkValues = primaryKeyFields.map { obj.getField(it) }
            val indexedValues = indexedColumns.map { obj.getField(it) }

            builder.append("insert into $tableName (${valueNames.joinToString()}, data) values (${valuePlaceholders.joinToString()}, :data)")

            dbi.open().use {
                val query = it.createStatement(builder.toString())
                var i = 0

                for (pkValue in pkValues) {
                    query.bind(i++, pkValue)
                }

                for (indexedValue in indexedValues) {
                    query.bind(i++, indexedValue)
                }

                query.bind(i, obj.toByteArray())

                query.execute()
            }
        }
    }

    fun update(obj: T) {
        // TODO
    }
}