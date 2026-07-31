package com.zuratouch.prizewheel.ui

import com.zuratouch.prizewheel.domain.PrizeCategory
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class WheelRotationCalculatorTest {
    private val categories = listOf(
        PrizeCategory("a", "A", 0xFF0000),
        PrizeCategory("b", "B", 0x00FF00),
        PrizeCategory("c", "C", 0x0000FF),
    )

    @Test fun `target rotation advances at least seven full spins`() {
        val target = WheelRotationCalculator.targetRotation(0f, categories, "b")
        require(target != null)
        assertTrue(target >= 7 * 360f)
    }

    @Test fun `target rotation lands segment center under pointer not on divider`() {
        val sweep = 360f / categories.size
        val pointerAngle = -90f
        categories.forEachIndexed { index, category ->
            val target = WheelRotationCalculator.targetRotation(0f, categories, category.id)
            require(target != null)
            val segmentCenterOnWheel = index * sweep + sweep / 2f - 90f
            val rotationMod = ((target % 360f) + 360f) % 360f
            val landedCenter = ((segmentCenterOnWheel + rotationMod) % 360f + 360f) % 360f
            val pointerMod = ((pointerAngle % 360f) + 360f) % 360f
            val distance = abs(landedCenter - pointerMod)
            assertTrue(distance < 1f || distance > 359f)
        }
    }

    @Test fun `target rotation works with a single available category`() {
        val single = listOf(PrizeCategory("only", "Bebida", 0xFF3775C7))
        val target = WheelRotationCalculator.targetRotation(0f, single, "only")
        require(target != null)
        assertTrue(target >= 7 * 360f)
    }

    @Test fun `returns null when winning category is not on wheel`() {
        assertNull(WheelRotationCalculator.targetRotation(0f, categories, "missing"))
    }
}
