package com.projctr.protostorage.configuration

import com.google.protobuf.Descriptors
import com.google.protobuf.MessageOrBuilder
import com.google.protobuf.Parser
import kotlin.reflect.KClass
import kotlin.reflect.declaredMemberProperties
import kotlin.reflect.functions
import kotlin.reflect.staticFunctions

class StorageObjectConfiguration (val mappedClass: KClass<out MessageOrBuilder>) {
    var tableName = mappedClass.simpleName ?: ""
    private val mutablePrimaryKeyFields : MutableList<Descriptors.FieldDescriptor> = mutableListOf()
    private val mutableIndexedColumns : MutableList<Descriptors.FieldDescriptor> = mutableListOf()
    val primaryKeyFields : List<Descriptors.FieldDescriptor> = mutablePrimaryKeyFields
    val indexedColumns : List<Descriptors.FieldDescriptor> = mutableIndexedColumns
    val parser = getMessageParser()

    fun addPrimaryKey(fieldName: String) {
        mutablePrimaryKeyFields.add(getFieldDescriptor(fieldName))
    }

    fun addIndexedColumn(fieldName: String) {
        mutableIndexedColumns.add(getFieldDescriptor(fieldName))
    }

    private fun getFieldDescriptor(fieldName: String): Descriptors.FieldDescriptor {
        val descriptor = mappedClass.staticFunctions.first { it.name == "getDescriptor" }.call() as Descriptors.Descriptor
        return descriptor.findFieldByName(fieldName) ?: throw StorageConfigurationException("Cannot find field with name $fieldName")
    }

    private fun getMessageParser(): Parser<MessageOrBuilder> {
        return mappedClass.members.first { it.name == "PARSER" }.call() as Parser<MessageOrBuilder>? ?: throw StorageConfigurationException("Cannot find parser for class $mappedClass")
    }
}