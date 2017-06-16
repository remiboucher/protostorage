package com.projctr.protostore.queries

interface IStorageResultQuery<out T> {
    fun execute(): T
    fun executeRaw(): ByteArray
}