package com.zuratouch.feature.salecards.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class ConfettiIntensity {
    Normal,
    Celebration,
}

private data class ConfettiParticle(
    val originX: Float,
    val originY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float,
    val shape: Int,
    val delayMs: Int,
    val durationMs: Int,
)

private val confettiColors = listOf(
    Color(0xFFFFD700),
    Color(0xFFFDB022),
    Color(0xFF805AD5),
    Color(0xFF2B6CB0),
    Color(0xFFF8FAFC),
    Color(0xFFE7A22B),
    Color(0xFFFF6B6B),
    Color(0xFF48BB78),
)

@Composable
internal fun ConfettiEffect(
    active: Boolean,
    modifier: Modifier = Modifier,
    intensity: ConfettiIntensity = ConfettiIntensity.Normal,
    burstKey: Int = 0,
) {
    if (!active) return

    val particleCount = when (intensity) {
        ConfettiIntensity.Normal -> 48
        ConfettiIntensity.Celebration -> 160
    }

    val particles = remember(intensity) {
        List(particleCount) { index ->
            createParticle(index, intensity, Random(index * 31 + 7))
        }
    }
    val progress = remember(intensity) { particles.map { Animatable(0f) } }

    LaunchedEffect(active, burstKey, intensity) {
        coroutineScope {
            progress.forEachIndexed { index, animatable ->
                launch {
                    val particle = particles[index]
                    animatable.snapTo(0f)
                    animatable.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = particle.durationMs,
                            delayMillis = particle.delayMs,
                            easing = LinearEasing,
                        ),
                    )
                }
            }
        }
    }

    LaunchedEffect(active, intensity) {
        if (!active || intensity != ConfettiIntensity.Celebration) return@LaunchedEffect
        while (true) {
            delay(2200)
            coroutineScope {
                progress.forEachIndexed { index, animatable ->
                    launch {
                        val particle = particles[index]
                        animatable.snapTo(0f)
                        animatable.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = particle.durationMs,
                                delayMillis = particle.delayMs % 350,
                                easing = LinearEasing,
                            ),
                        )
                    }
                }
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEachIndexed { index, particle ->
            val t = progress[index].value
            if (t <= 0f) return@forEachIndexed
            val gravity = t * t * size.height * 0.35f
            val x = particle.originX * size.width + particle.velocityX * t
            val y = particle.originY * size.height + particle.velocityY * t + gravity
            val alpha = (1f - t * 0.55f).coerceIn(0f, 1f)
            val rotation = particle.rotationSpeed * t * 360f

            rotate(rotation, pivot = Offset(x, y)) {
                when (particle.shape % 3) {
                    0 -> drawRect(
                        color = particle.color.copy(alpha = alpha),
                        topLeft = Offset(x, y),
                        size = Size(particle.size, particle.size * 1.5f),
                    )
                    1 -> drawCircle(
                        color = particle.color.copy(alpha = alpha),
                        radius = particle.size * 0.55f,
                        center = Offset(x, y),
                    )
                    else -> {
                        val half = particle.size * 0.45f
                        drawRect(
                            color = particle.color.copy(alpha = alpha),
                            topLeft = Offset(x - half, y - half),
                            size = Size(half * 2f, half * 0.35f),
                        )
                    }
                }
            }
        }
    }
}

private fun createParticle(
    index: Int,
    intensity: ConfettiIntensity,
    random: Random,
): ConfettiParticle {
    val celebration = intensity == ConfettiIntensity.Celebration
    val angle = random.nextFloat() * 6.28f
    val speed = if (celebration) random.nextFloat() * 420f + 80f else random.nextFloat() * 180f + 40f
    val fromCenter = celebration && index % 4 == 0
    val originX = if (fromCenter) 0.48f + random.nextFloat() * 0.04f else random.nextFloat()
    val originY = if (fromCenter) 0.22f + random.nextFloat() * 0.08f else random.nextFloat() * -0.25f
    val vx = if (fromCenter) cos(angle) * speed else random.nextFloat() * 160f - 80f
    val vy = if (fromCenter) sin(angle).coerceAtMost(0.2f) * speed - 120f else random.nextFloat() * 80f + 40f

    return ConfettiParticle(
        originX = originX,
        originY = originY,
        velocityX = vx,
        velocityY = vy,
        size = random.nextFloat() * (if (celebration) 10f else 6f) + (if (celebration) 5f else 4f),
        color = confettiColors.random(random),
        rotationSpeed = random.nextFloat() * 2.5f + 0.5f,
        shape = random.nextInt(3),
        delayMs = random.nextInt(if (celebration) 500 else 400),
        durationMs = random.nextInt(if (celebration) 1400 else 1000) + (if (celebration) 2000 else 1800),
    )
}
