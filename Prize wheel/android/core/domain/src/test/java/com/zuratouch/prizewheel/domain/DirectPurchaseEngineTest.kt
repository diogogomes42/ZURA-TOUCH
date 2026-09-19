package com.zuratouch.prizewheel.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DirectPurchaseEngineTest {
    private val engine = DirectPurchaseEngine()

    private fun spiral(
        id: String,
        queue: List<QueuedProduct>,
    ) = Spiral(id, 1, 1, 1, 10, queue)

    private val categories = listOf(
        PrizeCategory(PrizeProductCatalog.COMMON, "Comum", PrizeProductCatalog.catalogColorFor(PrizeProductCatalog.COMMON)),
        PrizeCategory(PrizeProductCatalog.RARE, "Raro", PrizeProductCatalog.catalogColorFor(PrizeProductCatalog.RARE)),
        PrizeCategory(PrizeProductCatalog.EPIC, "Épico", PrizeProductCatalog.catalogColorFor(PrizeProductCatalog.EPIC)),
    )

    @Test
    fun `physical offers include only common and rare front products`() {
        val stock = listOf(
            spiral("1", listOf(QueuedProduct(PrizeProductCatalog.COMMON, "Barra de Chocolate"))),
            spiral("2", listOf(QueuedProduct(PrizeProductCatalog.RARE, "Powerbank"))),
            spiral("3", listOf(QueuedProduct(PrizeProductCatalog.EPIC, "Auscultadores Bluetooth"))),
            spiral("4", emptyList()),
        )

        val offers = engine.physicalOffers(categories, stock)

        assertEquals(2, offers.size)
        assertTrue(offers.all { it.kind == DirectPurchaseKind.PHYSICAL })
        assertEquals(setOf("Barra de Chocolate", "Powerbank"), offers.map { it.productName }.toSet())
        assertFalse(offers.any { it.categoryId == PrizeProductCatalog.EPIC })
    }

    @Test
    fun `physical offers deduplicate same product on multiple spirals`() {
        val stock = listOf(
            spiral("1", listOf(QueuedProduct(PrizeProductCatalog.COMMON, "Barra de Chocolate"))),
            spiral("2", listOf(QueuedProduct(PrizeProductCatalog.COMMON, "Barra de Chocolate"))),
        )

        val offers = engine.physicalOffers(categories, stock)

        assertEquals(1, offers.size)
    }

    @Test
    fun `physical offers exclude products not at front`() {
        val stock = listOf(
            spiral(
                "1",
                listOf(
                    QueuedProduct(PrizeProductCatalog.RARE, "Outro"),
                    QueuedProduct(PrizeProductCatalog.COMMON, "Barra de Chocolate"),
                ),
            ),
        )

        val offers = engine.physicalOffers(categories, stock)

        assertEquals(1, offers.size)
        assertEquals(PrizeProductCatalog.RARE, offers.single().categoryId)
        assertEquals("Outro", offers.single().productName)
    }

    @Test
    fun `build catalog always includes digital offers`() {
        val catalog = engine.buildCatalog(categories, emptyList())

        assertTrue(catalog.machineOffers.isEmpty())
        assertTrue(catalog.digitalOffers.isNotEmpty())
        assertTrue(catalog.digitalOffers.all { it.kind == DirectPurchaseKind.DIGITAL })
        assertTrue(catalog.digitalOffers.any { it.digitalType == DirectPurchaseDigitalType.EXPERIENCE })
        assertTrue(catalog.digitalOffers.any { it.digitalType == DirectPurchaseDigitalType.GIFT_CARD })
    }

    @Test
    fun `physical prices follow direct purchase catalog`() {
        val stock = listOf(
            spiral("1", listOf(QueuedProduct(PrizeProductCatalog.COMMON, "Barra de Chocolate"))),
            spiral("2", listOf(QueuedProduct(PrizeProductCatalog.RARE, "Powerbank"))),
        )

        val offers = engine.physicalOffers(categories, stock)
        val common = offers.single { it.categoryId == PrizeProductCatalog.COMMON }
        val rare = offers.single { it.categoryId == PrizeProductCatalog.RARE }

        assertEquals(800L, common.priceCents)
        assertEquals(1_500L, rare.priceCents)
    }
}
