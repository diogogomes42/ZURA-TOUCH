package com.zuratouch.prizewheel

import android.app.Application
import com.zuratouch.prizewheel.data.StockRepository
import com.zuratouch.prizewheel.data.local.AppDatabase
import com.zuratouch.prizewheel.feedback.FeedbackManager
import com.zuratouch.prizewheel.payment.FakePaymentTerminal
import com.zuratouch.prizewheel.payment.PaymentTerminal
import com.zuratouch.prizewheel.payment.VmcCashlessPayment
import com.zuratouch.prizewheel.vending.FakeVendingMachine
import com.zuratouch.prizewheel.vending.FileSerialTransport
import com.zuratouch.prizewheel.vending.VendingMachine
import com.zuratouch.prizewheel.vending.VmcSession
import com.zuratouch.prizewheel.vending.VmcVendingMachine
import kotlinx.coroutines.runBlocking

class ZuraTouchApp : Application() {
    lateinit var stockRepository: StockRepository
        private set
    lateinit var vendingMachine: VendingMachine
        private set
    lateinit var paymentTerminal: PaymentTerminal
        private set
    lateinit var feedbackManager: FeedbackManager
        private set
    var vmcSession: VmcSession? = null
        private set

    override fun onCreate() {
        super.onCreate()
        feedbackManager = FeedbackManager(this)
        val database = AppDatabase.get(this)
        runBlocking { AppDatabase.seedIfEmpty(database) }
        stockRepository = StockRepository(
            categoryDao = database.categoryDao(),
            productSlotDao = database.productSlotDao(),
            appConfigDao = database.appConfigDao(),
            saleLogDao = database.saleLogDao(),
        )

        if (BuildConfig.USE_FAKE_HARDWARE) {
            vendingMachine = FakeVendingMachine()
            paymentTerminal = FakePaymentTerminal()
            vmcSession = null
        } else {
            val serialPath = runBlocking { stockRepository.getSerialPortPath() }
            val transport = FileSerialTransport(serialPath)
            val session = VmcSession(transport)
            vmcSession = session
            vendingMachine = VmcVendingMachine(session)
            paymentTerminal = VmcCashlessPayment(session)
        }
    }
}
