package com.zuratouch.prizewheel.vending

import java.util.ArrayDeque

class FakeSerialTransport(private val responses: ArrayDeque<ByteArray>) : SerialTransport {
    val written = mutableListOf<ByteArray>()
    var openResult: Result<Unit> = Result.success(Unit)

    override suspend fun open(): Result<Unit> = openResult

    override suspend fun write(data: ByteArray) {
        written.add(data.copyOf())
    }

    override suspend fun readFrame(timeoutMs: Long): ByteArray? = responses.poll()

    override suspend fun discardInput() = Unit

    override suspend fun close() = Unit

    override fun isAvailable(): Boolean = true
}
