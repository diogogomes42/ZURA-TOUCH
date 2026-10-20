package com.zuratouch.prizewheel.hardware

import com.zuratouch.prizewheel.payment.FakePaymentTerminal
import com.zuratouch.prizewheel.payment.PaymentTerminal
import com.zuratouch.prizewheel.payment.VmcCashlessPayment
import com.zuratouch.prizewheel.vending.FakeVendingMachine
import com.zuratouch.prizewheel.vending.FileSerialTransport
import com.zuratouch.prizewheel.vending.SerialTransport
import com.zuratouch.prizewheel.vending.VendingMachine
import com.zuratouch.prizewheel.vending.VmcSession
import com.zuratouch.prizewheel.vending.VmcVendingMachine

sealed interface HardwareStatus {
    data object Simulated : HardwareStatus
    data object Connected : HardwareStatus
    data class Degraded(val serialPath: String, val reason: String) : HardwareStatus
}

data class HardwareDependencies(
    val vendingMachine: VendingMachine,
    val paymentTerminal: PaymentTerminal,
    val vmcSession: VmcSession?,
)

data class HardwareBootstrapResult(
    val dependencies: HardwareDependencies,
    val status: HardwareStatus,
)

suspend fun bootstrapHardware(
    useFakeHardware: Boolean,
    serialPath: String,
    createTransport: (String) -> SerialTransport = { FileSerialTransport(it) },
): HardwareBootstrapResult {
    if (useFakeHardware) {
        return HardwareBootstrapResult(
            dependencies = HardwareDependencies(
                vendingMachine = FakeVendingMachine(),
                paymentTerminal = FakePaymentTerminal(),
                vmcSession = null,
            ),
            status = HardwareStatus.Simulated,
        )
    }

    val transport = createTransport(serialPath)
    return transport.open().fold(
        onSuccess = {
            val session = VmcSession(transport, initiallyOpen = true)
            HardwareBootstrapResult(
                dependencies = HardwareDependencies(
                    vendingMachine = VmcVendingMachine(session),
                    paymentTerminal = VmcCashlessPayment(session),
                    vmcSession = session,
                ),
                status = HardwareStatus.Connected,
            )
        },
        onFailure = { error ->
            transport.close()
            HardwareBootstrapResult(
                dependencies = HardwareDependencies(
                    vendingMachine = FakeVendingMachine(),
                    paymentTerminal = FakePaymentTerminal(),
                    vmcSession = null,
                ),
                status = HardwareStatus.Degraded(
                    serialPath = serialPath,
                    reason = error.message ?: "Não foi possível abrir a porta serial.",
                ),
            )
        },
    )
}
