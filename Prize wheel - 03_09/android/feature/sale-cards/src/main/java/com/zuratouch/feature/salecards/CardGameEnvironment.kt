package com.zuratouch.feature.salecards

import com.zuratouch.prizewheel.data.StockDataSource
import com.zuratouch.prizewheel.feedback.FeedbackController
import com.zuratouch.prizewheel.payment.PaymentTerminal
import com.zuratouch.prizewheel.vending.VendingMachine

data class CardGameEnvironment(
    val stockRepository: StockDataSource,
    val vendingMachine: VendingMachine,
    val paymentTerminal: PaymentTerminal,
    val feedbackManager: FeedbackController,
)
