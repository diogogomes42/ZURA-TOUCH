package com.zuratouch.feature.directpurchase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DirectPurchaseViewModelFactory(
    private val environment: DirectPurchaseEnvironment,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(DirectPurchaseViewModel::class.java))
        return DirectPurchaseViewModel(
            stockRepository = environment.stockRepository,
            paymentTerminal = environment.paymentTerminal,
            vendingMachine = environment.vendingMachine,
            feedbackManager = environment.feedbackManager,
        ) as T
    }
}
