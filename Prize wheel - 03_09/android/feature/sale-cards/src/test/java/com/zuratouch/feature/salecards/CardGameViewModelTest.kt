package com.zuratouch.feature.salecards

import androidx.lifecycle.SavedStateHandle
import com.zuratouch.prizewheel.data.AppConfig
import com.zuratouch.prizewheel.data.StockDataSource
import com.zuratouch.prizewheel.data.StockSnapshot
import com.zuratouch.prizewheel.domain.DispenseResult
import com.zuratouch.prizewheel.domain.PrizeCategory
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
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CardGameViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val categories = listOf(
        PrizeCategory("common", "Comum", 0xFF718096, weight = 90),
        PrizeCategory("premium", "Premium", 0xFF3182CE, weight = 10),
        PrizeCategory("snack", "Snack", 0xFFB7791F, weight = 30),
    )

    private fun spiral(id: String, shelf: Int, slot: Int, queue: List<QueuedProduct>) =
        Spiral(id, shelf, slot, shelf * 10 + slot, 15, queue)

    private val stock = listOf(
        spiral("A1", 1, 1, List(5) { QueuedProduct("common", "Água") }),
        spiral("B1", 1, 2, List(3) { QueuedProduct("premium", "Caixa Premium") }),
        spiral("C1", 2, 1, List(4) { QueuedProduct("snack", "Barra") }),
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
    fun `startRound generates board and pick dispenses prize on happy path`() = runTest {
        val payment = RecordingPaymentTerminal(PaymentResult.Completed)
        val vending = RecordingVendingMachine(DispenseResult.Delivered)
        val repository = FakeStockDataSource(snapshot)
        val viewModel = createViewModel(payment, vending, repository)

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.canPlay)

        viewModel.startRound()
        advanceUntilIdle()

        assertEquals(SaleState.PLAYING, viewModel.uiState.value.saleState)
        val board = viewModel.uiState.value.board
        assertNotNull(board)
        assertEquals(24, board!!.slots.size)

        viewModel.selectCard(0)
        runCurrent()
        assertEquals(SaleState.PLAYING, viewModel.uiState.value.saleState)
        assertEquals(0, viewModel.uiState.value.selectedCardIndex)

        viewModel.onRevealAnimationComplete()
        runCurrent()

        assertEquals(SaleState.PLAYING, viewModel.uiState.value.saleState)
        assertNotNull(viewModel.uiState.value.revealedPrize)
        assertTrue(repository.deliveryRegistered)
    }

    @Test
    fun `returnToIdle refreshes available prizes after delivery`() = runTest {
        val payment = RecordingPaymentTerminal(PaymentResult.Completed)
        val vending = RecordingVendingMachine(DispenseResult.Delivered)
        val repository = FakeStockDataSource(snapshot)
        val viewModel = createViewModel(payment, vending, repository)

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.availablePrizes.isNotEmpty())

        viewModel.startRound()
        advanceUntilIdle()
        viewModel.selectCard(0)
        runCurrent()
        viewModel.onRevealAnimationComplete()
        runCurrent()

        viewModel.returnToIdle()
        advanceUntilIdle()

        assertEquals(SaleState.IDLE, viewModel.uiState.value.saleState)
        assertTrue(viewModel.uiState.value.availablePrizes.isNotEmpty())
        assertTrue(repository.deliveryRegistered)
    }

    @Test
    fun `startTrialRound skips payment and does not dispense`() = runTest {
        val payment = RecordingPaymentTerminal(PaymentResult.Completed)
        val vending = RecordingVendingMachine(DispenseResult.Delivered)
        val repository = FakeStockDataSource(snapshot)
        val viewModel = createViewModel(payment, vending, repository)

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.canPlay)

        viewModel.startTrialRound()
        advanceUntilIdle()

        assertEquals(0, payment.requestCount)
        assertTrue(viewModel.uiState.value.isTrialRound)
        assertEquals(SaleState.PLAYING, viewModel.uiState.value.saleState)
        assertNotNull(viewModel.uiState.value.board)

        viewModel.selectCard(0)
        runCurrent()
        viewModel.onRevealAnimationComplete()
        runCurrent()

        assertEquals(SaleState.PLAYING, viewModel.uiState.value.saleState)
        assertNotNull(viewModel.uiState.value.revealedPrize)
        assertEquals(
            CardGameViewModel.TRIAL_COMPLETE_MESSAGE,
            viewModel.uiState.value.message,
        )
        assertFalse(repository.deliveryRegistered)
        assertEquals(0, vending.dispenseCount)
    }

    @Test
    fun `startRound blocks while payment pending`() = runTest {
        val payment = RecordingPaymentTerminal(PaymentResult.Completed, holdIndefinitely = true)
        val viewModel = createViewModel(
            payment,
            RecordingVendingMachine(DispenseResult.Delivered),
            FakeStockDataSource(snapshot),
        )

        advanceUntilIdle()
        viewModel.startRound()
        advanceUntilIdle()

        assertEquals(SaleState.IDLE, viewModel.uiState.value.saleState)
        viewModel.startRound()
        advanceUntilIdle()
        assertEquals(SaleState.IDLE, viewModel.uiState.value.saleState)
    }

    private fun createViewModel(
        payment: RecordingPaymentTerminal,
        vending: RecordingVendingMachine,
        repository: FakeStockDataSource,
        initialSnapshot: StockSnapshot = snapshot,
    ): CardGameViewModel {
        val viewModel = CardGameViewModel(
            stockRepository = repository,
            vendingMachine = vending,
            paymentTerminal = payment,
            feedbackManager = NoOpFeedbackController,
            savedStateHandle = SavedStateHandle(),
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
        val current = snapshotFlow.value
        val updatedStock = current.stock.map { spiral ->
            if (spiral.id != spiralId || spiral.queue.isEmpty()) spiral
            else spiral.copy(queue = spiral.queue.drop(1))
        }
        emit(current.copy(stock = updatedStock))
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

    override suspend fun setExperienceMode(mode: com.zuratouch.prizewheel.domain.CustomerExperience) = Unit
}

private class RecordingPaymentTerminal(
    private val result: PaymentResult,
    private val holdIndefinitely: Boolean = false,
) : PaymentTerminal {
    private val hold = CompletableDeferred<Unit>()
    var requestCount = 0

    override suspend fun requestPayment(amountCents: Long, lane: Int): PaymentResult {
        requestCount++
        if (holdIndefinitely) hold.await()
        return result
    }

    override suspend fun cancel() = Unit

    override suspend fun refundPayment(amountCents: Long, lane: Int): Boolean = true
}

private class RecordingVendingMachine(
    private val result: DispenseResult,
) : VendingMachine {
    var dispenseCount = 0

    override suspend fun dispense(spiral: Spiral): DispenseResult {
        dispenseCount++
        return result
    }
}
