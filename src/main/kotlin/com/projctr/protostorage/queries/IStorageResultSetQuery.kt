package com.projctr.protostorage.queries

interface IStorageResultSetQuery<out T> {
    fun where(fieldName: String)
    fun greaterThan(value: Int)
    fun lesserThan(value: Int)  // probably a bunch more numeral types
    fun equals(value: String)  // probably a bunch more types
    fun and()  // will have to figure this out, maybe another where is required?
    fun or()  // will have to figure this out, maybe another where is required?
    fun execute(): List<T>
    fun executeRaw(): List<ByteArray>
}