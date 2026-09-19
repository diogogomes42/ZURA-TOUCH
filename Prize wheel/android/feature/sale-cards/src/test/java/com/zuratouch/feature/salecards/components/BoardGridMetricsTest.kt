package com.zuratouch.feature.salecards.components

import androidx.compose.ui.unit.Density
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BoardGridMetricsTest {
    private val density = Density(1f)

    @Test
    fun `board grid uses six columns for twenty four cards`() {
        val metrics = computeBoardGridMetrics(
            areaWidthPx = 360f,
            areaHeightPx = 420f,
            slotCount = 24,
            density = density,
        )
        assertEquals(6, metrics.columns)
        assertEquals(4, (24 + 6 - 1) / 6)
        assertTrue(metrics.gridHeightPx <= 420f)
        assertTrue(metrics.gridWidthPx <= 360f)
    }

    @Test
    fun `board grid shrinks when winner band is reserved`() {
        val full = computeBoardGridMetrics(360f, 420f, 24, density)
        val reserved = computeBoardGridMetrics(360f, 420f, 24, density, reservedTopPx = 120f)
        assertTrue(reserved.cardHeightPx < full.cardHeightPx)
        assertTrue(reserved.gridHeightPx + 120f <= 420f)
    }
}
