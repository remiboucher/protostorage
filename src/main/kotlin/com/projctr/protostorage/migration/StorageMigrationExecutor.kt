package com.projctr.protostorage.migration

import com.projctr.protostorage.configuration.StorageObjectConfiguration
import com.projctr.protostorage.util.DbTypeMapper
import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.util.IntegerColumnMapper

internal class StorageMigrationExecutor(val objectConfigurations: Iterable<StorageObjectConfiguration>, val dbi: DBI) {
    internal fun migrate() {
        for(objectConfiguration in objectConfigurations) {
            try {
                // create the table first, then the indexed columns
                // to make sure newly configured ones are created on existing tables
                var builder = StringBuilder()
                builder.append("create table if not exists ${objectConfiguration.tableName} ( ")

                // TODO: PK DATA MIGRATION FOR PKs CREATED AFTER FIRST MIGRATION
                val pkNames: MutableList<String> = mutableListOf()
                for (pk in objectConfiguration.primaryKeyFields) {
                    val pkName = "id_${pk.name}"
                    pkNames.add(pkName)
                    builder.append("$pkName ${DbTypeMapper.getDbType(pk.type)} NOT NULL")
                }
                builder.append("constraint pk_${objectConfiguration.tableName} primary key (${pkNames.joinToString(", ")}), ")
                builder.append("data bytea NOT NULL )")

                dbi.open().use { it.execute(builder.toString()) }

                // reset the builder for indexed columns
                builder = StringBuilder()
                for (indexedColumn in objectConfiguration.indexedColumns) {
                    if (columnExists(objectConfiguration.tableName, indexedColumn.name)) continue

                    builder.append("alter table ${objectConfiguration.tableName} add column ${indexedColumn.name} ${DbTypeMapper.getDbType(indexedColumn.type)}; ")
                    builder.append("create index ${indexedColumn.name}_index on ${objectConfiguration.tableName} (${indexedColumn.name})")
                }

                if (builder.isNotEmpty()) dbi.open().use { it.execute(builder.toString()) }
            } catch (e: Throwable) {
                throw StorageMigrationException("Error handling migration of class ${objectConfiguration.mappedClass.simpleName}", e)
            }
        }
    }

    private fun columnExists(tableName: String, columnName: String): Boolean {
        dbi.open().use {
            val count = it.createQuery(String.format("select count(*) from information_schema.columns where table_name = '%s' and column_name = '%s'", tableName, columnName))
                    .map(IntegerColumnMapper.PRIMITIVE)
                    .first()
            return count > 0
        }
    }
}