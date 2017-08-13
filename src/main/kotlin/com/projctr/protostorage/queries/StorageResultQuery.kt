package com.projctr.protostorage.queries

import com.google.protobuf.*
import com.projctr.protostorage.configuration.StorageObjectConfiguration
import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.Query
import org.skife.jdbi.v2.util.ByteArrayColumnMapper

class StorageResultQuery<out TResultType : MessageOrBuilder, TKeyType>(private val objectConfiguration: StorageObjectConfiguration,
                                                                       private val id: TKeyType,
                                                                       private val dbi: DBI) : IStorageResultQuery<TResultType> {
    override fun execute(): TResultType {
        return objectConfiguration.parser.parseFrom(executeRaw()) as TResultType? ?: throw StorageQueryException("Could not deserialize data")
    }

    override fun executeRaw(): ByteArray {
        var query: Query<ByteArray>
        val builder: StringBuilder = StringBuilder()

        with(objectConfiguration) {
            builder.append("select data from $tableName where ")

            dbi.open().use {
                if (id is Array<*>) {
                    if (id.size != primaryKeyFields.size) {
                        throw StorageQueryException("Number of composite PK fields in query doesn't correspond to number of PK columns in the table. Use get().where() if you want to query with a partial key.")
                    }

                    id.forEachIndexed { i, any -> builder.append("id_${primaryKeyFields[i].name} = $it") }
                } else {
                    if (primaryKeyFields.size > 1) {
                        throw StorageQueryException("There's more than one PK column in the table. Use get().where() if you want to query with a partial key.")
                    }

                    builder.append("id_${primaryKeyFields[0].name} = $id")
                }

                query = it.createQuery(builder.toString()).map(ByteArrayColumnMapper.INSTANCE)

                return query.first()
            }
        }
    }
}