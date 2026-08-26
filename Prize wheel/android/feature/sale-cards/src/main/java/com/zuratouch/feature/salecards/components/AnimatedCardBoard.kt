package com.zuratouch.feature.salecards.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.zuratouch.feature.salecards.CardProductImages
import com.zuratouch.feature.salecards.theme.ZuraColors
import com.zuratouch.prizewheel.domain.CardSlot
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private enum class DealPhase { Stack, Flip, Spread, Ready }

@Composable
fun AnimatedCardBoard(
    slots: List<CardSlot>,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
    selectionEnabled: Boolean,
    onDealComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var phase by remember(slots) { mutableStateOf(DealPhase.Stack) }
    var dealCompleteNotified by remember(slots) { mutableStateOf(false) }

    LaunchedEffect(slots) {
        phase = DealPhase.Stack
        dealCompleteNotified = false
        delay(450)
        phase = DealPhase.Flip
        delay(650)
        phase = DealPhase.Spread
        delay(1_100)
        phase = DealPhase.Ready
    }

    LaunchedEffect(phase) {
        if (phase == DealPhase.Ready && !dealCompleteNotified) {
            dealCompleteNotified = true
            onDealComplete()
        }
    }

    val flipRotation by animateFloatAsState(
        targetValue = when (phase) {
            DealPhase.Stack -> 0f
            else -> 180f
        },
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "flipRotation",
    )

    val spreadProgress by animateFloatAsState(
        targetValue = when (phase) {
            DealPhase.Stack, DealPhase.Flip -> 0f
            else -> 1f
        },
        animationSpec = tween(durationMillis = 1_100, easing = FastOutSlowInEasing),
        label = "spreadProgress",
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val columns = 4
        val density = LocalDensity.current
        val spacingPx = with(density) { 10.dp.roundToPx() }
        val cardWidthPx = ((constraints.maxWidth - spacingPx * (columns - 1)) / columns)
            .coerceAtLeast(1)
        val cardHeightPx = (cardWidthPx / 0.72f).roundToInt()
        val rowCount = (slots.size + columns - 1) / columns
        val gridHeightPx = rowCount * cardHeightPx + (rowCount - 1).coerceAtLeast(0) * spacingPx
        val layoutProgress = if (phase == DealPhase.Ready) 1f else spreadProgress
        val needsScroll = gridHeightPx > constraints.maxHeight
        val startY = if (needsScroll) {
            0f
        } else {
            ((constraints.maxHeight - gridHeightPx) / 2f).coerceAtLeast(0f)
        }
        val centerX = constraints.maxWidth / 2f - cardWidthPx / 2f
        val centerY = constraints.maxHeight / 2f - cardHeightPx / 2f
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (needsScroll) {
                        Modifier.verticalScroll(scrollState)
                    } else {
                        Modifier
                    },
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = with(density) { gridHeightPx.toDp() }),
            ) {
                slots.forEach { slot ->
                    val col = slot.index % columns
                    val row = slot.index / columns
                    val targetX = col * (cardWidthPx + spacingPx).toFloat()
                    val targetY = startY + row * (cardHeightPx + spacingPx)
                    val x = lerp(centerX, targetX, layoutProgress)
                    val y = lerp(centerY, targetY, layoutProgress)
                    val isSelected = slot.index == selectedIndex
                    val revealRotation by animateFloatAsState(
                        targetValue = if (isSelected) 360f else flipRotation,
                        animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing),
                        label = "reveal-${slot.index}",
                    )

                    ProductCardTile(
                        slot = slot,
                        rotationY = revealRotation,
                        onClick = { onSelect(slot.index) },
                        enabled = selectionEnabled && selectedIndex == null && phase == DealPhase.Ready,
                        modifier = Modifier
                            .zIndex(if (isSelected) 10f else if (layoutProgress < 1f) slot.index.toFloat() else 0f)
                            .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
                            .width(with(density) { cardWidthPx.toDp() })
                            .height(with(density) { cardHeightPx.toDp() }),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductCardTile(
    slot: CardSlot,
    rotationY: Float,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val showFront = rotationY % 360f <= 90f || rotationY % 360f >= 270f

    Box(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(12.dp))
            .graphicsLayer {
                this.rotationY = rotationY
                cameraDistance = 8f * density
            }
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() },
    ) {
        if (showFront) {
            CardProductFace(
                productName = slot.dispensePlan.product.productName,
                imageRes = CardProductImages.drawableFor(slot.dispensePlan.product.productName),
                categoryColor = slot.category.color,
                modifier = Modifier.fillMaxSize(),
                cornerRadius = 12.dp,
                imagePadding = 8.dp,
            )
        } else {
            CardBackFace(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private fun lerp(start: Float, end: Float, fraction: Float): Float =
    start + (end - start) * fraction
