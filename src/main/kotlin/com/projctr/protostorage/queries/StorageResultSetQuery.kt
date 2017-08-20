package com.projctr.protostorage.queries

import com.google.protobuf.MessageOrBuilder
import com.projctr.protostorage.configuration.StorageObjectConfiguration
import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.Query
import org.skife.jdbi.v2.util.ByteArrayColumnMapper

internal class StorageResultSetQuery<out TResultType : MessageOrBuilder>(private val objectConfiguration: StorageObjectConfiguration,
                                                                         private val dbi: DBI) : IStorageResultSetQuery<TResultType> {
    private val conditionBuilder = StringBuilder()
    private val conditionValues : MutableList<Any> = mutableListOf()

    override fun where(fieldName: String) {
        if (conditionBuilder.length > 0) throw StorageQueryException("WHERE condition has already been initialized")
        conditionBuilder.append("WHERE $fieldName")
    }

    override fun greaterThan(value: Int) {
        addCondition(">", value)
    }

    override fun lesserThan(value: Int) {
        addCondition("<", value)
    }

    override fun equals(value: String) {
        addCondition("=", value)
    }

    override fun and(fieldName: String) {
        if (!conditionBuilder.startsWith("WHERE ")) throw StorageQueryException("WHERE condition has not been initialized")
        conditionBuilder.append(" AND $fieldName")
    }

    override fun or(fieldName: String) {
        if (!conditionBuilder.startsWith("WHERE ")) throw StorageQueryException("WHERE condition has not been initialized")
        conditionBuilder.append(" OR $fieldName")
    }

    override fun like(value: String) {
        addCondition("LIKE", value)
    }

    override fun execute(): List<TResultType> {
        val result: MutableList<TResultType> = mutableListOf()
        
        executeRaw().forEach { result.add(objectConfiguration.parser.parseFrom(it) as TResultType) }

        return result
    }

    override fun executeRaw(): List<ByteArray> {
        with (objectConfiguration) {
            dbi.open().use {
                val queryString = "SELECT data FROM ${tableName} ${conditionBuilder.toString()}"
                var query = it.createQuery(queryString).map(ByteArrayColumnMapper.INSTANCE)

                conditionValues.forEachIndexed { i, obj -> query.bind(i, obj) }

                return query.list()
            }
        }
    }

    private fun addCondition(operator: String, value: Any) {
        if (!conditionBuilder.startsWith("WHERE ")) throw StorageQueryException("WHERE condition has not been initialized")
        conditionBuilder.append(" $operator :condition${conditionValues.size}")
        conditionValues.add(value)
    }
}