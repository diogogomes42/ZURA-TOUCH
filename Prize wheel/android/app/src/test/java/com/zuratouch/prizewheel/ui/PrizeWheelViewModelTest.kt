package com.zuratouch.prizewheel.ui

import com.zuratouch.prizewheel.data.AppConfig
import com.zuratouch.prizewheel.data.StockDataSource
import com.zuratouch.prizewheel.data.StockSnapshot
import com.zuratouch.prizewheel.domain.DispenseResult
import com.zuratouch.prizewheel.domain.PrizeCategory
import com.zuratouch.prizewheel.domain.PrizeWheelEngine
import com.zuratouch.prizewheel.domain.QueuedProduct
import com.zuratouch.prizewheel.domain.SaleLogResult
import com.zuratouch.prizewheel.domain.SaleState
import com.zuratouch.prizewheel.domain.Spiral
import com.zuratouch.prizewheel.feedback.FeedbackController
import com.zuratouch.prizewheel.payment.PaymentResult
import com.zuratouch.prizewheel.payment.PaymentTerminal
import com.zuratouch.prizewheel.vending.VendingMachine
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrizeWheelViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val categories = listOf(
        PrizeCategory("common", "Comum", 0xFF718096),
        PrizeCategory("rare", "Raro", 0xFF3182CE),
    )
    private val stock = listOf(
        Spiral("A1", 1, 1, 1, 10, listOf(QueuedProduct("common", "Água"))),
        Spiral("B1", 2, 1, 2, 10, listOf(QueuedProduct("rare", "Barra"))),
    )
    private val snapshot = StockSnapshot(
        categories = categories,
        stock = stock,
        config = AppConfig(200, "/dev/ttyS0", "Mystery Box", true),
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `purchase completes spin and reveal on happy path`() = runTest {
        val singleSnapshot = snapshot.copy(
            categories = listOf(categories.first()),
            stock = listOf(stock.first()),
        )
        val payment = RecordingPaymentTerminal(PaymentResult.Completed)
        val vending = RecordingVendingMachine(DispenseResult.Delivered)
        val repository = FakeStockDataSource(singleSnapshot)
        val viewModel = createViewModel(payment, vending, repository, singleSnapshot)

        advanceUntilIdle()
        viewModel.purchaseSpin()
        advanceUntilIdle()

        assertEquals(SaleState.SPINNING, viewModel.uiState.value.saleState)
        assertEquals(listOf(categories.first()), viewModel.uiState.value.categories)

        viewModel.onSpinAnimationComplete(viewModel.uiState.value.targetRotation)
        runCurrent()

        assertEquals(SaleState.REVEALING_PRIZE, viewModel.uiState.value.saleState)
        assertEquals("Água", viewModel.uiState.value.revealedPrize)
        assertTrue(repository.deliveryRegistered)
    }

    @Test
    fun `operator access is blocked while sale is active`() = runTest {
        val payment = RecordingPaymentTerminal(PaymentResult.Completed, holdIndefinitely = true)
        val viewModel = createViewModel(
            payment,
            RecordingVendingMachine(DispenseResult.Delivered),
            FakeStockDataSource(snapshot),
        )

        advanceUntilIdle()
        viewModel.purchaseSpin()
        advanceUntilIdle()

        assertEquals(SaleState.PAYMENT_PENDING, viewModel.uiState.value.saleState)
        assertFalse(viewModel.uiState.value.canOpenOperator)
    }

    @Test
    fun `createSale freezes wheel categories for the whole sale`() {
        val engine = PrizeWheelEngine()
        val sale = engine.createSale(categories, stock)
        require(sale != null)
        assertEquals(categories, sale.wheelCategories)
    }

    private fun createViewModel(
        payment: RecordingPaymentTerminal,
        vending: RecordingVendingMachine,
        repository: FakeStockDataSource,
        initialSnapshot: StockSnapshot = snapshot,
    ): PrizeWheelViewModel {
        val viewModel = PrizeWheelViewModel(
            engine = PrizeWheelEngine(),
            stockRepository = repository,
            vendingMachine = vending,
            paymentTerminal = payment,
            feedbackManager = NoOpFeedbackController,
        )
        repository.emit(initialSnapshot)
        return viewModel
    }
}

private object NoOpFeedbackController : FeedbackController {
    override fun setSoundEnabled(enabled: Boolean) = Unit
    override fun buttonTap() = Unit
    override fun paymentConfirmed() = Unit
    override fun spinStart() = Unit
    override fun spinTick() = Unit
    override fun win() = Unit
    override fun error() = Unit
}

private class FakeStockDataSource(initial: StockSnapshot) : StockDataSource {
    private val snapshotFlow = MutableStateFlow(initial)
    override val stockSnapshot = snapshotFlow
    override val recentSales = MutableStateFlow(emptyList<com.zuratouch.prizewheel.domain.SaleLogEntry>())
    var deliveryRegistered = false

    fun emit(snapshot: StockSnapshot) {
        snapshotFlow.value = snapshot
    }

    override suspend fun getSnapshot(): StockSnapshot = snapshotFlow.value

    override suspend fun registerDelivery(spiralId: String): Boolean {
        deliveryRegistered = true
        return true
    }

    override suspend fun setSpiralQueue(spiralId: String, queue: List<QueuedProduct>, maxCapacity: Int) = Unit

    override suspend fun updateConfig(
        priceCents: Long,
        serialPath: String,
        label: String,
        newPin: String?,
        soundEnabled: Boolean,
    ) = Unit

    override suspend fun logSale(
        categoryLabel: String,
        productName: String?,
        vmcLane: Int,
        result: SaleLogResult,
        message: String?,
    ) = Unit

    override suspend fun getSerialPortPath(): String = snapshotFlow.value.config.serialPortPath

    override suspend fun isOperatorPinConfigured(): Boolean = true

    override suspend fun setOperatorPin(pin: String) = Unit

    override suspend fun verifyOperatorPin(pin: String): Boolean = false
}

private class RecordingPaymentTerminal(
    private val result: PaymentResult,
    private val holdIndefinitely: Boolean = false,
) : PaymentTerminal {
    var refundCalled = false
    private val hold = CompletableDeferred<Unit>()

    override suspend fun requestPayment(amountCents: Long, lane: Int): PaymentResult {
        if (holdIndefinitely) hold.await()
        return result
    }

    override suspend fun cancel() = Unit

    override suspend fun refundPayment(amountCents: Long, lane: Int): Boolean {
        refundCalled = true
        return true
    }
}

private class RecordingVendingMachine(
    private val result: DispenseResult,
) : VendingMachine {
    override suspend fun dispense(spiral: Spiral): DispenseResult = result
}
