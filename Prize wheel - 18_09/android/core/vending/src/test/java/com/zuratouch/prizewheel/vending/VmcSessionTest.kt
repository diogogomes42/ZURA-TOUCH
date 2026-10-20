package com.zuratouch.prizewheel.vending

import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.ArrayDeque

class VmcSessionTest {
    @Test
    fun `send opens transport without deadlocking when session starts closed`() = runTest {
        val transport = FakeSerialTransport(
            ArrayDeque(
                listOf(
                    response(VmcProtocol.Command.DEVICE_ID, "VMC-1".toByteArray()),
                ),
            ),
        )
        val session = VmcSession(transport, initiallyOpen = false)

        val result = async {
            withTimeout(2_000) {
                session.send(VmcProtocol.deviceId())
            }
        }

        assertTrue(result.await() is VmcResponse.DeviceId)
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
