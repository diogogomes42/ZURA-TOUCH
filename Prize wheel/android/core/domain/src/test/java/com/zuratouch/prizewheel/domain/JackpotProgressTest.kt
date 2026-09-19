package com.zuratouch.prizewheel.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class JackpotProgressTest {
    @Test
    fun `progress scales with accumulated revenue`() {
        val half = JackpotProgress.displayStateFor(
            JackpotState(accumulatedRevenueCents = 300_000L, enabled = true),
        )
        assertEquals(0.5f, half.progress, 0.001f)
        assertFalse(half.isGuaranteeReady)
    }

    @Test
    fun `marks guarantee ready at threshold without exposing amounts`() {
        val ready = JackpotProgress.displayStateFor(
            JackpotState(
                accumulatedRevenueCents = JackpotConfig.LEGENDARY_GUARANTEE_THRESHOLD_CENTS,
                enabled = true,
            ),
        )
        assertTrue(ready.isGuaranteeReady)
        assertEquals(1f, ready.progress, 0.001f)
    }

    @Test
    fun `hidden when jackpot disabled`() {
        val hidden = JackpotProgress.displayStateFor(
            JackpotState(accumulatedRevenueCents = 500_000L, enabled = false),
        )
        assertFalse(hidden.visible)
    }
}
