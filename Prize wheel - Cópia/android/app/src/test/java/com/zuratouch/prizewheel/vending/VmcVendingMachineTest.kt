package com.zuratouch.prizewheel.vending

import com.zuratouch.prizewheel.domain.DispenseResult
import com.zuratouch.prizewheel.domain.ProductSlot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.ArrayDeque

class VmcVendingMachineTest {
    private val slot = ProductSlot("A1", "drink", "Água", 1, 1, 3, 1)

    @Test fun `delivers when machine confirms completion`() = runTest {
        val transport = FakeSerialTransport(
            ArrayDeque(
                listOf(
                    response(VmcProtocol.Command.DISPENSE, byteArrayOf(3, 1)),
                    response(VmcProtocol.Command.QUERY_STATUS, byteArrayOf(0x01)),
                ),
            ),
        )
        val machine = VmcVendingMachine(VmcSession(transport))

        assertEquals(DispenseResult.Delivered, machine.dispense(slot))
        assertTrue(transport.written.first().contentEquals(VmcProtocol.dispense(3)))
    }

    @Test fun `fails when delivery status is not completed`() = runTest {
        val transport = FakeSerialTransport(
            ArrayDeque(
                listOf(
                    response(VmcProtocol.Command.DISPENSE, byteArrayOf(3, 1)),
                    response(VmcProtocol.Command.QUERY_STATUS, byteArrayOf(0x02)),
                ),
            ),
        )
        val machine = VmcVendingMachine(VmcSession(transport))

        val result = machine.dispense(slot)
        assertTrue(result is DispenseResult.Failed)
    }

    private fun response(command: Int, payload: ByteArray): ByteArray {
        val unverified = byteArrayOf(0xFF.toByte(), 0x00, 0xAA.toByte(), command.toByte(), payload.size.toByte()) + payload
        val checksum = unverified.copyOfRange(2, unverified.size).fold(0) { sum, byte -> (sum + (byte.toInt() and 0xFF)) and 0xFF }
        return unverified + checksum.toByte()
    }
}
