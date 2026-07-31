package com.zuratouch.prizewheel.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.prizewheel.domain.PrizeCategory
import com.zuratouch.prizewheel.domain.SaleState
import com.zuratouch.prizewheel.ui.PrizeWheelViewModel
import com.zuratouch.prizewheel.ui.WheelFrictionEasing
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.zuratouch.prizewheel.ui.theme.ZuraColors

@Composable
fun PrizeWheelCanvas(
    categories: List<PrizeCategory>,
    saleState: SaleState,
    spinFromRotation: Float,
    targetRotation: Float,
    spinSessionId: Long,
    onSpinComplete: (Float) -> Unit,
    onSpinTick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val wheelRotation = remember { Animatable(spinFromRotation) }
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val labelStyle = TextStyle(
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 0.5.sp,
        textAlign = TextAlign.Center,
    )
    val wheelSizePx = with(density) { 320.dp.roundToPx() }
    val labelMaxWidth = remember(wheelSizePx) { (wheelSizePx * 0.97f).toInt() }
    val labelLayouts = remember(categories, labelMaxWidth) {
        categories.map { category ->
            textMeasurer.measure(
                text = "${category.icon}\n${category.label}",
                style = labelStyle,
                constraints = Constraints(maxWidth = labelMaxWidth),
            )
        }
    }
    val wheelBitmap = remember(categories, labelLayouts, wheelSizePx, density, layoutDirection) {
        renderWheelBitmap(
            sizePx = wheelSizePx,
            density = density,
            layoutDirection = layoutDirection,
            categories = categories,
            labelLayouts = labelLayouts,
        )
    }

    LaunchedEffect(spinSessionId) {
        if (spinSessionId == 0L) return@LaunchedEffect
        wheelRotation.snapTo(spinFromRotation)
        val degreesPerTick = 360f / categories.size.coerceAtLeast(1)
        var lastTickRotation = spinFromRotation
        val tickJob = launch {
            while (isActive) {
                delay(16)
                val current = wheelRotation.value
                while (current - lastTickRotation >= degreesPerTick) {
                    onSpinTick()
                    lastTickRotation += degreesPerTick
                }
            }
        }
        try {
            wheelRotation.animateTo(
                targetValue = targetRotation,
                animationSpec = tween(
                    durationMillis = PrizeWheelViewModel.SPIN_DURATION_MS.toInt(),
                    easing = WheelFrictionEasing,
                ),
            )
        } finally {
            tickJob.cancel()
        }
        onSpinComplete(wheelRotation.value)
    }

    Box(
        modifier = modifier.size(340.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(y = 18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f
                val center = Offset(size.width / 2f, size.height / 2f)
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x66030712), Color.Transparent),
                        center = center + Offset(0f, radius * 0.55f),
                        radius = radius * 1.1f,
                    ),
                    topLeft = Offset(center.x - radius * 1.15f, center.y - radius * 0.05f),
                    size = Size(radius * 2.3f, radius * 0.7f),
                )
            }

            SpinningWheelImage(
                rotationAnimatable = wheelRotation,
                bitmap = wheelBitmap,
                modifier = Modifier.fillMaxSize(),
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f
                val center = Offset(size.width / 2f, size.height / 2f)
                drawWheelOuterRim(center, radius)
            }
        }

        Pointer3D(
            modifier = Modifier.offset(y = 4.dp),
            glowing = saleState == SaleState.SPINNING,
        )
    }
}

/** Isolated layer: only this composable recomposes each animation frame. */
@Composable
private fun SpinningWheelImage(
    rotationAnimatable: Animatable<Float, AnimationVector1D>,
    bitmap: androidx.compose.ui.graphics.ImageBitmap,
    modifier: Modifier = Modifier,
) {
    val rotation = rotationAnimatable.value
    Image(
        bitmap = bitmap,
        contentDescription = null,
        modifier = modifier.graphicsLayer {
            rotationZ = rotation
            compositingStrategy = CompositingStrategy.Offscreen
        },
    )
}

private fun DrawScope.drawWheelOuterRim(center: Offset, radius: Float) {
    drawCircle(
        color = Color(0xFF374151),
        radius = radius + 3f,
        center = center,
        style = Stroke(width = 8f, cap = StrokeCap.Round),
    )
    drawCircle(
        color = ZuraColors.Accent,
        radius = radius,
        center = center,
        style = Stroke(width = 7f, cap = StrokeCap.Round),
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.3f),
        radius = radius - 1.5f,
        center = center,
        style = Stroke(width = 2f, cap = StrokeCap.Round),
    )
}

@Composable
private fun Pointer3D(
    modifier: Modifier = Modifier,
    glowing: Boolean = false,
) {
    Canvas(modifier = modifier.size(52.dp, 56.dp)) {
        val tip = Offset(size.width / 2f, size.height)
        val left = Offset(size.width * 0.08f, size.height * 0.08f)
        val right = Offset(size.width * 0.92f, size.height * 0.08f)

        val bodyPath = Path().apply {
            moveTo(tip.x, tip.y)
            lineTo(left.x, left.y)
            lineTo(right.x, right.y)
            close()
        }
        drawPath(bodyPath, color = Color.Black.copy(alpha = 0.35f))
        drawPath(
            path = bodyPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFDE68A), ZuraColors.Accent, Color(0xFFB45309)),
            ),
        )
        if (glowing) {
            drawPath(bodyPath, color = ZuraColors.Accent.copy(alpha = 0.18f))
        }
    }
}
