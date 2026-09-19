package com.zuratouch.prizewheel.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zuratouch.feature.directpurchase.DirectPurchaseEnvironment
import com.zuratouch.feature.directpurchase.DirectPurchaseScreen
import com.zuratouch.feature.directpurchase.DirectPurchaseViewModel
import com.zuratouch.feature.directpurchase.DirectPurchaseViewModelFactory
import com.zuratouch.feature.directpurchase.components.CustomerBrandHeader
import com.zuratouch.feature.salewheel.theme.ZuraColors
import com.zuratouch.feature.salecards.CardGameEnvironment
import com.zuratouch.feature.salecards.CardGameScreen
import com.zuratouch.feature.salecards.CardGameViewModel
import com.zuratouch.feature.salecards.CardGameViewModelFactory
import com.zuratouch.feature.salewheel.PrizeWheelScreen
import com.zuratouch.feature.salewheel.PrizeWheelViewModel
import com.zuratouch.feature.salewheel.PrizeWheelViewModelFactory
import com.zuratouch.feature.salewheel.WheelSaleEnvironment
import com.zuratouch.prizewheel.ZuraTouchApp
import com.zuratouch.prizewheel.domain.CustomerExperience
import com.zuratouch.prizewheel.domain.SaleState

/**
 * Routes between customer experiences. Mode changes from the Operator app apply only while idle
 * (no active payment, spin, or dispense).
 */
@Composable
fun ExperienceHost(app: ZuraTouchApp, modifier: Modifier = Modifier) {
    val hardwareRevision by app.hardwareRevision.collectAsState()
    val hardwareStatus by app.hardwareStatus.collectAsState()
    var configuredMode by remember { mutableStateOf(CustomerExperience.WHEEL) }
    var activeMode by remember { mutableStateOf(CustomerExperience.WHEEL) }
    var saleActive by remember { mutableStateOf(false) }
    var showDirectPurchase by remember { mutableStateOf(false) }
    var lastInteractionMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var inactivityResetSignal by remember { mutableIntStateOf(0) }

    val directPurchaseEnvironment = remember(app) {
        DirectPurchaseEnvironment(
            stockRepository = app.stockRepository,
            paymentTerminal = app.paymentTerminal,
            vendingMachine = app.vendingMachine,
            feedbackManager = app.feedbackManager,
        )
    }
    val directPurchaseViewModel: DirectPurchaseViewModel = viewModel(
        factory = DirectPurchaseViewModelFactory(directPurchaseEnvironment),
    )
    val directPurchaseState by directPurchaseViewModel.uiState.collectAsState()

    val registerActivity = {
        lastInteractionMs = System.currentTimeMillis()
    }

    val dismissDirectPurchase = {
        showDirectPurchase = false
        directPurchaseViewModel.resetFlow()
    }

    val inactivityMonitoringEnabled =
        !saleActive && !(showDirectPurchase && directPurchaseState.isBusy)

    CustomerInactivityTimeout(
        lastInteractionMs = lastInteractionMs,
        enabled = inactivityMonitoringEnabled,
        onTimeout = {
            inactivityResetSignal++
            if (showDirectPurchase) {
                dismissDirectPurchase()
            }
        },
    )

    LaunchedEffect(app) {
        app.stockRepository.stockSnapshot.collect { snapshot ->
            configuredMode = snapshot.config.experienceMode
        }
    }

    LaunchedEffect(configuredMode, saleActive) {
        if (!saleActive) {
            activeMode = configuredMode
        }
    }

    val openDirectPurchase = {
        if (!saleActive) {
            registerActivity()
            showDirectPurchase = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .trackCustomerActivity(onActivity = registerActivity),
    ) {
        key(hardwareRevision, activeMode) {
            when (activeMode) {
                CustomerExperience.WHEEL -> {
                    val environment = remember(app) {
                        WheelSaleEnvironment(
                            stockRepository = app.stockRepository,
                            vendingMachine = app.vendingMachine,
                            paymentTerminal = app.paymentTerminal,
                            feedbackManager = app.feedbackManager,
                        )
                    }
                    val viewModel: PrizeWheelViewModel = viewModel(
                        factory = PrizeWheelViewModelFactory(environment),
                    )
                    val state by viewModel.uiState.collectAsState()
                    LaunchedEffect(state.saleState) {
                        saleActive = state.saleState != SaleState.IDLE
                    }
                    PrizeWheelScreen(
                        viewModel = viewModel,
                        hardwareStatus = hardwareStatus,
                        inactivityResetSignal = inactivityResetSignal,
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            WheelTopBarWithDirectPurchase(
                                mysteryBoxLabel = state.mysteryBoxLabel,
                                showDirectPurchaseEntry = !saleActive,
                                onDirectPurchaseClick = openDirectPurchase,
                            )
                        },
                    )
                }
                CustomerExperience.CARDS -> {
                    val environment = remember(app) {
                        CardGameEnvironment(
                            stockRepository = app.stockRepository,
                            vendingMachine = app.vendingMachine,
                            paymentTerminal = app.paymentTerminal,
                            feedbackManager = app.feedbackManager,
                        )
                    }
                    val viewModel: CardGameViewModel = viewModel(
                        factory = CardGameViewModelFactory(environment),
                    )
                    val state by viewModel.uiState.collectAsState()
                    LaunchedEffect(state.saleState) {
                        saleActive = state.saleState != SaleState.IDLE
                    }
                    CardGameScreen(
                        viewModel = viewModel,
                        hardwareStatus = hardwareStatus,
                        inactivityResetSignal = inactivityResetSignal,
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            CustomerBrandHeader(
                                onDirectPurchaseClick = openDirectPurchase,
                                showDirectPurchaseEntry = !saleActive,
                            )
                        },
                    )
                }
            }
        }

        if (showDirectPurchase) {
            DirectPurchaseScreen(
                viewModel = directPurchaseViewModel,
                onDismiss = dismissDirectPurchase,
                onUserInteraction = registerActivity,
            )
        }
    }
}

@Composable
private fun WheelTopBarWithDirectPurchase(
    mysteryBoxLabel: String,
    showDirectPurchaseEntry: Boolean,
    onDirectPurchaseClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CustomerBrandHeader(
            onDirectPurchaseClick = onDirectPurchaseClick,
            showDirectPurchaseEntry = showDirectPurchaseEntry,
        )
        Text(
            text = mysteryBoxLabel,
            color = ZuraColors.Accent,
            fontSize = 11.sp,
            letterSpacing = 1.7.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
