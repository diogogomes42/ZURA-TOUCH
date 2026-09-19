package com.zuratouch.prizewheel.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PrizeTierCustomerInfoTest {
    @Test
    fun `prime tier lists mythic and legendary only`() {
        val tier = PrizeTierCatalog.tierByIdOrThrow(PrizeTierCatalog.PRIME)
        val label = PrizeTierCustomerInfo.formatPrizeCategories(tier)
        assertEquals("Mítico · Lendário", label)
    }

    @Test
    fun `pro tier includes legendary`() {
        val tier = PrizeTierCatalog.tierByIdOrThrow(PrizeTierCatalog.PRO)
        val label = PrizeTierCustomerInfo.formatPrizeCategories(tier)
        assertTrue(label.contains("Lendário"))
        assertEquals("Raro · Épico · Mítico · Lendário", label)
    }

    @Test
    fun `lite tier includes basic through legendary`() {
        val tier = PrizeTierCatalog.tierByIdOrThrow(PrizeTierCatalog.LITE)
        val label = PrizeTierCustomerInfo.formatPrizeCategories(tier)
        assertTrue(label.contains("Básico"))
        assertTrue(label.contains("Lendário"))
    }

    @Test
    fun `entries never expose economics or basis points`() {
        val entries = PrizeTierCustomerInfo.entriesFor(PrizeTierCatalog.tiers)
        entries.forEach { entry ->
            assertFalse(entry.headline.contains("9400"))
            assertFalse(entry.prizeCategoriesLabel.contains("€"))
        }
    }
}
