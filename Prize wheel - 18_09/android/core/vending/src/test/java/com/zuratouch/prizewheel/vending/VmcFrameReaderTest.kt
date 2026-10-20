package com.zuratouch.prizewheel.vending

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VmcFrameReaderTest {
    private val reader = VmcFrameReader()

    @Test
    fun `extracts valid frame from byte stream`() {
        val frame = response(VmcProtocol.Command.DEVICE_ID, "VMC-1".toByteArray())
        var result: ByteArray? = null
        for (byte in frame) {
            result = reader.append(byte)
        }
        assertEquals(frame.toList(), result?.toList())
    }

    @Test
    fun `resyncs after garbage prefix`() {
        val frame = response(VmcProtocol.Command.DEVICE_ID, "VMC-1".toByteArray())
        var result: ByteArray? = null
        for (byte in byteArrayOf(0x01, 0x02, 0x03) + frame) {
            result = reader.append(byte) ?: result
        }
        assertEquals(frame.toList(), result?.toList())
    }

    @Test
    fun `returns null until frame is complete`() {
        val frame = response(VmcProtocol.Command.DEVICE_ID, "VMC-1".toByteArray())
        repeat(frame.size - 1) { index ->
            assertNull(reader.append(frame[index]))
        }
        val completed = reader.append(frame.last())
        assertEquals(frame.toList(), completed!!.toList())
    }

    private fun response(command: Int, payload: ByteArray): ByteArray {
        val unverified = byteArrayOf(
            0xFF.toByte(),
            0x00,
            0xAA.toByte(),
            command.toByte(),
            payload.size.toByte(),
        ) + payload
        val checksum = unverified.drop(2).fold(0) { sum, byte -> (sum + (byte.toInt() and 0xFF)) and 0xFF }
        return unverified + checksum.toByte()
    }
}
