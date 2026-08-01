package com.zuratouch.prizewheel.vending

interface SerialTransport {
    suspend fun open(): Result<Unit>
    suspend fun write(data: ByteArray)
    suspend fun readFrame(timeoutMs: Long): ByteArray?
    suspend fun close()
    fun isAvailable(): Boolean
}
