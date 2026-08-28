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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.zuratouch.feature.salecards.components.HardwareDegradedBanner
import com.zuratouch.feature.salecards.components.PrizeShowcaseGrid
import com.zuratouch.feature.salecards.theme.ZuraColors
import com.zuratouch.prizewheel.domain.SaleState
import com.zuratouch.prizewheel.hardware.HardwareStatus
import java.util.Locale

@Composable
fun CardGameScreen(
    viewModel: CardGameViewModel,
    hardwareStatus: HardwareStatus = HardwareStatus.Simulated,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val boardSeed = state.board?.boardSeed
    var dealComplete by remember { mutableStateOf(false) }
    LaunchedEffect(boardSeed, state.saleState) {
        if (state.saleState == SaleState.PLAYING && boardSeed != null) {
            dealComplete = false
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
            BrandHeader()
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (state.saleState == SaleState.PLAYING || state.saleState == SaleState.DISPENSING) {
                    StatusSection(state = state, dealComplete = dealComplete)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                val showBoard = state.board != null &&
                    (state.saleState == SaleState.PLAYING || state.saleState == SaleState.DISPENSING)
                when {
                    showBoard -> {
                        state.board?.let { board ->
                            AnimatedCardBoard(
                                slots = board.slots,
                                selectedIndex = state.selectedCardIndex,
                                onSelect = viewModel::selectCard,
                                selectionEnabled = dealComplete && state.saleState == SaleState.PLAYING,
                                onDealComplete = { dealComplete = true },
                                onRevealComplete = viewModel::onRevealAnimationComplete,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                            )
                        }
                    }
                    state.saleState == SaleState.IDLE && state.availablePrizes.isNotEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            PrizeShowcaseGrid(
                                prizes = state.availablePrizes,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }
                if (state.saleState != SaleState.PLAYING &&
                    state.saleState != SaleState.DISPENSING &&
                    state.saleState != SaleState.PAYMENT_PENDING
                ) {
                    Spacer(modifier = Modifier.height(28.dp))
                    StatusSection(state = state, dealComplete = dealComplete)
                }
                Spacer(modifier = Modifier.height(20.dp))
                val showFinish = state.selectedCardIndex != null &&
                    (state.saleState == SaleState.PLAYING || state.saleState == SaleState.DISPENSING)
                when (state.saleState) {
                    SaleState.IDLE -> PlayButton(
                        priceCents = state.playPriceCents,
                        enabled = state.canPlay && !state.isBusy,
                        onClick = viewModel::startRound,
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
            .widthIn(max = 200.dp)
            .heightIn(min = 100.dp, max = 140.dp),
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
        else -> stringResource(R.string.cards_idle_hint)
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
private fun PlayButton(priceCents: Long, enabled: Boolean, onClick: () -> Unit) {
    val pulse = rememberInfiniteTransition(label = "ctaPulse")
    val scale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = if (enabled) 1.03f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ctaScale",
    )
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(8.dp, RoundedCornerShape(13.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = ZuraColors.Accent,
            contentColor = ZuraColors.BackgroundDark,
            disabledContainerColor = ZuraColors.Accent.copy(alpha = 0.5f),
        ),
        shape = RoundedCornerShape(13.dp),
    ) {
        Text(stringResource(R.string.cards_play_cta), fontWeight = FontWeight.Black, letterSpacing = 0.6.sp)
        Text(
            text = formatPrice(priceCents),
            modifier = Modifier.padding(start = 12.dp),
            color = ZuraColors.BackgroundDark.copy(alpha = 0.7f),
            fontWeight = FontWeight.Black,
        )
    }
}

private fun formatPrice(cents: Long): String = String.format(Locale("pt", "PT"), "€%.2f", cents / 100.0)
