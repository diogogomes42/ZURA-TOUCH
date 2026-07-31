package com.zuratouch.prizewheel.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class PrizeWheelEngineTest {
    private val engine = PrizeWheelEngine()
    private val categories = listOf(PrizeCategory("a", "A", 0), PrizeCategory("b", "B", 0))

    private fun spiral(
        id: String,
        shelf: Int,
        slot: Int,
        queue: List<QueuedProduct>,
    ) = Spiral(id, shelf, slot, shelf * 10 + slot, 15, queue)

    @Test fun `excludes a category with no products anywhere in the machine`() {
        val stock = listOf(
            spiral("1", 1, 1, emptyList()),
            spiral("2", 1, 2, listOf(QueuedProduct("b", "Produto"))),
        )
        assertEquals(listOf("b"), engine.availableCategories(categories, stock).map { it.id })
    }

    @Test fun `category stays on wheel when products exist but not at front`() {
        val stock = listOf(
            spiral("1", 1, 1, listOf(QueuedProduct("b", "Outro"), QueuedProduct("a", "Produto"))),
        )
        assertEquals(listOf("a", "b"), engine.availableCategories(categories, stock).map { it.id }.sorted())
        assertEquals(listOf("b"), engine.dispensableCategories(categories, stock).map { it.id })
    }

    @Test fun `createSale only draws categories with a front product`() {
        val stock = listOf(
            spiral("1", 1, 1, listOf(QueuedProduct("b", "Outro"), QueuedProduct("a", "Produto"))),
            spiral("2", 1, 2, listOf(QueuedProduct("b", "Na frente"))),
        )
        val weightedEngine = PrizeWheelEngine(Random(0))
        repeat(50) {
            val sale = weightedEngine.createSale(categories, stock)
            require(sale != null)
            assertEquals("b", sale.category.id)
        }
        val sale = weightedEngine.createSale(categories, stock)!!
        assertEquals(listOf("a", "b"), sale.wheelCategories.map { it.id }.sorted())
    }

    @Test fun `createSale blocked when nothing is dispensable at front`() {
        val stock = listOf(
            spiral("1", 1, 1, listOf(QueuedProduct("c", "Outro"), QueuedProduct("a", "Produto"))),
        )
        assertNull(engine.createSale(categories, stock))
    }

    @Test fun `pops front product after delivery`() {
        val initial = listOf(
            spiral("1", 1, 1, listOf(QueuedProduct("a", "Primeiro"), QueuedProduct("b", "Segundo"))),
        )
        val updated = engine.registerSuccessfulDelivery(initial, "1")
        assertEquals("b", updated.single().frontProduct?.categoryId)
        assertEquals(1, updated.single().queueLength)
    }

    @Test fun `createSale picks category only not spiral`() {
        val stock = listOf(
            spiral("low", 1, 1, List(2) { QueuedProduct("drink", "Água") }),
            spiral("high", 1, 2, List(5) { QueuedProduct("drink", "Refrigerante") }),
        )
        val drinkCategories = listOf(PrizeCategory("drink", "Bebida", 0))
        val sale = engine.createSale(drinkCategories, stock)
        assertEquals("drink", sale?.category?.id)
    }

    @Test fun `dispense picks spiral with highest queue among front matches`() {
        val stock = listOf(
            spiral("low", 1, 1, listOf(QueuedProduct("drink", "Água")) + List(1) { QueuedProduct("snack", "X") }),
            spiral("high", 1, 2, List(5) { QueuedProduct("drink", "Refrigerante") }),
        )
        assertEquals("high", engine.pickSpiralForCategory(stock, "drink")?.id)
    }

    @Test fun `when queue lengths tie picks lower shelf first`() {
        val stock = listOf(
            spiral("second", 2, 1, List(3) { QueuedProduct("drink", "B") }),
            spiral("first", 1, 1, List(3) { QueuedProduct("drink", "A") }),
        )
        assertEquals("first", engine.pickSpiralForCategory(stock, "drink")?.id)
    }

    @Test fun `spiral with no front match is ignored for dispense`() {
        val stock = listOf(
            spiral("wrong-front", 1, 1, listOf(QueuedProduct("snack", "Chips"), QueuedProduct("drink", "Água"))),
            spiral("match", 1, 2, listOf(QueuedProduct("drink", "Cola"))),
        )
        assertEquals("match", engine.pickSpiralForCategory(stock, "drink")?.id)
    }

    @Test fun `dispense order prefers highest queue length`() {
        val stock = listOf(
            spiral("low", 1, 1, listOf(QueuedProduct("drink", "Água")) + List(1) { QueuedProduct("x", "y") }),
            spiral("high", 3, 2, listOf(QueuedProduct("drink", "Sumo")) + List(10) { QueuedProduct("x", "y") }),
            spiral("mid", 2, 1, listOf(QueuedProduct("drink", "Cola")) + List(4) { QueuedProduct("x", "y") }),
        )
        val order = engine.spiralsForDispense(stock, "drink").map { it.id }
        assertEquals(listOf("high", "mid", "low"), order)
    }

    @Test fun `createSale freezes wheel categories at purchase time`() {
        val stock = listOf(
            spiral("1", 1, 1, listOf(QueuedProduct("a", "Produto"))),
            spiral("2", 1, 2, listOf(QueuedProduct("b", "Produto"))),
        )
        val sale = engine.createSale(categories, stock)
        assertEquals(listOf("a", "b"), sale?.wheelCategories?.map { it.id }?.sorted())
    }

    @Test fun `respects category weights over many draws`() {
        val categories = listOf(
            PrizeCategory("common", "Comum", 0, weight = 90),
            PrizeCategory("rare", "Raro", 0, weight = 10),
        )
        val stock = listOf(
            spiral("1", 1, 1, List(10) { QueuedProduct("common", "Comum") }),
            spiral("2", 1, 2, List(10) { QueuedProduct("rare", "Raro") }),
        )
        val weightedEngine = PrizeWheelEngine(Random(1))
        val picks = List(1000) { weightedEngine.pickWeightedCategory(categories)?.id }
        val rareCount = picks.count { it == "rare" }
        assertTrue(rareCount in 50..180)
    }

    @Test fun `after pop new front may be different category`() {
        val stock = listOf(
            spiral("1", 1, 1, listOf(QueuedProduct("a", "A"), QueuedProduct("b", "B"))),
        )
        val after = engine.registerSuccessfulDelivery(stock, "1")
        assertFalse(after.single().frontMatchesCategory("a"))
        assertTrue(after.single().frontMatchesCategory("b"))
    }
}
