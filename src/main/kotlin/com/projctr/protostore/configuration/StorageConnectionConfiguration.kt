package com.projctr.protostore.configuration

data class StorageConnectionConfiguration(val host: String, val port: Int = 5432, val databaseName: String, val username: String, val password: String, val useSsl: Boolean = false)