package com.zuratouch.feature.salecards

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zuratouch.prizewheel.data.CardGameSessionCodec
import com.zuratouch.prizewheel.data.StockDataSource
import com.zuratouch.prizewheel.domain.CardBoard
import com.zuratouch.prizewheel.domain.CardBoardGenerator
import com.zuratouch.prizewheel.domain.CardGameSession
import com.zuratouch.prizewheel.domain.CardSlot
import com.zuratouch.prizewheel.domain.DispensePlan
import com.zuratouch.prizewheel.domain.DispenseResult
import com.zuratouch.prizewheel.domain.PrizeCategory
import com.zuratouch.prizewheel.domain.PrizeEngine
import com.zuratouch.prizewheel.domain.SaleLogResult
import com.zuratouch.prizewheel.domain.SaleState
import com.zuratouch.prizewheel.domain.Spiral
import com.zuratouch.prizewheel.feedback.FeedbackController
import com.zuratouch.prizewheel.payment.PaymentResult
import com.zuratouch.prizewheel.payment.PaymentTerminal
import com.zuratouch.prizewheel.vending.VendingMachine
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

data class CardGameUiState(
    val saleState: SaleState = SaleState.IDLE,
    val playPriceCents: Long = 200,
    val mysteryBoxLabel: String = "Mystery Box",
    val availablePrizes: List<AvailablePrizeDisplay> = emptyList(),
    val board: CardBoard? = null,
    val selectedCardIndex: Int? = null,
    val revealedPrize: String? = null,
    val message: String? = null,
    val canPlay: Boolean = false,
    val isBusy: Boolean = false,
)

