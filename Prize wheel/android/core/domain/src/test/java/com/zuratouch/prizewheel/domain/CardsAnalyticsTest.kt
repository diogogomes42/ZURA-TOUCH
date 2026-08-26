package com.zuratouch.prizewheel.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CardsAnalyticsTest {
    @Test
    fun `summarize counts successful card rounds and target picks`() {
        val sales = listOf(
            sale(SaleLogResult.SUCCESS, "experience=CARDS;wasTargetSlot=true"),
            sale(SaleLogResult.SUCCESS, "experience=CARDS;wasTargetSlot=false"),
            sale(SaleLogResult.PAYMENT_FAILED, "experience=CARDS;wasTargetSlot=false"),
            sale(SaleLogResult.SUCCESS, "wheel spin"),
        )
        val summary = CardsAnalytics.summarize(sales)
        assertEquals(2, summary.completedRounds)
        assertEquals(1, summary.targetSlotPicks)
        assertEquals(0.5, summary.observedTargetRate!!, 0.0001)
        assertEquals(1.0 / 24.0, summary.expectedRandomRate, 0.0001)
        assertTrue(summary.deltaFromRandom!! > 0.4)
    }

    @Test
    fun `summarize returns null rate when no card rounds`() {
        val summary = CardsAnalytics.summarize(emptyList())
        assertEquals(0, summary.completedRounds)
        assertNull(summary.observedTargetRate)
    }

    @Test
    fun `parseWasTargetSlot reads audit flag`() {
        assertEquals(true, CardsAnalytics.parseWasTargetSlot("experience=CARDS;wasTargetSlot=true"))
        assertEquals(false, CardsAnalytics.parseWasTargetSlot("experience=CARDS;wasTargetSlot=false"))
        assertNull(CardsAnalytics.parseWasTargetSlot("experience=WHEEL"))
    }

    private fun sale(result: SaleLogResult, message: String) = SaleLogEntry(
        id = 1,
        timestampMs = 0,
        categoryLabel = "Comum",
        productName = "Água",
        vmcLane = 1,
        result = result,
        message = message,
    )
}
