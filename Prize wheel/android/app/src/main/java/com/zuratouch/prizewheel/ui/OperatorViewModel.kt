package com.zuratouch.prizewheel.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zuratouch.prizewheel.data.AppConfig
import com.zuratouch.prizewheel.data.OperatorPinGuard
import com.zuratouch.prizewheel.data.OperatorPinHasher
import com.zuratouch.prizewheel.data.StockRepository
import com.zuratouch.prizewheel.domain.PrizeCategory
import com.zuratouch.prizewheel.domain.PrizeWheelEngine
import com.zuratouch.prizewheel.domain.QueuedProduct
import com.zuratouch.prizewheel.domain.SaleLogEntry
import com.zuratouch.prizewheel.domain.Spiral
import com.zuratouch.prizewheel.hardware.HardwareStatus
import com.zuratouch.prizewheel.vending.VmcProtocol
import com.zuratouch.prizewheel.vending.VmcResponse
import com.zuratouch.prizewheel.vending.VmcSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PinSetupPhase { Enter, Confirm }

data class OperatorUiState(
    val authenticated: Boolean = false,
    val needsPinSetup: Boolean = false,
    val pinSetupPhase: PinSetupPhase = PinSetupPhase.Enter,
    val pinInput: String = "",
    val pinError: String? = null,
    val config: AppConfig? = null,
    val categories: List<PrizeCategory> = emptyList(),
    val stock: List<Spiral> = emptyList(),
    val sales: List<SaleLogEntry> = emptyList(),
    val catalogMessage: String? = null,
    val diagnosticsMessage: String? = null,
    val isRunningDiagnostic: Boolean = false,
    val hardwareStatus: HardwareStatus = HardwareStatus.Simulated,
)

