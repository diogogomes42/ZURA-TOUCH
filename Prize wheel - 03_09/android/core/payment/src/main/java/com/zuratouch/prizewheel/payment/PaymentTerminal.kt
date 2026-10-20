package com.zuratouch.prizewheel.payment

sealed interface PaymentResult {
    data object Completed : PaymentResult
    data class Failed(val reason: String) : PaymentResult
    data object Cancelled : PaymentResult
}

interface PaymentTerminal {
    suspend fun requestPayment(amountCents: Long, lane: Int): PaymentResult
    suspend fun cancel()
    suspend fun refundPayment(amountCents: Long, lane: Int): Boolean
}
