package com.zuratouch.prizewheel.payment

import com.zuratouch.prizewheel.vending.VmcProtocol
import com.zuratouch.prizewheel.vending.VmcResponse
import com.zuratouch.prizewheel.vending.VmcSession

class VmcCashlessPayment(private val session: VmcSession) : PaymentTerminal {
    override suspend fun requestPayment(amountCents: Long, lane: Int): PaymentResult {
        session.ensureOpen().getOrElse {
            return PaymentResult.Failed("Erro de comunicação com a máquina.")
        }

        session.send(
            VmcProtocol.startPayment(amountCents, VmcProtocol.PaymentMethod.CASHLESS, lane),
        )

        val response = session.pollStatus(lane) {
            it is VmcResponse.PaymentCompleted || it is VmcResponse.DeliveryFailed
        }

        return when (response) {
            is VmcResponse.PaymentCompleted -> {
                if (response.amountCents >= amountCents) PaymentResult.Completed
                else PaymentResult.Failed("Pagamento não concluído.")
            }
            is VmcResponse.DeliveryFailed -> PaymentResult.Failed("Pagamento não concluído.")
            null -> {
                cancel()
                PaymentResult.Failed("Pagamento não concluído.")
            }
            else -> PaymentResult.Failed("Pagamento não concluído.")
        }
    }

    override suspend fun cancel() {
        session.send(VmcProtocol.cancelCashless())
    }

    override suspend fun refundPayment(amountCents: Long, lane: Int): Boolean {
        session.ensureOpen().getOrElse { return false }
        return when (val response = session.send(VmcProtocol.refundCoins())) {
            is VmcResponse.Acknowledged -> response.command == VmcProtocol.Command.REFUND_COINS
            else -> false
        }
    }
}