class OperatorViewModel(
    private val stockRepository: StockRepository,
    private val pinGuard: OperatorPinGuard,
    private val vmcSessionProvider: () -> VmcSession?,
    hardwareStatus: StateFlow<HardwareStatus>,
    private val retryHardwareConnection: suspend () -> HardwareStatus,
    private val engine: PrizeWheelEngine = PrizeWheelEngine(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(OperatorUiState())
    val uiState: StateFlow<OperatorUiState> = _uiState.asStateFlow()
    private var pendingSetupPin: String? = null

    init {
        viewModelScope.launch {
            val configured = stockRepository.isOperatorPinConfigured()
            _uiState.update { it.copy(needsPinSetup = !configured) }
        }
        viewModelScope.launch {
            stockRepository.stockSnapshot.collect { snapshot ->
                _uiState.update {
                    it.copy(
                        config = snapshot.config,
                        categories = snapshot.categories,
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
        viewModelScope.launch {
            hardwareStatus.collect { status ->
                _uiState.update { it.copy(hardwareStatus = status) }
            }
        }
    }

    fun appendPin(digit: String) {
        if (_uiState.value.authenticated) return
        if (_uiState.value.pinInput.length >= OperatorPinHasher.MAX_LENGTH) return
        _uiState.update { it.copy(pinInput = it.pinInput + digit, pinError = null) }
    }

    fun clearPin() {
        _uiState.update { it.copy(pinInput = "", pinError = null) }
    }

    fun submitPin() {
        viewModelScope.launch {
            if (_uiState.value.needsPinSetup) {
                handlePinSetupSubmit(_uiState.value.pinInput)
                return@launch
            }
            if (pinGuard.isLockedOut()) {
                val seconds = pinGuard.lockoutRemainingSeconds()
                _uiState.update {
                    it.copy(
                        pinError = "Demasiadas tentativas. Bloqueado ${seconds}s.",
                        pinInput = "",
                    )
                }
                return@launch
            }
            val pin = _uiState.value.pinInput
            if (stockRepository.verifyOperatorPin(pin)) {
                pinGuard.recordSuccess()
                _uiState.update { it.copy(authenticated = true, pinInput = "", pinError = null) }
            } else {
                val lockedOut = pinGuard.recordFailure()
                if (lockedOut) {
                    _uiState.update {
                        it.copy(
                            pinError = "Demasiadas tentativas. Bloqueado 5 min.",
                            pinInput = "",
                        )
                    }
                } else {
                    val remaining = pinGuard.remainingAttempts()
                    _uiState.update {
                        it.copy(
                            pinError = "PIN incorreto ($remaining restantes)",
                            pinInput = "",
                        )
                    }
                }
            }
        }
    }

    private suspend fun handlePinSetupSubmit(input: String) {
        if (!OperatorPinHasher.isValidPin(input)) {
            _uiState.update {
                it.copy(
                    pinError = "PIN deve ter pelo menos ${OperatorPinHasher.MIN_LENGTH} dígitos",
                    pinInput = "",
                )
            }
            return
        }
        when (_uiState.value.pinSetupPhase) {
            PinSetupPhase.Enter -> {
                pendingSetupPin = input
                _uiState.update {
                    it.copy(
                        pinSetupPhase = PinSetupPhase.Confirm,
                        pinInput = "",
                        pinError = null,
                    )
                }
            }
            PinSetupPhase.Confirm -> {
                if (input == pendingSetupPin) {
                    stockRepository.setOperatorPin(input)
                    pendingSetupPin = null
                    _uiState.update {
                        it.copy(
                            needsPinSetup = false,
                            authenticated = true,
                            pinSetupPhase = PinSetupPhase.Enter,
                            pinInput = "",
                            pinError = null,
                        )
                    }
                } else {
                    pendingSetupPin = null
                    _uiState.update {
                        it.copy(
                            pinSetupPhase = PinSetupPhase.Enter,
                            pinInput = "",
                            pinError = "PINs não coincidem. Tente novamente.",
                        )
                    }
                }
            }
        }
    }

    fun logout() {
        pendingSetupPin = null
        _uiState.update {
            OperatorUiState(
                needsPinSetup = it.needsPinSetup,
                config = it.config,
                categories = it.categories,
                stock = it.stock,
                sales = it.sales,
                hardwareStatus = it.hardwareStatus,
            )
        }
    }

    fun clearCatalogMessage() {
        _uiState.update { it.copy(catalogMessage = null) }
    }

    fun saveCategory(
        id: String,
        label: String,
        icon: String,
        color: Long,
        weight: Int,
        isNew: Boolean,
    ) {
        viewModelScope.launch {
            runCatching {
                val normalizedId = normalizeCategoryId(id)
                require(normalizedId.isNotBlank()) { "ID inválido" }
                if (isNew && _uiState.value.categories.any { it.id == normalizedId }) {
                    error("Já existe uma categoria com este ID")
                }
                stockRepository.upsertCategory(
                    PrizeCategory(
                        id = normalizedId,
                        label = label.trim(),
                        color = color,
                        weight = weight.coerceAtLeast(1),
                        icon = icon.ifBlank { "🎁" },
                    ),
                )
            }.onSuccess {
                _uiState.update { it.copy(catalogMessage = "Categoria guardada") }
            }.onFailure { error ->
                _uiState.update { it.copy(catalogMessage = error.message ?: "Erro ao guardar categoria") }
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            val error = stockRepository.deleteCategory(categoryId)
            _uiState.update {
                it.copy(catalogMessage = error ?: "Categoria removida")
            }
        }
    }

    fun loadTestMachineLayout() {
        viewModelScope.launch {
            stockRepository.loadTestMachineLayout()
            _uiState.update { it.copy(catalogMessage = "Layout de teste 8×3 carregado") }
        }
    }

    fun saveSpiralQueue(
        spiralId: String,
        queue: List<QueuedProduct>,
        maxCapacity: Int,
    ) {
        viewModelScope.launch {
            runCatching {
                val spiral = _uiState.value.stock.firstOrNull { it.id == spiralId }
                    ?: error("Espiral desconhecida")
                stockRepository.refillSpiral(
                    spiral.copy(
                        queue = queue,
                        maxCapacity = maxCapacity.coerceAtLeast(1),
                    ),
                )
            }.onSuccess {
                _uiState.update { it.copy(catalogMessage = "Espiral guardada") }
            }.onFailure { error ->
                _uiState.update { it.copy(catalogMessage = error.message ?: "Erro ao guardar espiral") }
            }
        }
    }

    fun popFrontFromSpiral(spiralId: String) {
        viewModelScope.launch {
            stockRepository.registerDelivery(spiralId)
        }
    }

    fun clearSpiral(spiralId: String) {
        viewModelScope.launch {
            stockRepository.clearSpiral(spiralId)
            _uiState.update { it.copy(catalogMessage = "Espiral esvaziada") }
        }
    }

    fun productCountForCategory(categoryId: String): Int =
        engine.countProductsInCategory(_uiState.value.stock, categoryId)

    private fun normalizeCategoryId(raw: String): String =
        raw.trim().lowercase().replace(Regex("[^a-z0-9_]+"), "_").trim('_').take(24)

    fun saveConfig(priceCents: Long, serialPath: String, label: String, newPin: String?, soundEnabled: Boolean) {
        if (newPin != null && !OperatorPinHasher.isValidPin(newPin)) return
        viewModelScope.launch {
            val previousPath = _uiState.value.config?.serialPortPath
            stockRepository.updateConfig(priceCents, serialPath, label, newPin, soundEnabled)
            if (previousPath != null && previousPath != serialPath) {
                retryHardwareConnection()
            }
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
        viewModelScope.launch {
            _uiState.update { it.copy(isRunningDiagnostic = true, diagnosticsMessage = "A testar RECONNECT…") }
            val message = when (val status = _uiState.value.hardwareStatus) {
                HardwareStatus.Simulated -> "Modo simulação — hardware fake activo."
                is HardwareStatus.Degraded -> when (val newStatus = retryHardwareConnection()) {
                    HardwareStatus.Connected -> "Hardware ligado com sucesso."
                    is HardwareStatus.Degraded -> "Ainda indisponível (${newStatus.reason}). Verifique ${newStatus.serialPath}."
                    HardwareStatus.Simulated -> "Modo simulação activo."
                }
                HardwareStatus.Connected -> {
                    val session = vmcSessionProvider()
                    if (session == null) {
                        "Sessão UART indisponível."
                    } else {
                        session.reconnect().fold(
                            onSuccess = { "Porta serial reconectada." },
                            onFailure = { error -> "Falha na reconexão: ${error.message}" },
                        )
                    }
                }
            }
            _uiState.update { it.copy(isRunningDiagnostic = false, diagnosticsMessage = message) }
        }
    }

    fun runDispenseTest(lane: Int) {
        runDiagnostic("DISPENSE_$lane") { session ->
            when (val accepted = session.send(VmcProtocol.dispense(lane))) {
                is VmcResponse.DispenseAccepted -> Unit
                is VmcResponse.DeliveryFailed -> return@runDiagnostic "Entrega falhou (código ${accepted.code})."
                else -> return@runDiagnostic "Resposta: $accepted"
            }
            when (
                val status = session.pollStatus(lane) {
                    it is VmcResponse.DeliveryCompleted || it is VmcResponse.DeliveryFailed
                }
            ) {
                VmcResponse.DeliveryCompleted -> "Entrega concluída no corredor $lane."
                is VmcResponse.DeliveryFailed -> "Entrega falhou (código ${status.code})."
                null -> "Tempo de entrega esgotado."
                else -> "Resposta: $status"
            }
        }
    }

    private fun runDiagnostic(label: String, block: suspend (VmcSession) -> String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRunningDiagnostic = true, diagnosticsMessage = "A testar $label…") }
            val message = when (val status = _uiState.value.hardwareStatus) {
                HardwareStatus.Simulated -> "Modo simulação — hardware fake activo."
                is HardwareStatus.Degraded -> degradedHardwareMessage(status)
                HardwareStatus.Connected -> {
                    val session = vmcSessionProvider()
                    if (session == null) {
                        "Sessão UART indisponível."
                    } else {
                        runCatching { block(session) }.getOrElse { "Erro: ${it.message}" }
                    }
                }
            }
            _uiState.update { it.copy(isRunningDiagnostic = false, diagnosticsMessage = message) }
        }
    }

    private fun degradedHardwareMessage(status: HardwareStatus.Degraded): String =
        "Hardware indisponível em ${status.serialPath}: ${status.reason}. Use «Reconectar UART»."
}
