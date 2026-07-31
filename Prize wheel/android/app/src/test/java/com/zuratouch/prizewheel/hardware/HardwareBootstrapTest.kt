package com.zuratouch.prizewheel.hardware

import com.zuratouch.prizewheel.payment.FakePaymentTerminal
import com.zuratouch.prizewheel.payment.VmcCashlessPayment
import com.zuratouch.prizewheel.vending.FakeSerialTransport
import com.zuratouch.prizewheel.vending.FakeVendingMachine
import com.zuratouch.prizewheel.vending.VmcVendingMachine
import java.util.ArrayDeque
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HardwareBootstrapTest {
    @Test
    fun `uses simulated hardware in debug mode`() = runTest {
        val result = bootstrapHardware(useFakeHardware = true, serialPath = "/dev/ttyS0")

        assertEquals(HardwareStatus.Simulated, result.status)
        assertTrue(result.dependencies.vendingMachine is FakeVendingMachine)
        assertTrue(result.dependencies.paymentTerminal is FakePaymentTerminal)
        assertNull(result.dependencies.vmcSession)
    }

    @Test
    fun `connects when serial port opens`() = runTest {
        val transport = FakeSerialTransport(ArrayDeque())
        val result = bootstrapHardware(
            useFakeHardware = false,
            serialPath = "/dev/ttyS0",
            createTransport = { transport },
        )

        assertEquals(HardwareStatus.Connected, result.status)
        assertTrue(result.dependencies.vendingMachine is VmcVendingMachine)
        assertTrue(result.dependencies.paymentTerminal is VmcCashlessPayment)
        assertTrue(result.dependencies.vmcSession != null)
    }

    @Test
    fun `falls back to degraded mode when serial port fails`() = runTest {
        val transport = FakeSerialTransport(ArrayDeque()).apply {
            openResult = Result.failure(IllegalStateException("Serial device not found"))
        }
        val result = bootstrapHardware(
            useFakeHardware = false,
            serialPath = "/dev/ttyS0",
            createTransport = { transport },
        )

        assertTrue(result.status is HardwareStatus.Degraded)
        val degraded = result.status as HardwareStatus.Degraded
        assertEquals("/dev/ttyS0", degraded.serialPath)
        assertEquals("Serial device not found", degraded.reason)
        assertTrue(result.dependencies.vendingMachine is FakeVendingMachine)
        assertTrue(result.dependencies.paymentTerminal is FakePaymentTerminal)
        assertNull(result.dependencies.vmcSession)
    }
}
