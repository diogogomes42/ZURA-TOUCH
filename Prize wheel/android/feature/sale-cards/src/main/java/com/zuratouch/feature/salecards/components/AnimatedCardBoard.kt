package com.zuratouch.feature.salecards.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.zuratouch.feature.salecards.CardProductImages
import com.zuratouch.prizewheel.domain.CardSlot
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private enum class DealPhase {
    Stack,
    Spread,
    Ready,
    Suspense,
    RevealSelected,
    Celebrate,
    Focus,
    Hold,
    Done,
}

private const val WINNER_HOLD_MS = 5_000L

@Composable
fun AnimatedCardBoard(
    slots: List<CardSlot>,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
    selectionEnabled: Boolean,
    onDealComplete: () -> Unit,
    onRevealComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var phase by remember(slots) { mutableStateOf(DealPhase.Stack) }
    var dealCompleteNotified by remember(slots) { mutableStateOf(false) }
    var revealCompleteNotified by remember(slots) { mutableStateOf(false) }
    var celebrationBurst by remember(slots) { mutableStateOf(0) }

    LaunchedEffect(slots) {
        phase = DealPhase.Stack
        dealCompleteNotified = false
        revealCompleteNotified = false
        celebrationBurst = 0
        delay(380)
        phase = DealPhase.Spread
        delay(920)
        phase = DealPhase.Ready
    }

    LaunchedEffect(phase) {
        if (phase == DealPhase.Ready && !dealCompleteNotified) {
            dealCompleteNotified = true
            onDealComplete()
        }
    }

    LaunchedEffect(selectedIndex) {
        if (selectedIndex == null || revealCompleteNotified) return@LaunchedEffect
        phase = DealPhase.Suspense
        delay(340)
        celebrationBurst++
        phase = DealPhase.RevealSelected
        delay(520)
        phase = DealPhase.Celebrate
        delay(480)
        phase = DealPhase.Focus
        delay(720)
        phase = DealPhase.Hold
        delay(WINNER_HOLD_MS)
        phase = DealPhase.Done
        if (!revealCompleteNotified) {
            revealCompleteNotified = true
            onRevealComplete()
        }
    }

    val spreadProgress by animateFloatAsState(
        targetValue = when (phase) {
            DealPhase.Stack -> 0f
            DealPhase.Spread -> 1f
            else -> 1f
        },
        animationSpec = tween(durationMillis = 920, easing = FastOutSlowInEasing),
        label = "spreadProgress",
    )

    val selectedLift by animateFloatAsState(
        targetValue = if (phase == DealPhase.Suspense) 1f else 0f,
        animationSpec = tween(320, easing = FastOutSlowInEasing),
        label = "selectedLift",
    )

    val selectedFlip by animateFloatAsState(
        targetValue = when (phase) {
            DealPhase.RevealSelected, DealPhase.Celebrate, DealPhase.Focus, DealPhase.Hold, DealPhase.Done -> 1f
            else -> 0f
        },
        animationSpec = tween(520, easing = FastOutSlowInEasing),
        label = "selectedFlip",
    )

    val celebratePop by animateFloatAsState(
        targetValue = when (phase) {
            DealPhase.Celebrate, DealPhase.Focus, DealPhase.Hold, DealPhase.Done -> 1f
            else -> 0f
        },
        animationSpec = spring(dampingRatio = 0.62f, stiffness = 340f),
        label = "celebratePop",
    )

    val othersFlip by animateFloatAsState(
        targetValue = when (phase) {
            DealPhase.Focus, DealPhase.Hold, DealPhase.Done -> 1f
            else -> 0f
        },
        animationSpec = tween(780, easing = FastOutSlowInEasing),
        label = "othersFlip",
    )

    val focusProgress by animateFloatAsState(
        targetValue = when (phase) {
            DealPhase.Focus, DealPhase.Hold, DealPhase.Done -> 1f
            else -> 0f
        },
        animationSpec = tween(720, easing = FastOutSlowInEasing),
        label = "focusProgress",
    )

    val celebrating = phase >= DealPhase.RevealSelected
    val showConfetti = celebrating
    val showBanner = phase >= DealPhase.Celebrate
    val inFocus = focusProgress > 0f
    val winnerPulseTransition = rememberInfiniteTransition(label = "winnerPulse")
    val winnerPulseRaw by winnerPulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(650),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "winnerPulseValue",
    )
    val winnerPulse = if (showBanner) winnerPulseRaw else 0f

    val selectedSlot = selectedIndex?.let { idx -> slots.firstOrNull { it.index == idx } }
    val winningSlot = slots.firstOrNull { it.isTargetSlot }
    val winnerName = selectedSlot?.let { slot ->
        winningSlot?.dispensePlan?.product?.productName ?: slot.dispensePlan.product.productName
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (showBanner) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(CardGridSpec.BANNER_SLOT_HEIGHT),
                    contentAlignment = Alignment.Center,
                ) {
                    CelebrationBanner(
                        visible = true,
                        prizeName = winnerName,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                val columns = CardGridSpec.COLUMNS
                val density = LocalDensity.current
                val cardAreaWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
                val cardAreaHeightPx = constraints.maxHeight.toFloat().coerceAtLeast(1f)
                val rowCount = (slots.size + columns - 1) / columns
                val metrics = computeCardGridMetrics(cardAreaWidthPx, rowCount, density)
                val spacingPx = metrics.spacingPx
                val cardWidthPx = metrics.cardWidthPx
                val cardHeightPx = metrics.cardHeightPx
                val cardWidthDp = with(density) { cardWidthPx.toDp() }
                val cardHeightDp = with(density) { cardHeightPx.toDp() }

                ConfettiEffect(
                    active = showConfetti,
                    intensity = ConfettiIntensity.Celebration,
                    burstKey = celebrationBurst,
                    modifier = Modifier.fillMaxSize(),
                )
                SparkleBurst(
                    active = showBanner,
                    burstKey = celebrationBurst,
                    modifier = Modifier.fillMaxSize(),
                )

                val topPaddingPx = with(density) { 8.dp.roundToPx().toFloat() }
                val winnerGapPx = with(density) { 20.dp.roundToPx().toFloat() }
                val winnerMaxScale = 1.14f
                val winnerSlotPx = topPaddingPx + cardHeightPx * winnerMaxScale + winnerGapPx
                val topInsetPx = winnerSlotPx * focusProgress
                val gridHeightPx = metrics.gridHeightPx
                val totalHeightPx = gridHeightPx + topInsetPx + topPaddingPx
                val needsScroll = totalHeightPx > cardAreaHeightPx
                val startY = if (needsScroll) 0f else ((cardAreaHeightPx - totalHeightPx) / 2f).coerceAtLeast(0f)
                val centerX = cardAreaWidthPx / 2f - cardWidthPx / 2f
                val centerY = cardAreaHeightPx / 2f - cardHeightPx / 2f
                val winnerTargetY = startY + topPaddingPx
                val scrollState = rememberScrollState()

                LaunchedEffect(inFocus, needsScroll) {
                    if (inFocus && needsScroll) {
                        scrollState.scrollTo(0)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(if (needsScroll) Modifier.verticalScroll(scrollState) else Modifier),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = with(density) { totalHeightPx.toDp() }),
                    ) {
                val sortedSlots = remember(slots, selectedIndex, focusProgress) {
                    slots.sortedBy { slot ->
                        if (focusProgress > 0f && slot.index == selectedIndex) 1f else 0f
                    }
                }

                sortedSlots.forEach { slot ->
                    val col = slot.index % columns
                    val row = slot.index / columns
                    val gridX = col * (cardWidthPx + spacingPx).toFloat()
                    val gridY = startY + topInsetPx + row * (cardHeightPx + spacingPx)
                    val stackOffsetX = (slot.index % 5) * 1.2f
                    val stackOffsetY = -(slot.index % 8) * 1.8f

                    val isSelected = slot.index == selectedIndex
                    val inReveal = phase >= DealPhase.RevealSelected

                    val spreadX = lerp(centerX + stackOffsetX, gridX, spreadProgress)
                    val spreadY = lerp(centerY + stackOffsetY, gridY, spreadProgress)

                    val layout = focusCardLayout(
                        spreadX = spreadX,
                        spreadY = spreadY,
                        gridX = gridX,
                        gridY = gridY,
                        centerX = centerX,
                        winnerTargetY = winnerTargetY,
                        focusProgress = focusProgress,
                        celebratePop = celebratePop,
                        winnerPulse = winnerPulse,
                        isSelected = isSelected,
                    )

                    val liftPx = if (isSelected) selectedLift * with(density) { 14.dp.toPx() } else 0f
                    val flipProgress = if (isSelected) selectedFlip else othersFlip
                    val rotationY = lerp(180f, 360f, flipProgress)

                    val cardAlpha = if (isSelected || !inFocus) 1f else 0.92f

                    val faceVariant = when {
                        isSelected && phase >= DealPhase.Celebrate -> CardFaceVariant.Spotlight
                        isSelected && inFocus -> CardFaceVariant.Hero
                        else -> CardFaceVariant.Standard
                    }

                    ProductCardTile(
                        slot = slot,
                        selectedIndex = selectedIndex,
                        winningSlot = winningSlot,
                        rotationY = rotationY,
                        scale = layout.scale,
                        alpha = cardAlpha,
                        isWinner = isSelected && inReveal,
                        winnerGlow = isSelected && showBanner,
                        faceVariant = faceVariant,
                        onClick = { onSelect(slot.index) },
                        enabled = selectionEnabled && selectedIndex == null && phase == DealPhase.Ready,
                        modifier = Modifier
                            .zIndex(layout.zIndex)
                            .offset {
                                IntOffset(
                                    layout.x.roundToInt(),
                                    (layout.y - liftPx).roundToInt(),
                                )
                            }
                            .size(width = cardWidthDp, height = cardHeightDp),
                    )
                }
                    }
                }
            }
        }
    }
}

