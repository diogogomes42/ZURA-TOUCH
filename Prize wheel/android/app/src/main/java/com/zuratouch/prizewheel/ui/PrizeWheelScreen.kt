package com.zuratouch.prizewheel.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.prizewheel.R
import com.zuratouch.prizewheel.domain.SaleState
import com.zuratouch.prizewheel.hardware.HardwareStatus
import com.zuratouch.prizewheel.ui.components.HardwareDegradedBanner
import com.zuratouch.prizewheel.ui.components.PrizeRevealOverlay
import com.zuratouch.prizewheel.ui.components.PrizeWheelCanvas
import com.zuratouch.prizewheel.ui.theme.ZuraColors
import java.util.Locale

@Composable
fun PrizeWheelScreen(
    viewModel: PrizeWheelViewModel,
    hardwareStatus: HardwareStatus = HardwareStatus.Simulated,
    onOpenOperator: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    Box(
        modifier = Modifier
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
            BrandHeader(
                onLongPress = onOpenOperator,
                operatorAccessEnabled = state.canOpenOperator,
            )
            Spacer(modifier = Modifier.height(24.dp))
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
                Spacer(modifier = Modifier.height(20.dp))
                when (state.saleState) {
                    SaleState.IDLE -> SpinButton(
                        priceCents = state.spinPriceCents,
                        enabled = state.categories.isNotEmpty(),
                        onClick = viewModel::purchaseSpin,
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
    }
}

@Composable
private fun BrandHeader(
    onLongPress: () -> Unit,
    operatorAccessEnabled: Boolean = true,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.pointerInput(operatorAccessEnabled, onLongPress) {
            if (operatorAccessEnabled) {
                detectTapGestures(onLongPress = { onLongPress() })
            }
        },
    ) {
        Image(
            painter = painterResource(R.drawable.zura_touch_logo),
            contentDescription = stringResource(R.string.brand_title),
            modifier = Modifier
                .widthIn(max = 200.dp)
                .heightIn(min = 100.dp, max = 140.dp),
            contentScale = ContentScale.Fit,
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
    SaleState.DISPENSING -> state.message ?: stringResource(R.string.dispensing)
    SaleState.ERROR -> state.message ?: stringResource(R.string.error_generic)
    SaleState.REVEALING_PRIZE -> ""
}

@Composable
private fun SpinButton(priceCents: Long, enabled: Boolean, onClick: () -> Unit) {
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
        Text(stringResource(R.string.spin_cta), fontWeight = FontWeight.Black, letterSpacing = 0.6.sp)
        Text(
            text = formatPrice(priceCents),
            modifier = Modifier.padding(start = 12.dp),
            color = ZuraColors.BackgroundDark.copy(alpha = 0.7f),
            fontWeight = FontWeight.Black,
        )
    }
}

private fun formatPrice(cents: Long): String = String.format(Locale("pt", "PT"), "€%.2f", cents / 100.0)
