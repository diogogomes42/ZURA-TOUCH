package com.zuratouch.prizewheel.vending

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class VmcProtocolTest {
    @Test fun `payment command has cents in little endian and selected lane`() {
        assertArrayEquals(
            byteArrayOf(0xFF.toByte(), 0x00, 0x55, 0x11, 0x06, 0xC8.toByte(), 0x00, 0x00, 0x00, 0x02, 0x07, 0x3D),
            VmcProtocol.startPayment(200, VmcProtocol.PaymentMethod.CASHLESS, 7),
        )
    }

    @Test fun `parses a successful delivery status`() {
        val response = VmcProtocol.parse(byteArrayOf(0xFF.toByte(), 0x00, 0xAA.toByte(), 0xE1.toByte(), 0x01, 0x01, 0x8D.toByte()))
        assertEquals(VmcResponse.DeliveryCompleted, response)
    }
}
