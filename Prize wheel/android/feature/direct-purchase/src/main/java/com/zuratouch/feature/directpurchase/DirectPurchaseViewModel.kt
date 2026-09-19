package com.zuratouch.feature.directpurchase

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zuratouch.prizewheel.data.StockDataSource
import com.zuratouch.prizewheel.domain.DirectPurchaseCatalogSections
import com.zuratouch.prizewheel.domain.DirectPurchaseEngine
import com.zuratouch.prizewheel.domain.DirectPurchaseKind
import com.zuratouch.prizewheel.domain.DirectPurchaseOffer
import com.zuratouch.prizewheel.domain.DispenseResult
import com.zuratouch.prizewheel.domain.SaleLogResult
import com.zuratouch.prizewheel.feedback.FeedbackController
import com.zuratouch.prizewheel.payment.PaymentResult
import com.zuratouch.prizewheel.payment.PaymentTerminal
import com.zuratouch.prizewheel.vending.VendingMachine
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class DirectPurchaseStep {
    CATALOG,
    CONFIRM_PHYSICAL,
    EMAIL_INPUT,
    PAYMENT_PENDING,
    SUCCESS,
    ERROR,
}

data class DirectPurchaseUiState(
    val step: DirectPurchaseStep = DirectPurchaseStep.CATALOG,
    val catalog: DirectPurchaseCatalogSections = DirectPurchaseCatalogSections(emptyList(), emptyList()),
    val selectedOffer: DirectPurchaseOffer? = null,
    val customerEmail: String = "",
    val isBusy: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)

