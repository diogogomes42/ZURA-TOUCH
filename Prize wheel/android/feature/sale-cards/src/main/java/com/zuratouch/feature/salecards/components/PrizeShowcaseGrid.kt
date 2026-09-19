package com.zuratouch.feature.salecards.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.feature.salecards.AvailablePrizeDisplay
import com.zuratouch.feature.salecards.AvailablePrizes
import com.zuratouch.feature.salecards.theme.ZuraColors

/** Até 28 prémios espalhados aleatoriamente — com movimento idle suave. */
@Composable
fun PrizeShowcaseGrid(
    prizes: List<AvailablePrizeDisplay>,
    modifier: Modifier = Modifier,
    onPrizeClick: ((AvailablePrizeDisplay) -> Unit)? = null,
    prizesClickable: Boolean = false,
    idleAnimationEnabled: Boolean = true,
    animationPaused: Boolean = false,
) {
    val visible = prizes.take(AvailablePrizes.MAX_SHOWCASE)
    val layoutSeed = remember(visible) {
        visible.fold(17L) { acc, prize -> acc * 31L + prize.productName.hashCode() }
    }

    val motionIntensity by animateFloatAsState(
        targetValue = if (idleAnimationEnabled && !animationPaused) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "showcaseMotionIntensity",
    )

    val infiniteTransition = rememberInfiniteTransition(label = "showcaseIdle")
    val idlePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = ShowcaseIdleMotion.TWO_PI,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = ShowcaseIdleMotion.CYCLE_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "showcaseIdlePhase",
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val areaWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)
        val areaHeightPx = constraints.maxHeight.toFloat().coerceAtLeast(1f)
        val gapPx = with(density) { CardGridSpec.SHOWCASE_SPACING.roundToPx().toFloat() }
        val driftXPx = with(density) { 5.dp.toPx() }
        val driftYPx = with(density) { 3.5.dp.toPx() }
        val cameraDistancePx = with(density) { 14.dp.toPx() }

        val placements = remember(areaWidthPx, areaHeightPx, visible, layoutSeed) {
            ScatteredCardLayout.compute(
                count = visible.size,
                areaWidthPx = areaWidthPx,
                areaHeightPx = areaHeightPx,
                gapPx = gapPx,
                seed = layoutSeed,
            )
        }

        visible.forEachIndexed { index, prize ->
            val placement = placements.getOrNull(index) ?: return@forEachIndexed
            val cardW = with(density) { placement.width.toDp() }
            val cardH = with(density) { placement.height.toDp() }
            val offsetX = with(density) { placement.x.toDp() }
            val offsetY = with(density) { placement.y.toDp() }
            val cornerRadius = (placement.width / density.density * 0.11f).coerceIn(5f, 11f).dp
            val imagePadding = (placement.width / density.density * 0.07f).coerceIn(3f, 7f).dp
            val motion = ShowcaseIdleMotion.layerForCard(
                index = index,
                phase = idlePhase,
                intensity = motionIntensity,
                baseRotationZ = placement.rotationDegrees,
                driftXPx = driftXPx,
                driftYPx = driftYPx,
            )

            val cardModifier = Modifier
                .offset(x = offsetX, y = offsetY)
                .size(cardW, cardH)
                .graphicsLayer {
                    translationX = motion.translationXPx
                    translationY = motion.translationYPx
                    rotationZ = motion.rotationZ
                    rotationY = motion.rotationY
                    cameraDistance = cameraDistancePx
                }

            CardProductFace(
                productName = prize.productName,
                imageRes = prize.imageRes,
                categoryColor = prize.categoryColor,
                modifier = if (onPrizeClick != null) {
                    cardModifier.clickable(
                        enabled = prizesClickable,
                        interactionSource = remember(prize.productName) { MutableInteractionSource() },
                        indication = null,
                        onClick = { onPrizeClick(prize) },
                    )
                } else {
                    cardModifier
                },
                cornerRadius = cornerRadius,
                imagePadding = imagePadding,
            )
        }
    }
}

/** Generic card back — not a product photo. */
@Composable
fun CardBackFace(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ZuraColors.CardBackTop, ZuraColors.CardBackBottom),
                ),
            )
            .border(1.5.dp, ZuraColors.Accent.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        Text(
            text = "?",
            color = ZuraColors.Accent.copy(alpha = 0.85f),
            fontSize = 22.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
        )
    }
}
