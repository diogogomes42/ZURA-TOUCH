package com.zuratouch.prizewheel.ui

import com.zuratouch.prizewheel.domain.PrizeCategory

object WheelRotationCalculator {
    /** Pointer fixed at 12 o'clock; segments drawn with start at [index * sweep - 90]. */
    fun targetRotation(currentRotation: Float, categories: List<PrizeCategory>, winningCategoryId: String): Float? {
        if (categories.isEmpty()) return null
        val index = categories.indexOfFirst { it.id == winningCategoryId }
        if (index < 0) return null
        val sweep = 360f / categories.size
        val segmentCenterOnWheel = index * sweep + sweep / 2f - 90f
        val pointerAngle = -90f
        val desiredMod = normalizeAngle(pointerAngle - segmentCenterOnWheel)
        val currentMod = normalizeAngle(currentRotation)
        var delta = desiredMod - currentMod
        if (delta <= 0f) delta += 360f
        val fullSpins = 7
        return currentRotation + fullSpins * 360f + delta
    }

    private fun normalizeAngle(degrees: Float): Float = ((degrees % 360f) + 360f) % 360f
}
