package com.zuratouch.feature.salecards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras

class CardGameViewModelFactory(
    private val environment: CardGameEnvironment,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(CardGameViewModel::class.java)) {
            return CardGameViewModel(
                stockRepository = environment.stockRepository,
                vendingMachine = environment.vendingMachine,
                paymentTerminal = environment.paymentTerminal,
                feedbackManager = environment.feedbackManager,
                savedStateHandle = extras.createSavedStateHandle(),
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
