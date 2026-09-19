package com.zuratouch.feature.salecards.components

import kotlin.math.cos
import kotlin.math.sin

/** Subtle, seamless idle drift for scattered showcase cards — one shared phase, per-card offset. */
object ShowcaseIdleMotion {
    const val CYCLE_MS = 9_000
    const val TWO_PI = (Math.PI * 2).toFloat()

    data class Layer(
        val translationXPx: Float,
        val translationYPx: Float,
        val rotationZ: Float,
        val rotationY: Float,
    )

    fun layerForCard(
        index: Int,
        phase: Float,
        intensity: Float,
        baseRotationZ: Float,
        driftXPx: Float,
        driftYPx: Float,
    ): Layer {
        if (intensity <= 0f) {
            return Layer(0f, 0f, baseRotationZ, 0f)
        }
        val cardPhase = phase + index * 0.82f
        return Layer(
            translationXPx = sin(cardPhase) * driftXPx * intensity,
            translationYPx = cos(cardPhase + 0.65f) * driftYPx * intensity,
            rotationZ = baseRotationZ + sin(cardPhase + 0.35f) * 1.1f * intensity,
            rotationY = sin(cardPhase + index * 0.15f + 0.5f) * 2.2f * intensity,
        )
    }
}
