package com.zuratouch.prizewheel.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zuratouch.prizewheel.data.StockDataSource
import com.zuratouch.prizewheel.data.StockRepository
import com.zuratouch.prizewheel.domain.DispensePlan
import com.zuratouch.prizewheel.domain.DispenseResult
import com.zuratouch.prizewheel.domain.PrizeWheelEngine
import com.zuratouch.prizewheel.domain.SaleLogResult
import com.zuratouch.prizewheel.domain.SaleSession
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

data class PrizeWheelUiState(
    val saleState: SaleState = SaleState.IDLE,
    val categories: List<com.zuratouch.prizewheel.domain.PrizeCategory> = emptyList(),
    val spinPriceCents: Long = 200,
    val mysteryBoxLabel: String = "Mystery Box",
    val winningCategory: com.zuratouch.prizewheel.domain.PrizeCategory? = null,
    val spinFromRotation: Float = 0f,
    val targetRotation: Float = 0f,
    val currentRotation: Float = 0f,
    val revealedPrize: String? = null,
    val message: String? = null,
    val isSpinning: Boolean = false,
    val spinSessionId: Long = 0L,
) {
    val canOpenOperator: Boolean get() = saleState == SaleState.IDLE
}

class PrizeWheelViewModel(
    private val engine: PrizeWheelEngine,
    private val stockRepository: StockDataSource,
    private val vendingMachine: VendingMachine,
    private val paymentTerminal: PaymentTerminal,
    private val feedbackManager: FeedbackController,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PrizeWheelUiState())
    val uiState: StateFlow<PrizeWheelUiState> = _uiState.asStateFlow()

    private var activeSale: SaleSession? = null
    private var categories: List<com.zuratouch.prizewheel.domain.PrizeCategory> = emptyList()
    private var stock = emptyList<Spiral>()
    private var spinPriceCents: Long = 200
    private var mysteryBoxLabel: String = "Mystery Box"
    private var errorReturnJob: Job? = null
    private var revealReturnJob: Job? = null
    private var purchaseJob: Job? = null
    private var isProcessing = false
    private var spinSessionCounter = 0L
    private var paymentCompletedForActiveSale = false

    init {
        viewModelScope.launch {
            stockRepository.stockSnapshot.collect { snapshot ->
                categories = snapshot.categories
                stock = snapshot.stock
                spinPriceCents = snapshot.config.spinPriceCents
                mysteryBoxLabel = snapshot.config.mysteryBoxLabel
                feedbackManager.setSoundEnabled(snapshot.config.soundEnabled)
                val available = engine.availableCategories(categories, stock)
                _uiState.update { current ->
                    if (current.saleState == SaleState.IDLE) {
                        current.copy(
                            categories = available,
                            spinPriceCents = spinPriceCents,
                            mysteryBoxLabel = mysteryBoxLabel,
                        )
                    } else {
                        current.copy(
                            spinPriceCents = spinPriceCents,
                            mysteryBoxLabel = mysteryBoxLabel,
                        )
                    }
                }
            }
        }
    }

    fun purchaseSpin() {
        if (_uiState.value.saleState != SaleState.IDLE || isProcessing) return
        isProcessing = true
        paymentCompletedForActiveSale = false
        feedbackManager.buttonTap()
        purchaseJob?.cancel()
        purchaseJob = viewModelScope.launch {
            try {
                val sale = engine.createSale(categories, stock)
                if (sale == null) {
                    isProcessing = false
                    showError("Não há prémios disponíveis.")
                    return@launch
                }
                activeSale = sale
                val paymentLane = engine.pickSpiralForCategory(stock, sale.category.id)?.vmcLane ?: 1
                _uiState.update {
                    it.copy(
                        saleState = SaleState.PAYMENT_PENDING,
                        categories = sale.wheelCategories,
                        message = "A confirmar pagamento…",
                        winningCategory = null,
                        revealedPrize = null,
                        isSpinning = false,
                    )
                }

                when (
                    val payment = paymentTerminal.requestPayment(
                        amountCents = spinPriceCents,
                        lane = paymentLane,
                    )
                ) {
                    PaymentResult.Completed -> {
                        paymentCompletedForActiveSale = true
                        feedbackManager.paymentConfirmed()
                        startSpin(sale)
                    }
                    PaymentResult.Cancelled -> {
                        stockRepository.logSale(
                            categoryLabel = sale.category.label,
                            productName = null,
                            vmcLane = paymentLane,
                            result = SaleLogResult.PAYMENT_FAILED,
                            message = "Pagamento cancelado",
                        )
                        isProcessing = false
                        showError("Pagamento cancelado.")
                    }
                    is PaymentResult.Failed -> {
                        stockRepository.logSale(
                            categoryLabel = sale.category.label,
                            productName = null,
                            vmcLane = paymentLane,
                            result = SaleLogResult.PAYMENT_FAILED,
                            message = payment.reason,
                        )
                        isProcessing = false
                        showError(payment.reason)
                    }
                }
            } catch (_: CancellationException) {
                // Cleanup handled in onCleared().
            }
        }
    }

    private fun startSpin(sale: SaleSession) {
        val startRotation = _uiState.value.currentRotation
        val target = WheelRotationCalculator.targetRotation(
            currentRotation = startRotation,
            categories = sale.wheelCategories,
            winningCategoryId = sale.category.id,
        ) ?: return
        feedbackManager.spinStart()
        spinSessionCounter += 1
        _uiState.update {
            it.copy(
                saleState = SaleState.SPINNING,
                categories = sale.wheelCategories,
                winningCategory = sale.category,
                spinFromRotation = startRotation,
                targetRotation = target,
                message = "A roda está a girar…",
                isSpinning = true,
                spinSessionId = spinSessionCounter,
            )
        }
    }

    fun onSpinTick() {
        feedbackManager.spinTick()
    }

    fun onSpinAnimationComplete(finalRotation: Float) {
        val sale = activeSale ?: return
        if (_uiState.value.saleState != SaleState.SPINNING) return
        _uiState.update {
            it.copy(currentRotation = finalRotation, isSpinning = false)
        }
        viewModelScope.launch { dispense(sale) }
    }

    private suspend fun dispense(sale: SaleSession) {
        _uiState.update {
            it.copy(
                saleState = SaleState.DISPENSING,
                message = "A preparar o seu prémio…",
                isSpinning = false,
            )
        }
        val plan = dispenseWithFallback(sale)
        if (plan != null) {
            val stockUpdated = stockRepository.registerDelivery(plan.spiral.id)
            stockRepository.logSale(
                categoryLabel = sale.category.label,
                productName = plan.product.productName,
                vmcLane = plan.spiral.vmcLane,
                result = SaleLogResult.SUCCESS,
                message = if (stockUpdated) null else "Entrega OK mas stock não foi atualizado",
            )
            feedbackManager.win()
            _uiState.update {
                it.copy(
                    saleState = SaleState.REVEALING_PRIZE,
                    revealedPrize = plan.product.productName,
                    message = null,
                )
            }
            isProcessing = false
            paymentCompletedForActiveSale = false
            scheduleRevealAutoReturn()
        } else {
            val refundLane = engine.pickSpiralForCategory(stock, sale.category.id)?.vmcLane ?: 1
            val refunded = paymentTerminal.refundPayment(spinPriceCents, refundLane)
            stockRepository.logSale(
                categoryLabel = sale.category.label,
                productName = null,
                vmcLane = refundLane,
                result = if (refunded) SaleLogResult.REFUNDED else SaleLogResult.DISPENSE_FAILED,
                message = if (refunded) "Falha na entrega · reembolso emitido" else "Falha na entrega · reembolso falhou",
            )
            isProcessing = false
            paymentCompletedForActiveSale = false
            showError(
                if (refunded) {
                    "Não foi possível entregar o prémio. O valor será reembolsado."
                } else {
                    "Não foi possível entregar o prémio. Contacte o operador."
                },
            )
        }
    }

    /** Uses fresh machine state; falls back silently to other front-eligible categories if needed. */
    private suspend fun dispenseWithFallback(sale: SaleSession): DispensePlan? {
        val freshSnapshot = stockRepository.getSnapshot()
        val freshStock = freshSnapshot.stock
        val freshCategories = freshSnapshot.categories
        val categoryIds = buildList {
            add(sale.category.id)
            addAll(
                engine.dispensableCategories(freshCategories, freshStock)
                    .map { it.id }
                    .filter { it != sale.category.id },
            )
        }
        for (categoryId in categoryIds) {
            for (spiral in engine.spiralsForDispense(freshStock, categoryId)) {
                when (vendingMachine.dispense(spiral)) {
                    DispenseResult.Delivered -> {
                        val product = spiral.frontProduct ?: continue
                        return DispensePlan(spiral, product)
                    }
                    is DispenseResult.Failed -> Unit
                }
            }
        }
        return null
    }

    fun returnToIdle() {
        errorReturnJob?.cancel()
        revealReturnJob?.cancel()
        activeSale = null
        paymentCompletedForActiveSale = false
        isProcessing = false
        val available = engine.availableCategories(categories, stock)
        _uiState.value = PrizeWheelUiState(
            saleState = SaleState.IDLE,
            categories = available,
            spinPriceCents = spinPriceCents,
            mysteryBoxLabel = mysteryBoxLabel,
            currentRotation = _uiState.value.currentRotation,
        )
    }

    private fun showError(message: String) {
        feedbackManager.error()
        errorReturnJob?.cancel()
        revealReturnJob?.cancel()
        activeSale = null
        paymentCompletedForActiveSale = false
        _uiState.update {
            it.copy(
                saleState = SaleState.ERROR,
                message = message,
                isSpinning = false,
                winningCategory = null,
                revealedPrize = null,
            )
        }
        errorReturnJob = viewModelScope.launch {
            delay(ERROR_AUTO_RETURN_MS)
            if (_uiState.value.saleState == SaleState.ERROR) returnToIdle()
        }
    }

    private fun scheduleRevealAutoReturn() {
        revealReturnJob?.cancel()
        revealReturnJob = viewModelScope.launch {
            delay(REVEAL_AUTO_RETURN_MS)
            if (_uiState.value.saleState == SaleState.REVEALING_PRIZE) returnToIdle()
        }
    }

    override fun onCleared() {
        val sale = activeSale
        val state = _uiState.value.saleState
        val paid = paymentCompletedForActiveSale
        purchaseJob?.cancel()
        if (sale != null) {
            runBlocking {
                abandonActiveSale(sale, state, paid)
            }
        }
        super.onCleared()
    }

    private suspend fun abandonActiveSale(sale: SaleSession, state: SaleState, paid: Boolean) {
        val refundLane = engine.pickSpiralForCategory(stock, sale.category.id)?.vmcLane ?: 1
        when (state) {
            SaleState.PAYMENT_PENDING -> paymentTerminal.cancel()
            SaleState.SPINNING, SaleState.DISPENSING -> if (paid) {
                val refunded = paymentTerminal.refundPayment(spinPriceCents, refundLane)
                stockRepository.logSale(
                    categoryLabel = sale.category.label,
                    productName = null,
                    vmcLane = refundLane,
                    result = if (refunded) SaleLogResult.REFUNDED else SaleLogResult.DISPENSE_FAILED,
                    message = "Venda abandonada durante ${state.name.lowercase()}",
                )
            }
            else -> Unit
        }
        activeSale = null
        paymentCompletedForActiveSale = false
        isProcessing = false
    }

    companion object {
        const val SPIN_DURATION_MS = 6_000L
        const val ERROR_AUTO_RETURN_MS = 8_000L
        const val REVEAL_AUTO_RETURN_MS = 6_000L
    }
}