private data class FocusLayout(
    val x: Float,
    val y: Float,
    val scale: Float,
    val zIndex: Float,
)

private fun focusCardLayout(
    spreadX: Float,
    spreadY: Float,
    gridX: Float,
    gridY: Float,
    centerX: Float,
    winnerTargetY: Float,
    focusProgress: Float,
    celebratePop: Float,
    winnerPulse: Float,
    isSelected: Boolean,
): FocusLayout {
    if (focusProgress <= 0f && celebratePop <= 0f) {
        return FocusLayout(spreadX, spreadY, 1f, 0f)
    }

    if (isSelected) {
        val popScale = 1f + celebratePop * 0.05f + winnerPulse * 0.02f
        val focusScale = if (focusProgress > 0f) lerp(1f, 1.06f, focusProgress) else 1f + celebratePop * 0.03f
        return FocusLayout(
            x = if (focusProgress > 0f) lerp(spreadX, centerX, focusProgress) else spreadX,
            y = if (focusProgress > 0f) lerp(spreadY, winnerTargetY, focusProgress) else spreadY,
            scale = focusScale * popScale,
            zIndex = 100f,
        )
    }

    return FocusLayout(
        x = lerp(spreadX, gridX, focusProgress.coerceAtLeast(0.01f)),
        y = lerp(spreadY, gridY, focusProgress.coerceAtLeast(0.01f)),
        scale = 1f,
        zIndex = 4f,
    )
}

