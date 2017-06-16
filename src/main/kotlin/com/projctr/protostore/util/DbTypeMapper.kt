package com.projctr.protostore.util

import com.google.protobuf.Descriptors.FieldDescriptor as FieldDescriptor

object DbTypeMapper {
    private val typeMap : Map<FieldDescriptor.Type, String> = hashMapOf(
            FieldDescriptor.Type.BOOL to "bool",
            FieldDescriptor.Type.BYTES to "bytea",
            FieldDescriptor.Type.DOUBLE to "double precision",
            FieldDescriptor.Type.ENUM to "integer",
            FieldDescriptor.Type.FIXED32 to "integer",
            FieldDescriptor.Type.FIXED64 to "bigint",
            FieldDescriptor.Type.FLOAT to "real",
            FieldDescriptor.Type.INT32 to "integer",
            FieldDescriptor.Type.INT64 to "bigint",
            FieldDescriptor.Type.SFIXED32 to "integer",
            FieldDescriptor.Type.SFIXED64 to "bigint",
            FieldDescriptor.Type.SINT32 to "integer",
            FieldDescriptor.Type.SINT64 to "bigint",
            FieldDescriptor.Type.STRING to "varchar",
            FieldDescriptor.Type.UINT32 to "numeric", // no unsigned types in Postgres
            FieldDescriptor.Type.UINT64 to "numeric" // no unsigned types in Postgres
    )

    fun getDbType(type: FieldDescriptor.Type) : String = typeMap[type] ?: throw DbTypeMapperException("Type $type is not mapped to a DB type")
}