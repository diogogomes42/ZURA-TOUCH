package com.zuratouch.prizewheel.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zuratouch.prizewheel.data.StockRepository
import com.zuratouch.prizewheel.domain.DispenseResult
import com.zuratouch.prizewheel.domain.PrizeCategory
import com.zuratouch.prizewheel.domain.PrizeWheelEngine
import com.zuratouch.prizewheel.domain.ProductSlot
import com.zuratouch.prizewheel.domain.SaleLogResult
import com.zuratouch.prizewheel.domain.SaleSession
import com.zuratouch.prizewheel.domain.SaleState
import com.zuratouch.prizewheel.feedback.FeedbackManager
import com.zuratouch.prizewheel.payment.PaymentResult
import com.zuratouch.prizewheel.payment.PaymentTerminal
import com.zuratouch.prizewheel.vending.VendingMachine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PrizeWheelUiState(
    val saleState: SaleState = SaleState.IDLE,
    val categories: List<PrizeCategory> = emptyList(),
    val spinPriceCents: Long = 200,
    val mysteryBoxLabel: String = "Mystery Box",
    val winningCategory: PrizeCategory? = null,
    val spinFromRotation: Float = 0f,
    val targetRotation: Float = 0f,
    val currentRotation: Float = 0f,
    val revealedPrize: String? = null,
    val message: String? = null,
    val isSpinning: Boolean = false,
    val spinSessionId: Long = 0L,
)

class PrizeWheelViewModel(
    private val engine: PrizeWheelEngine,
    private val stockRepository: StockRepository,
    private val vendingMachine: VendingMachine,
    private val paymentTerminal: PaymentTerminal,
    private val feedbackManager: FeedbackManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PrizeWheelUiState())
    val uiState: StateFlow<PrizeWheelUiState> = _uiState.asStateFlow()

    private var activeSale: SaleSession? = null
    private var categories: List<PrizeCategory> = emptyList()
    private var stock = emptyList<ProductSlot>()
    private var spinPriceCents: Long = 200
    private var mysteryBoxLabel: String = "Mystery Box"
    private var errorReturnJob: Job? = null
    private var revealReturnJob: Job? = null
    private var isProcessing = false
    private var spinSessionCounter = 0L

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
        feedbackManager.buttonTap()
        viewModelScope.launch {
            val sale = engine.createSale(categories, stock)
            if (sale == null) {
                isProcessing = false
                showError("Não há prémios disponíveis.")
                return@launch
            }
            activeSale = sale
            _uiState.update {
                it.copy(
                    saleState = SaleState.PAYMENT_PENDING,
                    message = "A confirmar pagamento…",
                    winningCategory = null,
                    revealedPrize = null,
                    isSpinning = false,
                )
            }

            when (
                val payment = paymentTerminal.requestPayment(
                    amountCents = spinPriceCents,
                    lane = sale.productSlot.vmcLane,
                )
            ) {
                PaymentResult.Completed -> {
                    feedbackManager.paymentConfirmed()
                    startSpin(sale)
                }
                PaymentResult.Cancelled -> {
                    stockRepository.logSale(
                        categoryLabel = sale.category.label,
                        productName = null,
                        vmcLane = sale.productSlot.vmcLane,
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
                        vmcLane = sale.productSlot.vmcLane,
                        result = SaleLogResult.PAYMENT_FAILED,
                        message = payment.reason,
                    )
                    isProcessing = false
                    showError(payment.reason)
                }
            }
        }
    }

    private fun startSpin(sale: SaleSession) {
        feedbackManager.spinStart()
        val available = engine.availableCategories(categories, stock)
        val startRotation = _uiState.value.currentRotation
        val target = WheelRotationCalculator.targetRotation(
            currentRotation = startRotation,
            categories = available,
            winningCategoryId = sale.category.id,
        )
        spinSessionCounter += 1
        _uiState.update {
            it.copy(
                saleState = SaleState.SPINNING,
                categories = available,
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
        val deliveredSlot = dispenseWithFallback(sale)
        if (deliveredSlot != null) {
            stockRepository.registerDelivery(deliveredSlot.id)
            stockRepository.logSale(
                categoryLabel = sale.category.label,
                productName = deliveredSlot.productName,
                vmcLane = deliveredSlot.vmcLane,
                result = SaleLogResult.SUCCESS,
            )
            feedbackManager.win()
            _uiState.update {
                it.copy(
                    saleState = SaleState.REVEALING_PRIZE,
                    revealedPrize = deliveredSlot.productName,
                    message = null,
                )
            }
            isProcessing = false
            scheduleRevealAutoReturn()
        } else {
            val refunded = paymentTerminal.refundPayment(spinPriceCents, sale.productSlot.vmcLane)
            stockRepository.logSale(
                categoryLabel = sale.category.label,
                productName = sale.productSlot.productName,
                vmcLane = sale.productSlot.vmcLane,
                result = if (refunded) SaleLogResult.REFUNDED else SaleLogResult.DISPENSE_FAILED,
                message = if (refunded) "Falha na entrega · reembolso emitido" else "Falha na entrega · reembolso falhou",
            )
            isProcessing = false
            showError(
                if (refunded) {
                    "Não foi possível entregar o prémio. O valor será reembolsado."
                } else {
                    "Não foi possível entregar o prémio. Contacte o operador."
                },
            )
        }
    }

    private suspend fun dispenseWithFallback(sale: SaleSession): ProductSlot? {
        for (slot in engine.slotsForCategory(stock, sale.category.id)) {
            when (vendingMachine.dispense(slot)) {
                DispenseResult.Delivered -> return slot
                is DispenseResult.Failed -> Unit
            }
        }
        return null
    }

    fun returnToIdle() {
        errorReturnJob?.cancel()
        revealReturnJob?.cancel()
        activeSale = null
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

    companion object {
        const val SPIN_DURATION_MS = 6_000L
        const val ERROR_AUTO_RETURN_MS = 8_000L
        const val REVEAL_AUTO_RETURN_MS = 6_000L
    }
}
