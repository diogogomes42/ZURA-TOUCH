package com.zuratouch.prizewheel.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zuratouch.prizewheel.ZuraTouchApp
import com.zuratouch.prizewheel.data.OperatorPinGuard
import com.zuratouch.prizewheel.domain.PrizeWheelEngine

class PrizeWheelViewModelFactory(private val app: ZuraTouchApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrizeWheelViewModel::class.java)) {
            return PrizeWheelViewModel(
                engine = PrizeWheelEngine(),
                stockRepository = app.stockRepository,
                vendingMachine = app.vendingMachine,
                paymentTerminal = app.paymentTerminal,
                feedbackManager = app.feedbackManager,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class OperatorViewModelFactory(private val app: ZuraTouchApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OperatorViewModel::class.java)) {
            return OperatorViewModel(
                stockRepository = app.stockRepository,
                pinGuard = OperatorPinGuard(app),
                vmcSessionProvider = { app.vmcSession },
                hardwareStatus = app.hardwareStatus,
                retryHardwareConnection = app::retryHardwareConnection,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
