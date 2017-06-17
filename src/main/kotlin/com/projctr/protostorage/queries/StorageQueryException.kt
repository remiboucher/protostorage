package com.projctr.protostorage.queries

class StorageQueryException : Throwable {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(cause: Throwable) : super(cause)
    constructor(message: String?, cause: Throwable) : super(message, cause)
}