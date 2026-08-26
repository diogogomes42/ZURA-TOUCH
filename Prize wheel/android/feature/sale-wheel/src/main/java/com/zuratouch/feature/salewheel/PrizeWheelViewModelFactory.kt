package com.zuratouch.feature.salewheel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zuratouch.prizewheel.data.StockDataSource
import com.zuratouch.prizewheel.domain.PrizeEngine
import com.zuratouch.prizewheel.feedback.FeedbackController
import com.zuratouch.prizewheel.payment.PaymentTerminal
import com.zuratouch.prizewheel.vending.VendingMachine

data class WheelSaleEnvironment(
    val stockRepository: StockDataSource,
    val vendingMachine: VendingMachine,
    val paymentTerminal: PaymentTerminal,
    val feedbackManager: FeedbackController,
)

class PrizeWheelViewModelFactory(
    private val environment: WheelSaleEnvironment,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrizeWheelViewModel::class.java)) {
            return PrizeWheelViewModel(
                engine = PrizeEngine(),
                stockRepository = environment.stockRepository,
                vendingMachine = environment.vendingMachine,
                paymentTerminal = environment.paymentTerminal,
                feedbackManager = environment.feedbackManager,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
