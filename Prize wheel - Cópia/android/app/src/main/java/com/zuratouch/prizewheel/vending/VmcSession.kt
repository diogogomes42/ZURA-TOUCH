package com.zuratouch.prizewheel.vending

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class VmcSession(private val transport: SerialTransport) {
    private val mutex = Mutex()
    private var isOpen = false

    suspend fun ensureOpen(): Result<Unit> = mutex.withLock {
        if (isOpen) return Result.success(Unit)
        transport.open().onSuccess { isOpen = true }
    }

    suspend fun reconnect(): Result<Unit> = mutex.withLock {
        transport.close()
        isOpen = false
        transport.open().onSuccess { isOpen = true }
    }

    suspend fun isConnected(): Boolean = mutex.withLock {
        isOpen && transport.isAvailable()
    }

    suspend fun send(command: ByteArray): VmcResponse = mutex.withLock {
        if (!isOpen) ensureOpen().getOrElse {
            return VmcResponse.Unknown(-1, byteArrayOf())
        }
        transport.write(command)
        val frame = transport.readFrame(FRAME_TIMEOUT_MS) ?: return VmcResponse.Unknown(-1, byteArrayOf())
        VmcProtocol.parse(frame)
    }

    suspend fun pollStatus(
        lane: Int,
        timeoutMs: Long = DEFAULT_POLL_TIMEOUT_MS,
        intervalMs: Long = 500,
        predicate: (VmcResponse) -> Boolean,
    ): VmcResponse? {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            when (val response = send(VmcProtocol.queryStatus(lane))) {
                is VmcResponse.Unknown -> Unit
                else -> if (predicate(response)) return response
            }
            delay(intervalMs)
        }
        return null
    }

    companion object {
        const val FRAME_TIMEOUT_MS = 3_000L
        const val DEFAULT_POLL_TIMEOUT_MS = 30_000L
    }
}
