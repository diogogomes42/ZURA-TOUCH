package com.zuratouch.prizewheel.vending

import com.zuratouch.prizewheel.domain.DispenseResult
import com.zuratouch.prizewheel.domain.Spiral

class VmcVendingMachine(private val session: VmcSession) : VendingMachine {
    override suspend fun dispense(spiral: Spiral): DispenseResult {
        session.ensureOpen().getOrElse {
            return DispenseResult.Failed("Erro de comunicação com a máquina.")
        }

        when (val accepted = session.send(VmcProtocol.dispense(spiral.vmcLane))) {
            is VmcResponse.DispenseAccepted -> Unit
            is VmcResponse.DeliveryFailed -> return DispenseResult.Failed("Não foi possível entregar o prémio.")
            else -> return DispenseResult.Failed("Comando de entrega rejeitado.")
        }

        return when (
            val status = session.pollStatus(spiral.vmcLane) {
                it is VmcResponse.DeliveryCompleted || it is VmcResponse.DeliveryFailed
            }
        ) {
            VmcResponse.DeliveryCompleted -> DispenseResult.Delivered
            is VmcResponse.DeliveryFailed -> {
                session.send(VmcProtocol.clearFault())
                DispenseResult.Failed("Não foi possível entregar o prémio.")
            }
            null -> {
                session.send(VmcProtocol.clearFault())
                DispenseResult.Failed("Tempo de entrega esgotado.")
            }
            else -> DispenseResult.Failed("Resposta de entrega inválida.")
        }
    }
}
