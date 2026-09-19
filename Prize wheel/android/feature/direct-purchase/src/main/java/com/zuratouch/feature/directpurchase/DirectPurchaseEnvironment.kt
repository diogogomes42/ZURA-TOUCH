package com.zuratouch.feature.directpurchase

import com.zuratouch.prizewheel.data.StockDataSource
import com.zuratouch.prizewheel.feedback.FeedbackController
import com.zuratouch.prizewheel.payment.PaymentTerminal
import com.zuratouch.prizewheel.vending.VendingMachine

data class DirectPurchaseEnvironment(
    val stockRepository: StockDataSource,
    val paymentTerminal: PaymentTerminal,
    val vendingMachine: VendingMachine,
    val feedbackManager: FeedbackController,
)
