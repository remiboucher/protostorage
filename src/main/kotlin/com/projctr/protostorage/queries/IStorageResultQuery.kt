package com.projctr.protostorage.queries

interface IStorageResultQuery<out T> {
    fun execute(): T
    fun executeRaw(): ByteArray
}