package com.projctr.protostorage.configuration

import com.google.protobuf.Descriptors
import com.google.protobuf.MessageOrBuilder
import kotlin.reflect.KClass
import kotlin.reflect.staticFunctions

class StorageObjectConfiguration constructor(val mappedClass: KClass<out MessageOrBuilder>) {
    var tableName = mappedClass.simpleName ?: ""
    private val mutablePrimaryKeyFields : MutableList<Descriptors.FieldDescriptor> = mutableListOf()
    private val mutableIndexedColumns : MutableList<Descriptors.FieldDescriptor> = mutableListOf()
    val primaryKeyFields : List<Descriptors.FieldDescriptor> = mutablePrimaryKeyFields
    val indexedColumns : List<Descriptors.FieldDescriptor> = mutableIndexedColumns

    fun addPrimaryKey(fieldName: String) {
        mutablePrimaryKeyFields.add(getFieldDescriptor(fieldName))
    }

    fun addIndexedColumns(fieldName: String) {
        mutableIndexedColumns.add(getFieldDescriptor(fieldName))
    }

    private fun getFieldDescriptor(fieldName: String): Descriptors.FieldDescriptor {
        val descriptor = mappedClass.staticFunctions.first { it.name == "getDescriptor" }.call() as Descriptors.Descriptor
        return descriptor.findFieldByName(fieldName) ?: throw StorageConfigurationException("Cannot find field with name $fieldName")
    }
}