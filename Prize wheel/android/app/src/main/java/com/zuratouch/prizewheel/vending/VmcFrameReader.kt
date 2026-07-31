package com.zuratouch.prizewheel.vending

/**
 * Incremental parser for VMC response frames with resync on corruption.
 * Expects frames: FF 00 AA [command] [length] [payload] [checksum].
 */
internal class VmcFrameReader {
    private val buffer = ArrayList<Byte>(MAX_FRAME_SIZE)

    fun reset() {
        buffer.clear()
    }

    fun append(byte: Byte): ByteArray? {
        buffer.add(byte)
        trimBufferIfNeeded()
        trimToSyncPrefix()

        if (buffer.size < MIN_FRAME_SIZE) return null

        val payloadSize = buffer[4].toInt() and 0xFF
        if (payloadSize > MAX_PAYLOAD_SIZE) {
            buffer.removeAt(0)
            trimToSyncPrefix()
            return null
        }

        val expectedSize = payloadSize + MIN_FRAME_SIZE
        if (buffer.size < expectedSize) return null

        val frame = buffer.take(expectedSize).toByteArray()
        if (!isValidFrame(frame)) {
            buffer.removeAt(0)
            trimToSyncPrefix()
            return null
        }

        repeat(expectedSize) { buffer.removeAt(0) }
        return frame
    }

    private fun trimBufferIfNeeded() {
        while (buffer.size > MAX_FRAME_SIZE * 2) {
            buffer.removeAt(0)
        }
    }

    private fun trimToSyncPrefix() {
        while (buffer.size >= 2) {
            val syncIndex = findSyncIndex()
            if (syncIndex < 0) {
                if (buffer.size > 1) {
                    val last = buffer.last()
                    buffer.clear()
                    buffer.add(last)
                }
                return
            }
            if (syncIndex > 0) {
                repeat(syncIndex) { buffer.removeAt(0) }
            }
            if (buffer.size >= 3 && buffer[2].toInt() and 0xFF != VMC_HEADER) {
                buffer.removeAt(0)
                continue
            }
            return
        }
    }

    private fun findSyncIndex(): Int {
        for (index in 0 until buffer.size - 1) {
            if (buffer[index].toInt() and 0xFF == ADDRESS && buffer[index + 1].toInt() and 0xFF == FRAME_NUMBER) {
                return index
            }
        }
        return -1
    }

    private fun isValidFrame(frame: ByteArray): Boolean =
        runCatching { VmcProtocol.parse(frame); true }.getOrDefault(false)

    companion object {
        const val MIN_FRAME_SIZE = 6
        const val MAX_PAYLOAD_SIZE = 255
        const val MAX_FRAME_SIZE = MIN_FRAME_SIZE + MAX_PAYLOAD_SIZE
        private const val ADDRESS = 0xFF
        private const val FRAME_NUMBER = 0x00
        private const val VMC_HEADER = 0xAA
    }
}
