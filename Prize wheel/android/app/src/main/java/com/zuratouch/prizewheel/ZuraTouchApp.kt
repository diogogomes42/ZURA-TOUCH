package com.zuratouch.prizewheel

import android.app.Application
import com.zuratouch.prizewheel.data.StockRepository
import com.zuratouch.prizewheel.data.local.AppDatabase
import com.zuratouch.prizewheel.feedback.FeedbackManager
import com.zuratouch.prizewheel.hardware.HardwareDependencies
import com.zuratouch.prizewheel.hardware.HardwareHeartbeatMonitor
import com.zuratouch.prizewheel.hardware.HardwareStatus
import com.zuratouch.prizewheel.hardware.bootstrapHardware
import com.zuratouch.prizewheel.payment.PaymentTerminal
import com.zuratouch.prizewheel.vending.VendingMachine
import com.zuratouch.prizewheel.vending.VmcSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface AppInitState {
    data object Loading : AppInitState
    data object Ready : AppInitState
    data class Failed(val message: String) : AppInitState
}

class ZuraTouchApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var heartbeatMonitor: HardwareHeartbeatMonitor? = null

    private val _initState = MutableStateFlow<AppInitState>(AppInitState.Loading)
    val initState: StateFlow<AppInitState> = _initState.asStateFlow()

    private val _hardwareStatus = MutableStateFlow<HardwareStatus>(HardwareStatus.Simulated)
    val hardwareStatus: StateFlow<HardwareStatus> = _hardwareStatus.asStateFlow()

    private val _hardwareRevision = MutableStateFlow(0)
    val hardwareRevision: StateFlow<Int> = _hardwareRevision.asStateFlow()

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
        startInitialization()
    }

    fun retryInitialization() {
        if (_initState.value is AppInitState.Loading) return
        _initState.value = AppInitState.Loading
        startInitialization()
    }

    suspend fun retryHardwareConnection(): HardwareStatus {
        if (BuildConfig.USE_FAKE_HARDWARE) {
            return _hardwareStatus.value
        }
        val repository = stockRepository
        vmcSession?.close()
        val bootstrap = bootstrapHardware(
            useFakeHardware = false,
            serialPath = repository.getSerialPortPath(),
        )
        withContext(Dispatchers.Main.immediate) {
            applyHardware(bootstrap.dependencies, bootstrap.status)
        }
        return bootstrap.status
    }

    private fun startInitialization() {
        applicationScope.launch {
            try {
                val database = AppDatabase.get(this@ZuraTouchApp)
                AppDatabase.seedIfEmpty(database)
                val repository = StockRepository(
                    categoryDao = database.categoryDao(),
                    spiralDao = database.spiralDao(),
                    appConfigDao = database.appConfigDao(),
                    saleLogDao = database.saleLogDao(),
                )
                repository.ensurePhysicalSpirals()
                val bootstrap = bootstrapHardware(
                    useFakeHardware = BuildConfig.USE_FAKE_HARDWARE,
                    serialPath = repository.getSerialPortPath(),
                )

                withContext(Dispatchers.Main.immediate) {
                    stockRepository = repository
                    applyHardware(bootstrap.dependencies, bootstrap.status)
                    startHeartbeatMonitor()
                    _initState.value = AppInitState.Ready
                }
            } catch (error: Exception) {
                withContext(Dispatchers.Main.immediate) {
                    _initState.value = AppInitState.Failed(
                        error.message ?: "Erro ao iniciar a aplicação.",
                    )
                }
            }
        }
    }

    private fun applyHardware(dependencies: HardwareDependencies, status: HardwareStatus) {
        vendingMachine = dependencies.vendingMachine
        paymentTerminal = dependencies.paymentTerminal
        vmcSession = dependencies.vmcSession
        _hardwareStatus.value = status
        _hardwareRevision.value += 1
    }

    private fun startHeartbeatMonitor() {
        if (BuildConfig.USE_FAKE_HARDWARE) return
        heartbeatMonitor = HardwareHeartbeatMonitor(
            scope = applicationScope,
            sessionProvider = { vmcSession?.takeIf { _hardwareStatus.value == HardwareStatus.Connected } },
            serialPathProvider = { stockRepository.getSerialPortPath() },
            onUnresponsive = { degraded ->
                if (_hardwareStatus.value == HardwareStatus.Connected) {
                    _hardwareStatus.value = degraded
                }
            },
        ).also { it.start() }
    }
}