@Composable
private fun ProductCardTile(
    slot: CardSlot,
    selectedIndex: Int?,
    winningSlot: CardSlot?,
    rotationY: Float,
    scale: Float,
    alpha: Float,
    isWinner: Boolean,
    winnerGlow: Boolean = false,
    faceVariant: CardFaceVariant,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val showFront = rotationY % 360f <= 90f || rotationY % 360f >= 270f
    val winningProduct = winningSlot?.dispensePlan?.product
    val winningCategory = winningSlot?.category
    val faceProduct = if (winningProduct != null && winningCategory != null) {
        slot.faceProduct(selectedIndex, winningProduct)
    } else {
        slot.displayProduct
    }
    val faceCategory = if (winningProduct != null && winningCategory != null) {
        slot.faceCategory(selectedIndex, winningCategory)
    } else {
        slot.category
    }

    Box(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
            transformOrigin = TransformOrigin(0.5f, 0.5f)
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = when {
                        winnerGlow -> 22.dp
                        isWinner -> 14.dp
                        else -> 3.dp
                    },
                    shape = RoundedCornerShape(12.dp),
                    spotColor = if (isWinner || winnerGlow) Color(0xFFFFD700) else Color.Black,
                )
                .graphicsLayer {
                    this.rotationY = rotationY
                    cameraDistance = 18f * density
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                }
                .clip(RoundedCornerShape(12.dp))
                .then(
                    when {
                        winnerGlow && showFront -> Modifier.border(3.dp, Color(0xFFFFD700), RoundedCornerShape(12.dp))
                        isWinner && showFront -> Modifier.border(2.5.dp, Color(0xFFFFD700), RoundedCornerShape(12.dp))
                        else -> Modifier
                    },
                )
                .clickable(enabled = enabled) { onClick() },
        ) {
            if (showFront) {
                CardProductFace(
                    productName = faceProduct.productName,
                    imageRes = CardProductImages.drawableFor(faceProduct.productName),
                    categoryColor = faceCategory.color,
                    variant = faceVariant,
                    modifier = Modifier.fillMaxSize(),
                    cornerRadius = 12.dp,
                    imagePadding = when (faceVariant) {
                        CardFaceVariant.Hero -> 10.dp
                        CardFaceVariant.Spotlight -> 8.dp
                        else -> 6.dp
                    },
                )
            } else {
                CardBackFace(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

private fun lerp(start: Float, end: Float, fraction: Float): Float =
    start + (end - start) * fraction.coerceIn(0f, 1f)
