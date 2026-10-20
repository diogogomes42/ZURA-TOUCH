package com.zuratouch.feature.salecards.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.feature.salecards.R
import com.zuratouch.feature.salecards.theme.ZuraColors
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
internal fun GoldenFlashOverlay(
    trigger: Int,
    modifier: Modifier = Modifier,
) {
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(trigger) {
        if (trigger <= 0) return@LaunchedEffect
        alpha.snapTo(0f)
        alpha.animateTo(0.55f, tween(120, easing = FastOutSlowInEasing))
        alpha.animateTo(0f, tween(480, easing = FastOutSlowInEasing))
    }
    if (alpha.value <= 0f) return
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val centerX = with(density) { maxWidth.toPx() * 0.5f }
        val centerY = with(density) { maxHeight.toPx() * 0.28f }
        val radius = with(density) { maxWidth.toPx().coerceAtLeast(maxHeight.toPx()) * 0.85f }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFD700).copy(alpha = alpha.value * 0.7f),
                            Color(0xFFFDB022).copy(alpha = alpha.value * 0.25f),
                            Color.Transparent,
                        ),
                        center = Offset(centerX, centerY),
                        radius = radius,
                    ),
                ),
        )
    }
}

@Composable
internal fun WinnerGlowBackdrop(
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!active) return
    val pulse = rememberInfiniteTransition(label = "winnerGlow")
    val glow by pulse.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowPulse",
    )
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val centerX = with(density) { maxWidth.toPx() * 0.5f }
        val centerY = with(density) { maxHeight.toPx() * 0.22f }
        val radius = with(density) { maxWidth.toPx().coerceAtLeast(maxHeight.toPx()) * 0.75f }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ZuraColors.Accent.copy(alpha = glow * 0.28f),
                            Color(0xFFFFD700).copy(alpha = glow * 0.12f),
                            Color.Transparent,
                        ),
                        center = Offset(centerX, centerY),
                        radius = radius,
                    ),
                ),
        )
    }
}

@Composable
internal fun CelebrationBanner(
    visible: Boolean,
    prizeName: String?,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(350)) + scaleIn(initialScale = 0.65f, animationSpec = tween(550, easing = FastOutSlowInEasing)),
        exit = fadeOut(tween(250)),
        modifier = modifier,
    ) {
        val shimmer = rememberInfiniteTransition(label = "bannerShimmer")
        val scale by shimmer.animateFloat(
            initialValue = 1f,
            targetValue = 1.02f,
            animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
            label = "bannerScale",
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            ZuraColors.WheelRing,
                            Color(0xFF1A2740),
                            ZuraColors.WheelRing,
                        ),
                    ),
                )
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.prize_congrats),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center,
                )
                if (!prizeName.isNullOrBlank()) {
                    Text(
                        text = prizeName,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 2,
                    )
                }
            }
        }
    }
}

@Composable
internal fun SparkleBurst(
    active: Boolean,
    burstKey: Int,
    modifier: Modifier = Modifier,
) {
    if (!active) return
    val sparkles = remember {
        List(24) {
            Sparkle(
                angle = Random.nextFloat() * 6.28f,
                distance = Random.nextFloat() * 0.35f + 0.15f,
                size = Random.nextFloat() * 6f + 3f,
                delay = Random.nextInt(300),
            )
        }
    }
    val progress = remember { sparkles.map { Animatable(0f) } }
    LaunchedEffect(active, burstKey) {
        progress.forEach { it.snapTo(0f) }
        progress.forEachIndexed { index, animatable ->
            animatable.animateTo(1f, tween(900, delayMillis = sparkles[index].delay, easing = FastOutSlowInEasing))
        }
    }
    val pulse = rememberInfiniteTransition(label = "sparklePulse")
    val twinkle by pulse.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "twinkle",
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        val cx = size.width * 0.5f
        val cy = size.height * 0.24f
        sparkles.forEachIndexed { index, sparkle ->
            val t = progress[index].value
            if (t <= 0f) return@forEachIndexed
            val radius = sparkle.distance * size.width * t
            val x = cx + cos(sparkle.angle) * radius
            val y = cy + sin(sparkle.angle) * radius * 0.6f
            val alpha = (1f - t).coerceIn(0f, 1f) * twinkle
            drawCircle(
                color = Color(0xFFFFD700).copy(alpha = alpha),
                radius = sparkle.size * (1f + t * 0.5f),
                center = Offset(x, y),
            )
            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.7f),
                radius = sparkle.size * 0.35f,
                center = Offset(x, y),
            )
        }
    }
}

private data class Sparkle(
    val angle: Float,
    val distance: Float,
    val size: Float,
    val delay: Int,
)