class DirectPurchaseViewModel(
    private val stockRepository: StockDataSource,
    private val paymentTerminal: PaymentTerminal,
    private val vendingMachine: VendingMachine,
    private val feedbackManager: FeedbackController,
    private val engine: DirectPurchaseEngine = DirectPurchaseEngine(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(DirectPurchaseUiState())
    val uiState: StateFlow<DirectPurchaseUiState> = _uiState.asStateFlow()

    private var purchaseJob: Job? = null

    init {
        viewModelScope.launch {
            stockRepository.stockSnapshot.collect { snapshot ->
                val catalog = engine.buildCatalog(snapshot.categories, snapshot.stock)
                _uiState.update { current ->
                    val selected = current.selectedOffer?.let { selected ->
                        catalog.allOffers.firstOrNull { it.id == selected.id }
                    }
                    current.copy(
                        catalog = catalog,
                        selectedOffer = selected,
                    )
                }
            }
        }
    }

    fun selectOffer(offer: DirectPurchaseOffer) {
        if (_uiState.value.isBusy) return
        feedbackManager.buttonTap()
        val nextStep = when (offer.kind) {
            DirectPurchaseKind.PHYSICAL -> DirectPurchaseStep.CONFIRM_PHYSICAL
            DirectPurchaseKind.DIGITAL -> DirectPurchaseStep.EMAIL_INPUT
        }
        _uiState.update {
            it.copy(
                selectedOffer = offer,
                step = nextStep,
                errorMessage = null,
                successMessage = null,
                customerEmail = "",
            )
        }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(customerEmail = email, errorMessage = null) }
    }

    fun backToCatalog() {
        if (_uiState.value.isBusy) return
        _uiState.update {
            it.copy(
                step = DirectPurchaseStep.CATALOG,
                selectedOffer = null,
                customerEmail = "",
                errorMessage = null,
                successMessage = null,
            )
        }
    }

    fun confirmEmailAndPay() {
        val email = _uiState.value.customerEmail.trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(errorMessage = "Introduz um email válido.") }
            return
        }
        startPayment()
    }

    fun confirmPhysicalAndPay() {
        startPayment()
    }

    fun dismissSuccess() {
        backToCatalog()
    }

    fun resetFlow() {
        purchaseJob?.cancel()
        _uiState.update {
            DirectPurchaseUiState(catalog = it.catalog)
        }
    }

    fun retryFromError() {
        val offer = _uiState.value.selectedOffer ?: run {
            backToCatalog()
            return
        }
        val nextStep = when (offer.kind) {
            DirectPurchaseKind.PHYSICAL -> DirectPurchaseStep.CONFIRM_PHYSICAL
            DirectPurchaseKind.DIGITAL -> DirectPurchaseStep.EMAIL_INPUT
        }
        _uiState.update {
            it.copy(step = nextStep, errorMessage = null, isBusy = false)
        }
    }

    private fun startPayment() {
        val offer = _uiState.value.selectedOffer ?: return
        if (_uiState.value.isBusy) return
        purchaseJob?.cancel()
        purchaseJob = viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isBusy = true,
                        step = DirectPurchaseStep.PAYMENT_PENDING,
                        errorMessage = null,
                    )
                }
                val lane = paymentLaneFor(offer)
                when (val payment = paymentTerminal.requestPayment(offer.priceCents, lane)) {
                    PaymentResult.Completed -> {
                        feedbackManager.paymentConfirmed()
                        completePurchase(offer)
                    }
                    PaymentResult.Cancelled -> {
                        failPurchase("Pagamento cancelado.")
                    }
                    is PaymentResult.Failed -> {
                        failPurchase(payment.reason)
                    }
                }
            } catch (_: CancellationException) {
                _uiState.update { it.copy(isBusy = false) }
            }
        }
    }

    private suspend fun completePurchase(offer: DirectPurchaseOffer) {
        when (offer.kind) {
            DirectPurchaseKind.PHYSICAL -> completePhysicalPurchase(offer)
            DirectPurchaseKind.DIGITAL -> completeDigitalPurchase(offer)
        }
    }

    private suspend fun completePhysicalPurchase(offer: DirectPurchaseOffer) {
        val plan = offer.dispensePlan
        if (plan == null) {
            paymentTerminal.refundPayment(offer.priceCents, paymentLaneFor(offer))
            failPurchase("Prémio indisponível.")
            return
        }
        val freshSnapshot = stockRepository.getSnapshot()
        val refreshedOffer = engine.physicalOffers(freshSnapshot.categories, freshSnapshot.stock)
            .firstOrNull { it.id == offer.id }
        val refreshedPlan = refreshedOffer?.dispensePlan
        if (refreshedPlan == null) {
            paymentTerminal.refundPayment(offer.priceCents, plan.spiral.vmcLane)
            failPurchase("Prémio indisponível.")
            return
        }
        when (vendingMachine.dispense(refreshedPlan.spiral)) {
            DispenseResult.Delivered -> {
                stockRepository.registerDelivery(refreshedPlan.spiral.id)
                stockRepository.logSale(
                    categoryLabel = offer.categoryLabel,
                    productName = offer.productName,
                    vmcLane = refreshedPlan.spiral.vmcLane,
                    result = SaleLogResult.SUCCESS,
                    message = "direct purchase physical",
                )
                _uiState.update {
                    it.copy(
                        step = DirectPurchaseStep.SUCCESS,
                        isBusy = false,
                        successMessage = "O teu prémio está reservado para levantamento.",
                        errorMessage = null,
                    )
                }
            }
            else -> {
                val refunded = paymentTerminal.refundPayment(offer.priceCents, refreshedPlan.spiral.vmcLane)
                stockRepository.logSale(
                    categoryLabel = offer.categoryLabel,
                    productName = offer.productName,
                    vmcLane = refreshedPlan.spiral.vmcLane,
                    result = if (refunded) SaleLogResult.REFUNDED else SaleLogResult.DISPENSE_FAILED,
                    message = "direct purchase dispense failed",
                )
                failPurchase(
                    if (refunded) {
                        "Não foi possível entregar o prémio. O valor será reembolsado."
                    } else {
                        "Não foi possível entregar o prémio. Contacte o operador."
                    },
                )
            }
        }
    }

    private suspend fun completeDigitalPurchase(offer: DirectPurchaseOffer) {
        val email = _uiState.value.customerEmail.trim()
        stockRepository.registerDigitalPurchase(
            productName = offer.productName,
            categoryLabel = offer.categoryLabel,
            priceCents = offer.priceCents,
            customerEmail = email,
        )
        stockRepository.logSale(
            categoryLabel = offer.categoryLabel,
            productName = offer.productName,
            vmcLane = 0,
            result = SaleLogResult.SUCCESS,
            message = "direct purchase digital email=$email",
        )
        _uiState.update {
            it.copy(
                step = DirectPurchaseStep.SUCCESS,
                isBusy = false,
                successMessage = "O teu prémio será enviado para o email indicado.",
                errorMessage = null,
            )
        }
    }

    private fun failPurchase(message: String) {
        feedbackManager.error()
        _uiState.update {
            it.copy(
                step = DirectPurchaseStep.ERROR,
                isBusy = false,
                errorMessage = message,
            )
        }
    }

    private fun paymentLaneFor(offer: DirectPurchaseOffer): Int =
        offer.dispensePlan?.spiral?.vmcLane ?: 1
}
