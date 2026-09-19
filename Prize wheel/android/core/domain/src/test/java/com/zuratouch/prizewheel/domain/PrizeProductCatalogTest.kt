package com.zuratouch.prizewheel.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class PrizeProductCatalogTest {
    private val categories = listOf(
        PrizeCategory("common", "Comum", 0xFF718096, weight = 50),
        PrizeCategory("rare", "Raro", 0xFF3182CE, weight = 25),
        PrizeCategory("epic", "Épico", 0xFF805AD5, weight = 15),
    )

    @Test
    fun `resolveCategory uses catalog tier for known product names`() {
        val category = PrizeProductCatalog.resolveCategory(
            productName = "Funko Pop",
            stockCategoryId = "common",
            categories = categories,
        )
        assertEquals("epic", category.id)
        assertEquals("Épico", category.label)
        assertEquals(0xFF805AD5, category.color)
    }

    @Test
    fun `iPhone is legendary in catalog`() {
        assertEquals(PrizeProductCatalog.LEGENDARY, PrizeProductCatalog.categoryIdForProduct("iPhone"))
        val category = PrizeProductCatalog.resolveCategory(
            productName = "iPhone",
            stockCategoryId = "mythic",
            categories = categories + PrizeCategory("legendary", "Lendário", 0xFFD69E2E, weight = 3),
        )
        assertEquals("legendary", category.id)
        assertEquals("Lendário", category.label)
    }

    @Test
    fun `resolveCategory falls back to stock category when product unknown`() {
        val category = PrizeProductCatalog.resolveCategory(
            productName = "Produto Desconhecido",
            stockCategoryId = "rare",
            categories = categories,
        )
        assertEquals("rare", category.id)
        assertEquals("Raro", category.label)
    }
}
