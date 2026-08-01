package com.zuratouch.prizewheel.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zuratouch.prizewheel.data.AppConfig
import com.zuratouch.prizewheel.data.StockRepository
import com.zuratouch.prizewheel.domain.SaleLogEntry
import com.zuratouch.prizewheel.domain.ProductSlot
import com.zuratouch.prizewheel.vending.VmcProtocol
import com.zuratouch.prizewheel.vending.VmcResponse
import com.zuratouch.prizewheel.vending.VmcSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OperatorUiState(
    val authenticated: Boolean = false,
    val pinInput: String = "",
    val pinError: String? = null,
    val config: AppConfig? = null,
    val stock: List<ProductSlot> = emptyList(),
    val sales: List<SaleLogEntry> = emptyList(),
    val diagnosticsMessage: String? = null,
    val isRunningDiagnostic: Boolean = false,
)

class OperatorViewModel(
    private val stockRepository: StockRepository,
    private val vmcSession: VmcSession?,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OperatorUiState())
    val uiState: StateFlow<OperatorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            stockRepository.stockSnapshot.collect { snapshot ->
                _uiState.update {
                    it.copy(
                        config = snapshot.config,
                        stock = snapshot.stock,
                    )
                }
            }
        }
        viewModelScope.launch {
            stockRepository.recentSales.collect { sales ->
                _uiState.update { it.copy(sales = sales) }
            }
        }
    }

    fun appendPin(digit: String) {
        if (_uiState.value.authenticated) return
        if (_uiState.value.pinInput.length >= 4) return
        _uiState.update { it.copy(pinInput = it.pinInput + digit, pinError = null) }
    }

    fun clearPin() {
        _uiState.update { it.copy(pinInput = "", pinError = null) }
    }

    fun submitPin() {
        viewModelScope.launch {
            val pin = _uiState.value.pinInput
            if (stockRepository.verifyOperatorPin(pin)) {
                _uiState.update { it.copy(authenticated = true, pinInput = "", pinError = null) }
            } else {
                _uiState.update { it.copy(pinError = "PIN incorreto", pinInput = "") }
            }
        }
    }

    fun logout() {
        _uiState.update { OperatorUiState(config = it.config, stock = it.stock, sales = it.sales) }
    }

    fun updateSlotQuantity(slotId: String, quantity: Int) {
        viewModelScope.launch { stockRepository.setSlotQuantity(slotId, quantity) }
    }

    fun saveConfig(priceCents: Long, serialPath: String, label: String, pin: String, soundEnabled: Boolean) {
        viewModelScope.launch {
            stockRepository.updateConfig(priceCents, serialPath, label, pin, soundEnabled)
        }
    }

    fun runDeviceIdTest() {
        runDiagnostic("DEVICE_ID") {
            when (val response = it.send(VmcProtocol.deviceId())) {
                is VmcResponse.DeviceId -> "Ligado: ${response.value}"
                else -> "Resposta inesperada: $response"
            }
        }
    }

    fun runReconnectTest() {
        runDiagnostic("RECONNECT") {
            it.reconnect().fold(
                onSuccess = { "Porta serial reconectada." },
                onFailure = { error -> "Falha na reconexão: ${error.message}" },
            )
        }
    }

    fun runDispenseTest(lane: Int) {
        runDiagnostic("DISPENSE_$lane") {
            when (val response = it.send(VmcProtocol.dispense(lane))) {
                is VmcResponse.DispenseAccepted -> "Entrega aceite no corredor $lane."
                is VmcResponse.DeliveryFailed -> "Entrega falhou (código ${response.code})."
                else -> "Resposta: $response"
            }
        }
    }

    private fun runDiagnostic(label: String, block: suspend (VmcSession) -> String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRunningDiagnostic = true, diagnosticsMessage = "A testar $label…") }
            val message = if (vmcSession == null) {
                "Modo simulação — hardware fake activo."
            } else {
                runCatching { block(vmcSession) }.getOrElse { "Erro: ${it.message}" }
            }
            _uiState.update { it.copy(isRunningDiagnostic = false, diagnosticsMessage = message) }
        }
    }
}
