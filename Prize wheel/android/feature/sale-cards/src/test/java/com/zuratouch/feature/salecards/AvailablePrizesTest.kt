package com.zuratouch.feature.salecards

import com.zuratouch.prizewheel.domain.PrizeCategory
import com.zuratouch.prizewheel.domain.QueuedProduct
import com.zuratouch.prizewheel.domain.Spiral
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class AvailablePrizesTest {
    private val categories = listOf(
        PrizeCategory("common", "Comum", 0xFF718096),
        PrizeCategory("rare", "Raro", 0xFF3182CE),
    )

    private fun spiral(queue: List<QueuedProduct>) =
        Spiral("A1", 1, 1, 1, 10, queue)

    @Test
    fun `fromStock resolves catalog tier over stock queue category`() {
        val stock = listOf(
            spiral(listOf(QueuedProduct("mythic", "iPhone"))),
            spiral(listOf(QueuedProduct("rare", "MacBook"))),
        )
        val prizes = AvailablePrizes.fromStock(categories + listOf(
            PrizeCategory("legendary", "Lendário", 0xFFD69E2E, weight = 3),
            PrizeCategory("mythic", "Mítico", 0xFFE53E3E, weight = 7),
        ), stock, Random(1))

        val iphone = prizes.first { it.productName == "iPhone" }
        assertEquals("legendary", iphone.categoryId)
        assertEquals("Lendário", iphone.categoryLabel)

        val macbook = prizes.first { it.productName == "MacBook" }
        assertEquals("mythic", macbook.categoryId)
    }

    @Test
    fun `fromStock returns unique products in machine queues`() {
        val stock = listOf(
            spiral(listOf(QueuedProduct("common", "Pato de Borracha"), QueuedProduct("common", "Pato de Borracha"))),
            spiral(listOf(QueuedProduct("rare", "MacBook"))),
        )
        val prizes = AvailablePrizes.fromStock(categories, stock, Random(1))
        assertEquals(2, prizes.size)
        assertTrue(prizes.any { it.productName == "MacBook" && it.imageRes == null })
        assertTrue(prizes.any { it.productName == "Pato de Borracha" })
    }

    @Test
    fun `fromStock shuffles order between seeds`() {
        val stock = listOf(
            Spiral("A1", 1, 1, 1, 10, List(5) { QueuedProduct("common", "A") }),
            Spiral("B1", 1, 2, 2, 10, List(5) { QueuedProduct("rare", "B") }),
            Spiral("C1", 2, 1, 3, 10, List(5) { QueuedProduct("common", "C") }),
            Spiral("D1", 2, 2, 4, 10, List(5) { QueuedProduct("rare", "D") }),
        )
        val orderA = AvailablePrizes.fromStock(categories, stock, Random(7)).map { it.productName }
        val orderB = AvailablePrizes.fromStock(categories, stock, Random(99)).map { it.productName }
        assertNotEquals(orderA, orderB)
    }

    @Test
    fun `fromStock caps at 28 products`() {
        val stock = List(30) { index ->
            Spiral("S$index", 1, 1, 1, 10, listOf(QueuedProduct("common", "Produto $index")))
        }
        val prizes = AvailablePrizes.fromStock(categories, stock, Random(1))
        assertEquals(28, prizes.size)
    }
}
