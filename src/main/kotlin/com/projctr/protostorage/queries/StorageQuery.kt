package com.projctr.protostorage.queries

import com.google.protobuf.MessageOrBuilder
import com.projctr.protostorage.configuration.StorageObjectConfiguration
import org.skife.jdbi.v2.DBI

class StorageQuery<out T: MessageOrBuilder>(private val objectConfiguration: StorageObjectConfiguration, private val dbi: DBI) {
    // TODO: WAY MORE ID TYPES
    fun getById(id: String): IStorageResultQuery<T> {
        return StorageResultQuery(objectConfiguration, id, dbi)
    }

    fun getById(compositeId: Map<String, Any>): IStorageResultQuery<T> {
        return StorageResultQuery(objectConfiguration, compositeId, dbi)
    }

    fun get(): IStorageResultSetQuery<T> {
        return StorageResultSetQuery(objectConfiguration, dbi)
    }
}