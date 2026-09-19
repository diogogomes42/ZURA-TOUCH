package com.zuratouch.feature.salecards

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.feature.salecards.components.AnimatedCardBoard
import com.zuratouch.feature.salecards.components.CategoryTierLegend
import com.zuratouch.feature.salecards.components.HardwareDegradedBanner
import com.zuratouch.feature.salecards.components.IdleCallToActionBanner
import com.zuratouch.feature.salecards.components.LegendaryJackpotBar
import com.zuratouch.feature.salecards.components.PlayCtaWithTierPicker
import com.zuratouch.feature.salecards.components.PrizeShowcaseGrid
import com.zuratouch.feature.salecards.components.PrizeTierPickerDialog
import com.zuratouch.feature.salecards.theme.ZuraColors
import com.zuratouch.prizewheel.domain.SaleState
import com.zuratouch.prizewheel.hardware.HardwareStatus
import java.util.Locale

@Composable
fun CardGameScreen(
    viewModel: CardGameViewModel,
    hardwareStatus: HardwareStatus = HardwareStatus.Simulated,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = { BrandHeader() },
    inactivityResetSignal: Int = 0,
) {
    val state by viewModel.uiState.collectAsState()
    val boardSeed = state.board?.boardSeed
    var dealComplete by remember { mutableStateOf(false) }
    var showPlayChoiceDialog by remember { mutableStateOf(false) }
    var showTierPicker by remember { mutableStateOf(false) }
    LaunchedEffect(inactivityResetSignal) {
        if (inactivityResetSignal > 0) {
            showPlayChoiceDialog = false
            showTierPicker = false
            viewModel.returnToIdle()
        }
    }
    LaunchedEffect(boardSeed, state.saleState) {
        if (state.saleState == SaleState.PLAYING && boardSeed != null) {
            dealComplete = false
        }
    }
    LaunchedEffect(state.saleState) {
        if (state.saleState != SaleState.IDLE) {
            showPlayChoiceDialog = false
        }
    }
    LaunchedEffect(showPlayChoiceDialog) {
        if (!showPlayChoiceDialog) {
            showTierPicker = false
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(ZuraColors.BackgroundMid, ZuraColors.BackgroundDark),
                    radius = 1200f,
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 620.dp)
                .align(Alignment.Center)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (hardwareStatus is HardwareStatus.Degraded) {
                HardwareDegradedBanner(
                    status = hardwareStatus,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }
            topBar()
            if (state.saleState == SaleState.IDLE && state.canPlay) {
                Spacer(modifier = Modifier.height(12.dp))
                IdleCallToActionBanner(
                    title = stringResource(R.string.cards_idle_cta_title),
                    subtitle = stringResource(R.string.cards_idle_cta_subtitle),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (state.saleState == SaleState.IDLE && state.prizeCategories.isNotEmpty()) {
                    CategoryTierLegend(
                        categories = state.prizeCategories,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                if (state.saleState == SaleState.PLAYING || state.saleState == SaleState.DISPENSING) {
                    StatusSection(state = state, dealComplete = dealComplete)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                val showBoard = state.board != null &&
                    (state.saleState == SaleState.PLAYING || state.saleState == SaleState.DISPENSING)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    when {
                        showBoard -> {
                            state.board?.let { board ->
                                AnimatedCardBoard(
                                    slots = board.slots,
                                    categories = state.prizeCategories,
                                    selectedIndex = state.selectedCardIndex,
                                    onSelect = viewModel::selectCard,
                                    selectionEnabled = dealComplete && state.saleState == SaleState.PLAYING,
                                    onDealComplete = { dealComplete = true },
                                    onRevealComplete = viewModel::onRevealAnimationComplete,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }
                        state.saleState == SaleState.IDLE && state.availablePrizes.isNotEmpty() -> {
                            PrizeShowcaseGrid(
                                prizes = state.availablePrizes,
                                modifier = Modifier.fillMaxSize(),
                                onPrizeClick = { showPlayChoiceDialog = true },
                                prizesClickable = state.canPlay && !state.isBusy,
                                animationPaused = showPlayChoiceDialog || showTierPicker || state.isBusy,
                            )
                        }
                    }
                }
                if (state.saleState != SaleState.PLAYING &&
                    state.saleState != SaleState.DISPENSING &&
                    state.saleState != SaleState.PAYMENT_PENDING
                ) {
                    StatusSection(state = state, dealComplete = dealComplete)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            val showFinish = state.selectedCardIndex != null &&
                (state.saleState == SaleState.PLAYING || state.saleState == SaleState.DISPENSING)
            when (state.saleState) {
                SaleState.IDLE -> PlayCtaWithTierPicker(
                    actionLabel = stringResource(R.string.cards_play_cta),
                    selectedTierId = state.selectedTierId,
                    priceCents = state.playPriceCents,
                    enabled = state.canPlay && !state.isBusy,
                    onPlay = viewModel::startRound,
                    onPriceClick = { if (state.availableTierIds.size > 1) showTierPicker = true },
                    tierPickerEnabled = state.availableTierIds.size > 1,
                    )
                SaleState.ERROR -> {
                    Button(
                        onClick = viewModel::returnToIdle,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                        ),
                        shape = RoundedCornerShape(13.dp),
                    ) {
                        Text(stringResource(R.string.back_cta))
                    }
                }
                else -> if (showFinish) {
                    Button(
                        onClick = viewModel::finishRound,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ZuraColors.Accent,
                            contentColor = ZuraColors.BackgroundDark,
                        ),
                        shape = RoundedCornerShape(13.dp),
                    ) {
                        Text(
                            stringResource(R.string.finish_cta),
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.6.sp,
                        )
                    }
                }
            }
            if (state.saleState == SaleState.IDLE && state.jackpotDisplay.visible) {
                Spacer(modifier = Modifier.height(8.dp))
                LegendaryJackpotBar(
                    state = state.jackpotDisplay,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        if (showPlayChoiceDialog) {
            PlayChoiceDialog(
                selectedTierId = state.selectedTierId,
                priceCents = state.playPriceCents,
                enabled = state.canPlay && !state.isBusy,
                onPlay = {
                    showPlayChoiceDialog = false
                    viewModel.startRound()
                },
                onTrial = {
                    showPlayChoiceDialog = false
                    viewModel.startTrialRound()
                },
                onPriceClick = { if (state.availableTierIds.size > 1) showTierPicker = true },
                tierPickerEnabled = state.availableTierIds.size > 1,
                tierPickerOpen = showTierPicker,
                onDismiss = { showPlayChoiceDialog = false },
            )
        }

        if (showTierPicker && state.saleState == SaleState.IDLE) {
            PrizeTierPickerDialog(
                selectedTierId = state.selectedTierId,
                availableTierIds = state.availableTierIds,
                onTierSelected = viewModel::selectTier,
                onDismiss = { showTierPicker = false },
            )
        }
    }
}

@Composable
private fun PlayChoiceDialog(
    selectedTierId: String,
    priceCents: Long,
    enabled: Boolean,
    tierPickerEnabled: Boolean,
    tierPickerOpen: Boolean,
    onPlay: () -> Unit,
    onTrial: () -> Unit,
    onPriceClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    var secondsLeft by remember { mutableStateOf(PLAY_CHOICE_DIALOG_SECONDS) }
    val tierPickerOpenState by rememberUpdatedState(tierPickerOpen)
    val dismiss by rememberUpdatedState(onDismiss)

    LaunchedEffect(selectedTierId) {
        var remaining = PLAY_CHOICE_DIALOG_SECONDS
        secondsLeft = remaining
        while (remaining > 0) {
            while (tierPickerOpenState) {
                delay(100)
            }
            secondsLeft = remaining
            delay(1_000)
            remaining--
        }
        dismiss()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.widthIn(max = 300.dp),
            shape = RoundedCornerShape(16.dp),
            color = ZuraColors.WheelRing,
            tonalElevation = 8.dp,
            shadowElevation = 12.dp,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = stringResource(R.string.cards_choice_dialog_title),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(R.string.cards_choice_dialog_subtitle),
                    color = ZuraColors.TextMuted,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                )
                PlayCtaWithTierPicker(
                    actionLabel = stringResource(R.string.cards_play_cta),
                    selectedTierId = selectedTierId,
                    priceCents = priceCents,
                    enabled = enabled,
                    onPlay = onPlay,
                    onPriceClick = onPriceClick,
                    tierPickerEnabled = tierPickerEnabled,
                )
                TrialButton(
                    enabled = enabled,
                    onClick = onTrial,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(R.string.cards_choice_dialog_timer, secondsLeft),
                    color = ZuraColors.TextFooter,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun BrandHeader() {
    Image(
        painter = painterResource(R.drawable.zura_touch_logo),
        contentDescription = stringResource(R.string.brand_title),
        modifier = Modifier
            .widthIn(max = 140.dp)
            .heightIn(min = 56.dp, max = 80.dp),
        contentScale = ContentScale.Fit,
    )
}

@Composable
private fun StatusSection(state: CardGameUiState, dealComplete: Boolean) {
    if (state.saleState == SaleState.PAYMENT_PENDING) return
    val showLoader = state.saleState == SaleState.DISPENSING
    val message = statusMessage(state, dealComplete)
    if (message.isBlank() && !showLoader) return
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        if (message.isNotBlank()) {
            AnimatedContent(
                targetState = message,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                label = "statusMessage",
            ) { text ->
                Text(
                    text = text,
                    color = if (state.saleState == SaleState.ERROR) ZuraColors.Error else Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                )
            }
        }
        if (showLoader) {
            CircularProgressIndicator(
                color = ZuraColors.Accent,
                strokeWidth = 3.dp,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(28.dp),
            )
        }
    }
}

@Composable
private fun statusMessage(state: CardGameUiState, dealComplete: Boolean): String = when (state.saleState) {
    SaleState.IDLE -> when {
        !state.canPlay -> state.message ?: stringResource(R.string.cards_unavailable)
        else -> ""
    }
    SaleState.PAYMENT_PENDING -> ""
    SaleState.PLAYING -> when {
        state.selectedCardIndex != null -> when {
            state.message != null -> state.message!!
            else -> stringResource(R.string.cards_revealing)
        }
        dealComplete -> stringResource(R.string.cards_pick_hint)
        else -> stringResource(R.string.cards_dealing)
    }
    SaleState.DISPENSING -> state.message ?: stringResource(R.string.dispensing)
    SaleState.ERROR -> state.message ?: stringResource(R.string.error_generic)
    SaleState.REVEALING_PRIZE -> ""
    else -> ""
}

@Composable
private fun TrialButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = 40.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ZuraColors.TrialAccent,
            contentColor = ZuraColors.BackgroundDark,
            disabledContainerColor = ZuraColors.TrialAccent.copy(alpha = 0.45f),
            disabledContentColor = ZuraColors.BackgroundDark.copy(alpha = 0.6f),
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            disabledElevation = 0.dp,
        ),
    ) {
        Text(
            text = stringResource(R.string.cards_trial_cta),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            letterSpacing = 0.3.sp,
            textAlign = TextAlign.Center,
        )
    }
}

private const val PLAY_CHOICE_DIALOG_SECONDS = 5
