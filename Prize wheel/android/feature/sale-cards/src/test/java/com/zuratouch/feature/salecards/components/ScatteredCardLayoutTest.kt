package com.zuratouch.feature.salecards.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScatteredCardLayoutTest {
    @Test
    fun `places all cards within allowed overlap budget`() {
        val allowedOverlap = CardGridSpec.SHOWCASE_ALLOWED_OVERLAP_FRACTION
        val placements = ScatteredCardLayout.compute(
            count = 28,
            areaWidthPx = 360f,
            areaHeightPx = 520f,
            gapPx = 12f,
            seed = 42L,
        )
        assertEquals(28, placements.size)
        for (i in placements.indices) {
            for (j in i + 1 until placements.size) {
                assertFalse(
                    "Cards $i and $j overlap too much",
                    ScatteredCardLayout.overlapsTooMuch(placements[i], placements[j], allowedOverlap),
                )
            }
        }
    }

    @Test
    fun `allows overlap up to ten percent of card area`() {
        val a = ScatteredCardPlacement(0f, 0f, 100f, 100f, 0f)
        val b = ScatteredCardPlacement(92f, 0f, 100f, 100f, 0f)
        assertFalse(ScatteredCardLayout.overlapsTooMuch(a, b, 0.10f))
        val c = ScatteredCardPlacement(80f, 0f, 100f, 100f, 0f)
        assertTrue(ScatteredCardLayout.overlapsTooMuch(a, c, 0.10f))
    }

    @Test
    fun `same seed produces same layout`() {
        val first = ScatteredCardLayout.compute(12, 320f, 400f, 10f, seed = 7L)
        val second = ScatteredCardLayout.compute(12, 320f, 400f, 10f, seed = 7L)
        assertEquals(first, second)
    }
}
