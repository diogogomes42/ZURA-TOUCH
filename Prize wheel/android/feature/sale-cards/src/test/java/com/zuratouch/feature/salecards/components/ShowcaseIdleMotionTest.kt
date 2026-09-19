package com.zuratouch.feature.salecards.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ShowcaseIdleMotionTest {
    @Test
    fun `phase loop is seamless at cycle boundary`() {
        val driftX = 10f
        val driftY = 8f
        val start = ShowcaseIdleMotion.layerForCard(0, 0f, 1f, 0f, driftX, driftY)
        val end = ShowcaseIdleMotion.layerForCard(0, ShowcaseIdleMotion.TWO_PI, 1f, 0f, driftX, driftY)
        assertEquals(start.translationXPx, end.translationXPx, 0.01f)
        assertEquals(start.translationYPx, end.translationYPx, 0.01f)
    }

    @Test
    fun `motion settles when intensity is zero`() {
        val layer = ShowcaseIdleMotion.layerForCard(3, 1.5f, 0f, 4f, 10f, 8f)
        assertEquals(0f, layer.translationXPx, 0f)
        assertEquals(0f, layer.translationYPx, 0f)
        assertEquals(4f, layer.rotationZ, 0f)
        assertEquals(0f, layer.rotationY, 0f)
    }

    @Test
    fun `rotation stays within readable bounds`() {
        val layer = ShowcaseIdleMotion.layerForCard(
            index = 5,
            phase = 1.2f,
            intensity = 1f,
            baseRotationZ = 5f,
            driftXPx = 12f,
            driftYPx = 10f,
        )
        assertTrue(abs(layer.rotationZ) <= 7f)
        assertTrue(abs(layer.rotationY) <= 3f)
    }
}
