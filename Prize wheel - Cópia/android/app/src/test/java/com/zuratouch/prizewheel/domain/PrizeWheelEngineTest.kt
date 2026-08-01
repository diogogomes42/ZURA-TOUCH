package com.zuratouch.prizewheel.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class PrizeWheelEngineTest {
    private val engine = PrizeWheelEngine()
    private val categories = listOf(PrizeCategory("a", "A", 0), PrizeCategory("b", "B", 0))

    @Test fun `excludes an empty category from the wheel`() {
        val stock = listOf(
            ProductSlot("1", "a", "Produto", 1, 1, 1, 0),
            ProductSlot("2", "b", "Produto", 1, 2, 2, 1),
        )
        assertEquals(listOf("b"), engine.availableCategories(categories, stock).map { it.id })
    }

    @Test fun `decrements stock only after a delivery`() {
        val initial = listOf(ProductSlot("1", "a", "Produto", 1, 1, 1, 1))
        val updated = engine.registerSuccessfulDelivery(initial, "1")
        assertEquals(0, updated.single().quantity)
        assertFalse(updated.single().isAvailable)
    }

    @Test fun `selects the front slot in a category`() {
        val categories = listOf(PrizeCategory("drink", "Bebida", 0))
        val stock = listOf(
            ProductSlot("back", "drink", "Back", 2, 1, 2, 1),
            ProductSlot("front", "drink", "Front", 1, 2, 1, 1),
        )
        val sale = engine.createSale(categories, stock)
        assertEquals("front", sale?.productSlot?.id)
    }

    @Test fun `respects category weights over many draws`() {
        val categories = listOf(
            PrizeCategory("common", "Comum", 0, weight = 90),
            PrizeCategory("rare", "Raro", 0, weight = 10),
        )
        val stock = listOf(
            ProductSlot("1", "common", "Comum", 1, 1, 1, 100),
            ProductSlot("2", "rare", "Raro", 1, 2, 2, 100),
        )
        val weightedEngine = PrizeWheelEngine(Random(1))
        val picks = List(1000) { weightedEngine.pickWeightedCategory(categories)?.id }
        val rareCount = picks.count { it == "rare" }
        assertTrue(rareCount in 50..180)
    }
}
