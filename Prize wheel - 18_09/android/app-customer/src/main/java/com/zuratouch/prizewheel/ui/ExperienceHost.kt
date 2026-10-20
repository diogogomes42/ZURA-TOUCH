package com.zuratouch.prizewheel.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
                    modifier = modifier,
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
                    modifier = modifier,
                )
            }
        }
    }
}
