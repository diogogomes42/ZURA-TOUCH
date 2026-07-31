package com.zuratouch.prizewheel.ui

import androidx.compose.animation.core.Easing
import kotlin.math.pow

/** Strong ease-out curve that mimics a wheel slowing down by friction. */
object WheelFrictionEasing : Easing {
    override fun transform(fraction: Float): Float {
        val t = fraction.coerceIn(0f, 1f)
        return 1f - (1f - t).pow(5.5f)
    }
}
