package com.zuratouch.prizewheel.ui.components

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
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

private data class ConfettiParticle(
    val x: Float,
    val startY: Float,
    val size: Float,
    val color: Color,
    val drift: Float,
    val delayMs: Int,
    val durationMs: Int,
)

private val confettiColors = listOf(
    Color(0xFFFDB022),
    Color(0xFF805AD5),
    Color(0xFF2B6CB0),
    Color(0xFFF8FAFC),
    Color(0xFFE7A22B),
)

@Composable
fun ConfettiEffect(
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!active) return

    val particles = remember {
        List(48) {
            ConfettiParticle(
                x = Random.nextFloat(),
                startY = Random.nextFloat() * -0.3f,
                size = Random.nextFloat() * 8f + 4f,
                color = confettiColors.random(),
                drift = Random.nextFloat() * 120f - 60f,
                delayMs = Random.nextInt(400),
                durationMs = Random.nextInt(1200) + 1800,
            )
        }
    }
    val progress = remember { particles.map { Animatable(0f) } }

    LaunchedEffect(active) {
        progress.forEachIndexed { index, animatable ->
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

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEachIndexed { index, particle ->
            val t = progress[index].value
            if (t <= 0f) return@forEachIndexed
            val x = particle.x * size.width + particle.drift * t
            val y = particle.startY * size.height + t * size.height * 1.2f
            drawRect(
                color = particle.color.copy(alpha = 1f - t * 0.6f),
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(particle.size, particle.size * 1.6f),
            )
        }
    }
}
