package com.projctr.protostore.configuration

import com.google.protobuf.GeneratedMessage
import kotlin.reflect.KClass

class StorageConfiguration(val connectionConfiguration: StorageConnectionConfiguration) {
    private val configuredObjects : MutableMap<KClass<out GeneratedMessage>, StorageObjectConfiguration> = mutableMapOf()

    fun registerClass(cls: KClass<out GeneratedMessage>) : StorageObjectConfiguration {
        if (configuredObjects.containsKey(cls)) throw StorageConfigurationException("Class $cls is already registered")

        val configuration = StorageObjectConfiguration(cls)
        configuredObjects[cls] = configuration
        return configuration
    }

    fun registerObjectConfiguration(objectConfiguration: StorageObjectConfiguration) {
        if (configuredObjects.containsKey(objectConfiguration.mappedClass))  throw StorageConfigurationException("Class ${objectConfiguration.mappedClass} is already registered")

        configuredObjects[objectConfiguration.mappedClass] = objectConfiguration
    }

    internal fun getObjectConfiguration(cls: KClass<out GeneratedMessage>) : StorageObjectConfiguration {
        return configuredObjects[cls] ?: throw StorageConfigurationException("Class $cls is not registered")
    }

    internal fun getObjectConfigurations() : Iterable<StorageObjectConfiguration> = configuredObjects.values
}