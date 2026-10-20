package com.zuratouch.operator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zuratouch.operator.OperatorApp
import com.zuratouch.prizewheel.data.OperatorPinGuard

class OperatorViewModelFactory(private val app: OperatorApp) : ViewModelProvider.Factory {
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
