package com.zuratouch.prizewheel.vending

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class VmcSession(
    private val transport: SerialTransport,
    initiallyOpen: Boolean = false,
) {
    private val mutex = Mutex()
    private var isOpen = initiallyOpen

    suspend fun close() = mutex.withLock {
        transport.close()
        isOpen = false
    }

    suspend fun ensureOpen(): Result<Unit> = mutex.withLock {
        openLocked()
    }

    suspend fun reconnect(): Result<Unit> = mutex.withLock {
        transport.close()
        isOpen = false
        openLocked()
    }

    suspend fun isConnected(): Boolean = mutex.withLock {
        isOpen && transport.isAvailable()
    }

    suspend fun send(command: ByteArray): VmcResponse = mutex.withLock {
        openLocked().getOrElse {
            return@withLock VmcResponse.Unknown(-1, byteArrayOf())
        }
        sendLocked(command)
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

    private suspend fun openLocked(): Result<Unit> {
        if (isOpen) return Result.success(Unit)
        return transport.open().onSuccess { isOpen = true }
    }

    private suspend fun sendLocked(command: ByteArray): VmcResponse {
        repeat(MAX_SEND_ATTEMPTS) { attempt ->
            try {
                transport.write(command)
                val frame = transport.readFrame(FRAME_TIMEOUT_MS)
                if (frame != null) {
                    return runCatching { VmcProtocol.parse(frame) }
                        .getOrElse { VmcResponse.Unknown(-1, byteArrayOf()) }
                }
            } catch (_: Exception) {
                isOpen = false
            }

            if (attempt < MAX_SEND_ATTEMPTS - 1) {
                transport.discardInput()
                openLocked()
            }
        }
        return VmcResponse.Unknown(-1, byteArrayOf())
    }

    companion object {
        const val FRAME_TIMEOUT_MS = 3_000L
        const val DEFAULT_POLL_TIMEOUT_MS = 30_000L
        private const val MAX_SEND_ATTEMPTS = 3
    }
}
