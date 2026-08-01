package com.zuratouch.prizewheel.payment

import com.zuratouch.prizewheel.vending.FakeSerialTransport
import com.zuratouch.prizewheel.vending.VmcProtocol
import com.zuratouch.prizewheel.vending.VmcSession
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.ArrayDeque

class VmcCashlessPaymentRefundTest {
    @Test
    fun `refund sends refund coins command`() = runTest {
        val transport = FakeSerialTransport(
            ArrayDeque(
                listOf(
                    frame(VmcProtocol.Command.REFUND_COINS, byteArrayOf(0xFF.toByte())),
                ),
            ),
        )
        val session = VmcSession(transport)
        val payment = VmcCashlessPayment(session)

        assertTrue(payment.refundPayment(200, 1))
    }

    private fun frame(command: Int, payload: ByteArray): ByteArray {
        val unverified = byteArrayOf(
            0xFF.toByte(), 0x00, 0xAA.toByte(), command.toByte(), payload.size.toByte(),
        ) + payload
        val checksum = unverified.drop(2).fold(0) { sum, byte -> (sum + (byte.toInt() and 0xFF)) and 0xFF }
        return unverified + checksum.toByte()
    }
}