class CardGameViewModel(
    private val engine: PrizeEngine = PrizeEngine(),
    private val boardGenerator: CardBoardGenerator = CardBoardGenerator(engine),
    private val stockRepository: StockDataSource,
    private val vendingMachine: VendingMachine,
    private val paymentTerminal: PaymentTerminal,
    private val feedbackManager: FeedbackController,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CardGameUiState())
    val uiState: StateFlow<CardGameUiState> = _uiState.asStateFlow()

    private var categories: List<PrizeCategory> = emptyList()
    private var stock: List<Spiral> = emptyList()
    private var playPriceCents: Long = 200
    private var mysteryBoxLabel: String = "Mystery Box"
    private var activeSession: CardGameSession? = null
    private var paymentConfirmed = false
    private var purchaseJob: Job? = null
    private var isProcessing = false
    private var errorReturnJob: Job? = null
    private var revealReturnJob: Job? = null
    private var idleDisplayRandom: Random = Random(System.nanoTime())
    private var dispenseStarted = false

    init {
        restoreSession()
        viewModelScope.launch {
            stockRepository.stockSnapshot.collect { snapshot ->
                categories = snapshot.categories
                stock = snapshot.stock
                playPriceCents = snapshot.config.spinPriceCents
                mysteryBoxLabel = snapshot.config.mysteryBoxLabel
                feedbackManager.setSoundEnabled(snapshot.config.soundEnabled)
                val canPlay = boardGenerator.canGenerateBoard(categories, stock)
                val availablePrizes = if (_uiState.value.saleState == SaleState.IDLE) {
                    AvailablePrizes.fromStock(categories, stock, idleDisplayRandom)
                } else {
                    AvailablePrizes.fromStock(categories, stock)
                }
                _uiState.update { current ->
                    if (current.saleState == SaleState.IDLE) {
                        current.copy(
                            playPriceCents = playPriceCents,
                            mysteryBoxLabel = mysteryBoxLabel,
                            availablePrizes = availablePrizes,
                            canPlay = canPlay,
                            message = if (canPlay) null else "Modo cartas indisponível — reponha stock.",
                        )
                    } else {
                        current.copy(
                            playPriceCents = playPriceCents,
                            mysteryBoxLabel = mysteryBoxLabel,
                            availablePrizes = availablePrizes,
                        )
                    }
                }
            }
        }
    }

    fun startRound() {
        if (_uiState.value.saleState != SaleState.IDLE || isProcessing) return
        if (!boardGenerator.canGenerateBoard(categories, stock)) {
            showError("Não há prémios disponíveis para cartas.")
            return
        }
        isProcessing = true
        _uiState.update { it.copy(isBusy = true) }
        paymentConfirmed = false
        dispenseStarted = false
        feedbackManager.buttonTap()
        purchaseJob?.cancel()
        purchaseJob = viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        selectedCardIndex = null,
                        revealedPrize = null,
                        board = null,
                        message = null,
                    )
                }
                val paymentLane = resolvePaymentLane()
                when (val payment = paymentTerminal.requestPayment(playPriceCents, paymentLane)) {
                    PaymentResult.Completed -> {
                        paymentConfirmed = true
                        feedbackManager.paymentConfirmed()
                        val board = boardGenerator.generate(
                            categories = categories,
                            stock = stock,
                            random = Random(System.nanoTime()),
                        )
                        if (board == null) {
                            paymentTerminal.refundPayment(playPriceCents, paymentLane)
                            isProcessing = false
                            paymentConfirmed = false
                            _uiState.update { it.copy(isBusy = false) }
                            showError("Não foi possível gerar o tabuleiro.")
                            return@launch
                        }
                        val session = CardGameSession(board = board, paymentConfirmed = true)
                        persistSession(session)
                        activeSession = session
                        _uiState.update {
                            it.copy(
                                saleState = SaleState.PLAYING,
                                board = board,
                                message = null,
                                isBusy = false,
                            )
                        }
                        isProcessing = false
                    }
                    PaymentResult.Cancelled -> {
                        logPaymentFailure("Pagamento cancelado", paymentLane)
                        isProcessing = false
                        _uiState.update { it.copy(isBusy = false) }
                        showError("Pagamento cancelado.")
                    }
                    is PaymentResult.Failed -> {
                        logPaymentFailure(payment.reason, paymentLane)
                        isProcessing = false
                        _uiState.update { it.copy(isBusy = false) }
                        showError(payment.reason)
                    }
                }
            } catch (_: CancellationException) {
                // Cleanup handled in onCleared().
            }
        }
    }

    fun selectCard(index: Int) {
        val session = activeSession ?: return
        if (_uiState.value.saleState != SaleState.PLAYING || isProcessing) return
        if (_uiState.value.selectedCardIndex != null) return
        val slot = session.board.slots.firstOrNull { it.index == index } ?: return
        isProcessing = true
        feedbackManager.buttonTap()
        feedbackManager.win()
        val updatedSession = session.copy(selectedSlot = slot)
        activeSession = updatedSession
        persistSession(updatedSession)
        _uiState.update {
            it.copy(selectedCardIndex = index)
        }
    }

    fun onRevealAnimationComplete() {
        val session = activeSession ?: return
        if (session.selectedSlot == null) return
        val winSlot = session.board.slots.firstOrNull { it.isTargetSlot } ?: return
        if (_uiState.value.saleState != SaleState.PLAYING || dispenseStarted) return
        viewModelScope.launch { dispense(winSlot) }
    }

    fun finishRound() {
        revealReturnJob?.cancel()
        if (_uiState.value.revealedPrize != null) {
            returnToIdle()
            return
        }
        val winSlot = activeSession?.board?.slots?.firstOrNull { it.isTargetSlot }
        if (winSlot != null && !dispenseStarted) {
            viewModelScope.launch {
                dispense(winSlot)
                returnToIdle()
            }
        } else {
            returnToIdle()
        }
    }

    private suspend fun dispense(slot: CardSlot) {
        if (dispenseStarted) return
        dispenseStarted = true
        _uiState.update {
            it.copy(
                saleState = SaleState.DISPENSING,
                message = "A preparar o seu prémio…",
            )
        }
        val plan = dispenseWithFallback(slot)
        val paymentLane = slot.dispensePlan.spiral.vmcLane
        if (plan != null) {
            stockRepository.registerDelivery(plan.spiral.id)
            refreshStockFromRepository()
            stockRepository.logSale(
                categoryLabel = slot.category.label,
                productName = plan.product.productName,
                vmcLane = plan.spiral.vmcLane,
                result = SaleLogResult.SUCCESS,
                message = buildSaleLogMessage(slot),
            )
            clearSession()
            _uiState.update {
                it.copy(
                    saleState = SaleState.PLAYING,
                    revealedPrize = plan.product.productName,
                    message = "Retire o seu prémio abaixo.",
                )
            }
            isProcessing = false
            paymentConfirmed = false
            activeSession = null
            schedulePostWinReturn()
        } else {
            val refunded = paymentTerminal.refundPayment(playPriceCents, paymentLane)
            stockRepository.logSale(
                categoryLabel = slot.category.label,
                productName = null,
                vmcLane = paymentLane,
                result = if (refunded) SaleLogResult.REFUNDED else SaleLogResult.DISPENSE_FAILED,
                message = buildSaleLogMessage(slot) + " · dispense failed",
            )
            clearSession()
            isProcessing = false
            paymentConfirmed = false
            activeSession = null
            showError(
                if (refunded) {
                    "Não foi possível entregar o prémio. O valor será reembolsado."
                } else {
                    "Não foi possível entregar o prémio. Contacte o operador."
                },
            )
        }
    }

    private fun buildSaleLogMessage(slot: CardSlot): String {
        val session = activeSession
        return buildString {
            append("experience=CARDS")
            session?.board?.boardSeed?.let { append(";boardSeed=$it") }
            append(";selectedCardIndex=${slot.index}")
            append(";wasTargetSlot=${slot.isTargetSlot}")
        }
    }

    private suspend fun dispenseWithFallback(slot: CardSlot): DispensePlan? {
        val freshStock = stockRepository.getSnapshot().stock
        val categoryId = slot.dispensePlan.product.categoryId
        val preferredId = slot.dispensePlan.spiral.id
        val spiralsToTry = buildList {
            freshStock.find { it.id == preferredId }?.let { add(it) }
            addAll(engine.spiralsForDispense(freshStock, categoryId).filter { it.id != preferredId })
        }
        for (spiral in spiralsToTry) {
            if (spiral.isEmpty) continue
            when (vendingMachine.dispense(spiral)) {
                DispenseResult.Delivered -> {
                    val product = spiral.frontProduct ?: continue
                    return DispensePlan(spiral, product)
                }
                is DispenseResult.Failed -> Unit
            }
        }
        return null
    }

    fun returnToIdle() {
        errorReturnJob?.cancel()
        revealReturnJob?.cancel()
        clearSession()
        activeSession = null
        paymentConfirmed = false
        isProcessing = false
        dispenseStarted = false
        viewModelScope.launch {
            refreshStockFromRepository()
            applyIdleState()
        }
    }

    private suspend fun refreshStockFromRepository() {
        val snapshot = stockRepository.getSnapshot()
        categories = snapshot.categories
        stock = snapshot.stock
        playPriceCents = snapshot.config.spinPriceCents
        mysteryBoxLabel = snapshot.config.mysteryBoxLabel
    }

    private fun applyIdleState() {
        idleDisplayRandom = Random(System.nanoTime())
        val canPlay = boardGenerator.canGenerateBoard(categories, stock)
        _uiState.value = CardGameUiState(
            saleState = SaleState.IDLE,
            playPriceCents = playPriceCents,
            mysteryBoxLabel = mysteryBoxLabel,
            availablePrizes = AvailablePrizes.fromStock(categories, stock, idleDisplayRandom),
            canPlay = canPlay,
            message = if (canPlay) null else "Modo cartas indisponível — reponha stock.",
        )
    }

    private fun showError(message: String) {
        feedbackManager.error()
        errorReturnJob?.cancel()
        revealReturnJob?.cancel()
        clearSession()
        activeSession = null
        paymentConfirmed = false
        _uiState.update {
            it.copy(
                saleState = SaleState.ERROR,
                message = message,
                board = null,
                selectedCardIndex = null,
                revealedPrize = null,
            )
        }
        errorReturnJob = viewModelScope.launch {
            delay(ERROR_AUTO_RETURN_MS)
            if (_uiState.value.saleState == SaleState.ERROR) returnToIdle()
        }
    }

    private fun schedulePostWinReturn() {
        revealReturnJob?.cancel()
        revealReturnJob = viewModelScope.launch {
            delay(POST_WIN_RETURN_MS)
            if (_uiState.value.saleState == SaleState.PLAYING && _uiState.value.revealedPrize != null) {
                returnToIdle()
            }
        }
    }

    private fun restoreSession() {
        val json = savedStateHandle.get<String>(CardGameSessionCodec.SAVED_STATE_KEY) ?: return
        val session = CardGameSessionCodec.decode(json) ?: return
        if (!session.paymentConfirmed) return
        activeSession = session
        paymentConfirmed = true
        val selected = session.selectedSlot
        _uiState.value = CardGameUiState(
            saleState = when {
                selected != null -> SaleState.DISPENSING
                else -> SaleState.PLAYING
            },
            playPriceCents = playPriceCents,
            board = session.board,
            selectedCardIndex = selected?.index,
            canPlay = false,
        )
        if (selected != null) {
            viewModelScope.launch { dispense(selected) }
        }
    }

    private fun persistSession(session: CardGameSession?) {
        if (session == null) {
            savedStateHandle.remove<String>(CardGameSessionCodec.SAVED_STATE_KEY)
        } else {
            savedStateHandle[CardGameSessionCodec.SAVED_STATE_KEY] = CardGameSessionCodec.encode(session)
        }
        activeSession = session
    }

    private fun clearSession() = persistSession(null)

    private fun resolvePaymentLane(): Int =
        engine.dispensableCategories(categories, stock)
            .firstNotNullOfOrNull { category ->
                engine.pickSpiralForCategory(stock, category.id)?.vmcLane
            } ?: 1

    private suspend fun logPaymentFailure(reason: String, lane: Int) {
        stockRepository.logSale(
            categoryLabel = "Cartas",
            productName = null,
            vmcLane = lane,
            result = SaleLogResult.PAYMENT_FAILED,
            message = reason,
        )
    }

    override fun onCleared() {
        purchaseJob?.cancel()
        val session = activeSession
        if (session != null && paymentConfirmed) {
            runBlocking {
                val lane = session.board.slots.firstOrNull()?.dispensePlan?.spiral?.vmcLane ?: 1
                val state = _uiState.value.saleState
                if (state == SaleState.PAYMENT_PENDING) {
                    paymentTerminal.cancel()
                } else if (state == SaleState.PLAYING || state == SaleState.DISPENSING) {
                    paymentTerminal.refundPayment(playPriceCents, lane)
                }
            }
        }
        super.onCleared()
    }

    companion object {
        const val ERROR_AUTO_RETURN_MS = 8_000L
        const val POST_WIN_RETURN_MS = 5_000L
    }
}
