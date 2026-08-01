package com.zuratouch.prizewheel.payment

import kotlinx.coroutines.delay

class FakePaymentTerminal : PaymentTerminal {
    override suspend fun requestPayment(amountCents: Long, lane: Int): PaymentResult {
        delay(900)
        return PaymentResult.Completed
    }

    override suspend fun cancel() = Unit

    override suspend fun refundPayment(amountCents: Long, lane: Int): Boolean {
        delay(600)
        return true
    }
}
