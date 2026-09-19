package com.zuratouch.prizewheel.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class TierPrizeSelectorTest {
    private val engine = PrizeEngine()
    private val selector = TierPrizeSelector(engine)

    private val allCategories = listOf(
        PrizeCategory(PrizeProductCatalog.BASIC, "Básico", 0xFFA0AEC0),
        PrizeCategory(PrizeProductCatalog.COMMON, "Comum", 0xFF718096),
        PrizeCategory(PrizeProductCatalog.RARE, "Raro", 0xFF3182CE),
        PrizeCategory(PrizeProductCatalog.EPIC, "Épico", 0xFF805AD5),
        PrizeCategory(PrizeProductCatalog.MYTHIC, "Mítico", 0xFFE53E3E),
        PrizeCategory(PrizeProductCatalog.LEGENDARY, "Lendário", 0xFFD69E2E),
    )

    private fun spiral(id: String, categoryId: String, product: String = "Item") =
        Spiral(id, 1, 1, 1, 10, listOf(QueuedProduct(categoryId, product)))

    private fun stockFor(vararg pairs: Pair<String, String>) =
        pairs.mapIndexed { index, (categoryId, product) ->
            spiral("S$index", categoryId, product)
        }

    @Test
    fun `all tier probabilities sum to one hundred percent`() {
        PrizeTierCatalog.tiers.forEach { tier ->
            val total = tier.categoryWeights.values.sum()
            assertEquals(
                "Tier ${tier.id} weights must sum to ${PrizeTierCatalog.BASIS_POINTS_TOTAL}",
                PrizeTierCatalog.BASIS_POINTS_TOTAL,
                total,
            )
        }
    }

    @Test
    fun `LITE allows all categories with defined weights`() {
        val tier = PrizeTierCatalog.tierByIdOrThrow(PrizeTierCatalog.LITE)
        val allowed = PrizeTierCatalog.allowedCategoryIds(tier)
        assertTrue(PrizeProductCatalog.BASIC in allowed)
        assertTrue(PrizeProductCatalog.LEGENDARY in allowed)
        assertEquals(6, allowed.size)
    }

    @Test
    fun `PLUS does not allow Basic`() {
        val tier = PrizeTierCatalog.tierByIdOrThrow(PrizeTierCatalog.PLUS)
        assertFalse(PrizeProductCatalog.BASIC in PrizeTierCatalog.allowedCategoryIds(tier))
    }

    @Test
    fun `PRO does not allow Basic or Common`() {
        val tier = PrizeTierCatalog.tierByIdOrThrow(PrizeTierCatalog.PRO)
        val allowed = PrizeTierCatalog.allowedCategoryIds(tier)
        assertFalse(PrizeProductCatalog.BASIC in allowed)
        assertFalse(PrizeProductCatalog.COMMON in allowed)
        assertTrue(PrizeProductCatalog.RARE in allowed)
        assertTrue(PrizeProductCatalog.LEGENDARY in allowed)
    }

    @Test
    fun `ULTRA does not allow Basic Common or Rare`() {
        val tier = PrizeTierCatalog.tierByIdOrThrow(PrizeTierCatalog.ULTRA)
        val allowed = PrizeTierCatalog.allowedCategoryIds(tier)
        assertFalse(PrizeProductCatalog.BASIC in allowed)
        assertFalse(PrizeProductCatalog.COMMON in allowed)
        assertFalse(PrizeProductCatalog.RARE in allowed)
        assertTrue(PrizeProductCatalog.EPIC in allowed)
    }

    @Test
    fun `PRIME only allows Mythic and Legendary`() {
        val tier = PrizeTierCatalog.tierByIdOrThrow(PrizeTierCatalog.PRIME)
        assertEquals(
            setOf(PrizeProductCatalog.MYTHIC, PrizeProductCatalog.LEGENDARY),
            PrizeTierCatalog.allowedCategoryIds(tier),
        )
    }

    @Test
    fun `legendary normal draw works when probability selects it`() {
        val tier = PrizeTierCatalog.tierByIdOrThrow(PrizeTierCatalog.ULTRA)
        val stock = stockFor(PrizeProductCatalog.LEGENDARY to "iPhone")
        repeat(20) {
            val categoryId = selector.selectCategory(tier, allCategories, stock, JackpotConfig.DEFAULT)
            assertEquals(PrizeProductCatalog.LEGENDARY, categoryId)
        }
    }

    @Test
    fun `guarantees legendary when threshold reached and legendary is in stock`() {
        val tier = PrizeTierCatalog.tierByIdOrThrow(PrizeTierCatalog.PRO)
        val stock = stockFor(
            PrizeProductCatalog.RARE to "Garrafa",
            PrizeProductCatalog.LEGENDARY to "iPhone",
        )
        val jackpot = JackpotState(
            accumulatedRevenueCents = JackpotConfig.LEGENDARY_GUARANTEE_THRESHOLD_CENTS,
            enabled = true,
        )
        val categoryId = selector.selectCategory(tier, allCategories, stock, jackpot)
        assertEquals(PrizeProductCatalog.LEGENDARY, categoryId)
    }

    @Test
    fun `resets jackpot counter after legendary win`() {
        val updated = selector.jackpotAfterSuccessfulPlay(
            current = JackpotState(accumulatedRevenueCents = 500_000L, enabled = true),
            tierPriceCents = 5_000L,
            wonCategoryId = PrizeProductCatalog.LEGENDARY,
        )
        assertEquals(0L, updated.accumulatedRevenueCents)
    }

    @Test
    fun `accumulates revenue after non legendary win`() {
        val updated = selector.jackpotAfterSuccessfulPlay(
            current = JackpotState(accumulatedRevenueCents = 1_000L, enabled = true),
            tierPriceCents = 250L,
            wonCategoryId = PrizeProductCatalog.BASIC,
        )
        assertEquals(1_250L, updated.accumulatedRevenueCents)
    }

    @Test
    fun `canPlayTier is false when tier categories are not at front`() {
        val tier = PrizeTierCatalog.tierByIdOrThrow(PrizeTierCatalog.PRO)
        val stock = stockFor(PrizeProductCatalog.BASIC to "Adesivo")
        assertFalse(selector.canPlayTier(tier, allCategories, stock, JackpotConfig.DEFAULT))
    }

    @Test
    fun `canPlayTier is true when tier has dispensable categories`() {
        val tier = PrizeTierCatalog.tierByIdOrThrow(PrizeTierCatalog.LITE)
        val stock = stockFor(PrizeProductCatalog.BASIC to "Adesivo")
        assertTrue(selector.canPlayTier(tier, allCategories, stock, JackpotConfig.DEFAULT))
    }

    @Test
    fun `out of stock categories cannot be selected`() {
        val tier = PrizeTierCatalog.tierByIdOrThrow(PrizeTierCatalog.LITE)
        val stock = stockFor(PrizeProductCatalog.COMMON to "Boné")
        repeat(100) {
            val categoryId = selector.selectCategory(tier, allCategories, stock, JackpotConfig.DEFAULT)
            assertEquals(PrizeProductCatalog.COMMON, categoryId)
        }
    }

    @Test
    fun `redistributes weights when category is out of stock`() {
        val tier = PrizeTierCatalog.tierByIdOrThrow(PrizeTierCatalog.PRO)
        val stock = stockFor(
            PrizeProductCatalog.EPIC to "Funko",
            PrizeProductCatalog.MYTHIC to "AirPods",
        )
        repeat(100) {
            val categoryId = selector.selectCategory(tier, allCategories, stock, JackpotConfig.DEFAULT)
            assertTrue(categoryId == PrizeProductCatalog.EPIC || categoryId == PrizeProductCatalog.MYTHIC)
        }
    }

    @Test
    fun `createTierSale returns null when nothing is dispensable`() {
        val tier = PrizeTierCatalog.tierByIdOrThrow(PrizeTierCatalog.LITE)
        val stock = listOf(
            Spiral(
                "1",
                1,
                1,
                1,
                10,
                listOf(
                    QueuedProduct("unknown", "Placeholder"),
                    QueuedProduct(PrizeProductCatalog.BASIC, "Adesivo"),
                ),
            ),
        )
        assertNull(engine.createTierSale(tier, allCategories, stock, JackpotConfig.DEFAULT))
    }

    @Test
    fun `jackpot guarantee threshold is six thousand euros in cents`() {
        assertEquals(600_000L, JackpotConfig.LEGENDARY_GUARANTEE_THRESHOLD_CENTS)
    }
}
