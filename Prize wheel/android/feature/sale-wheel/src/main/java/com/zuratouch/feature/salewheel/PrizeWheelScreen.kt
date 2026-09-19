package com.zuratouch.feature.salewheel

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.feature.salewheel.R
import com.zuratouch.prizewheel.domain.SaleState
import com.zuratouch.prizewheel.hardware.HardwareStatus
import com.zuratouch.feature.salewheel.components.HardwareDegradedBanner
import com.zuratouch.feature.salewheel.components.IdleCallToActionBanner
import com.zuratouch.feature.salewheel.components.LegendaryJackpotBar
import com.zuratouch.feature.salewheel.components.PlayCtaWithTierPicker
import com.zuratouch.feature.salewheel.components.PrizeRevealOverlay
import com.zuratouch.feature.salewheel.components.PrizeTierPickerDialog
import com.zuratouch.feature.salewheel.components.PrizeWheelCanvas
import com.zuratouch.feature.salewheel.theme.ZuraColors

@Composable
fun PrizeWheelScreen(
    viewModel: PrizeWheelViewModel,
    hardwareStatus: HardwareStatus = HardwareStatus.Simulated,
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    inactivityResetSignal: Int = 0,
) {
    val state by viewModel.uiState.collectAsState()
    var showTierPicker by remember { mutableStateOf(false) }
    LaunchedEffect(inactivityResetSignal) {
        if (inactivityResetSignal > 0) {
            showTierPicker = false
            viewModel.returnToIdle()
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
            if (topBar != null) {
                topBar()
            } else {
                BrandHeader(mysteryBoxLabel = state.mysteryBoxLabel)
            }
            if (state.saleState == SaleState.IDLE && state.categories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                IdleCallToActionBanner(
                    title = stringResource(R.string.wheel_idle_cta_title),
                    subtitle = stringResource(R.string.wheel_idle_cta_subtitle),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PrizeWheelCanvas(
                    categories = state.categories,
                    saleState = state.saleState,
                    spinFromRotation = state.spinFromRotation,
                    targetRotation = state.targetRotation,
                    spinSessionId = state.spinSessionId,
                    onSpinComplete = viewModel::onSpinAnimationComplete,
                    onSpinTick = viewModel::onSpinTick,
                )
                Spacer(modifier = Modifier.height(28.dp))
                StatusSection(state = state)
                if (state.saleState == SaleState.IDLE && state.jackpotDisplay.visible) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LegendaryJackpotBar(
                        state = state.jackpotDisplay,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                when (state.saleState) {
                    SaleState.IDLE -> PlayCtaWithTierPicker(
                        actionLabel = stringResource(R.string.spin_cta),
                        selectedTierId = state.selectedTierId,
                        priceCents = state.spinPriceCents,
                        enabled = state.categories.isNotEmpty(),
                        onPlay = viewModel::purchaseSpin,
                        onPriceClick = { showTierPicker = true },
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
                    else -> Unit
                }
            }
        }

        if (state.saleState == SaleState.REVEALING_PRIZE) {
            PrizeRevealOverlay(
                prizeName = state.revealedPrize.orEmpty(),
                visible = true,
                onFinish = viewModel::returnToIdle,
            )
        }

        if (showTierPicker && state.saleState == SaleState.IDLE) {
            PrizeTierPickerDialog(
                selectedTierId = state.selectedTierId,
                onTierSelected = viewModel::selectTier,
                onDismiss = { showTierPicker = false },
            )
        }
    }
}

@Composable
private fun BrandHeader(mysteryBoxLabel: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.zura_touch_logo),
            contentDescription = stringResource(R.string.brand_title),
            modifier = Modifier
                .widthIn(max = 140.dp)
                .heightIn(min = 56.dp, max = 80.dp),
            contentScale = ContentScale.Fit,
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

@Composable
private fun StatusSection(state: PrizeWheelUiState) {
    val showLoader = state.saleState == SaleState.PAYMENT_PENDING || state.saleState == SaleState.DISPENSING
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        AnimatedContent(
            targetState = statusMessage(state),
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
            label = "statusMessage",
        ) { message ->
            Text(
                text = message,
                color = if (state.saleState == SaleState.ERROR) ZuraColors.Error else Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
            )
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
private fun statusMessage(state: PrizeWheelUiState): String = when (state.saleState) {
    SaleState.IDLE -> when {
        state.categories.isEmpty() -> stringResource(R.string.no_stock)
        else -> ""
    }
    SaleState.PAYMENT_PENDING -> state.message ?: stringResource(R.string.payment_pending)
    SaleState.SPINNING -> state.message ?: stringResource(R.string.spinning)
    SaleState.PLAYING -> ""
    SaleState.DISPENSING -> state.message ?: stringResource(R.string.dispensing)
    SaleState.ERROR -> state.message ?: stringResource(R.string.error_generic)
    SaleState.REVEALING_PRIZE -> ""
}
