package com.projctr.protostore.writer

import com.google.protobuf.GeneratedMessage
import com.projctr.protostore.configuration.StorageObjectConfiguration
import org.skife.jdbi.v2.DBI

internal class StorageWriter<in T: GeneratedMessage>(private val objectConfiguration: StorageObjectConfiguration, private val dbi: DBI) {
    fun insert(obj: T) {
        val builder: StringBuilder = StringBuilder()

        with(objectConfiguration) {
            val pkColumnNames = primaryKeyFields.map { it.name }.joinToString()
            val indexedColumnNames = indexedColumns.map { it.name }.joinToString()
            val valueNames = listOf(pkColumnNames, indexedColumnNames).joinToString()
            val valuePlaceholders = "?, ".repeat(primaryKeyFields.size + indexedColumns.size)
            val pkValues = primaryKeyFields.map { obj.getField(it) }
            val indexedValues = indexedColumns.map { obj.getField(it) }

            builder.append("insert into $tableName ($valueNames, data) values ($valuePlaceholders, ?)")

            dbi.open().use {
                it.execute(builder.toString(), pkValues, indexedValues, obj.toByteArray())
            }
        }
    }

    fun update(obj: T) {
        // TODO
    }
}