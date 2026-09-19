package com.zuratouch.prizewheel.domain

import kotlin.random.Random

class TierPrizeSelector(
    private val engine: PrizeEngine,
    private val random: Random = Random.Default,
) {
    fun canPlayTier(
        tier: PrizeTierDefinition,
        categories: List<PrizeCategory>,
        stock: List<Spiral>,
        jackpot: JackpotState,
    ): Boolean {
        val dispensableIds = engine.dispensableCategories(categories, stock).map { it.id }.toSet()
        if (dispensableIds.isEmpty()) return false
        if (shouldGuaranteeLegendary(jackpot, dispensableIds)) {
            return PrizeProductCatalog.LEGENDARY in dispensableIds &&
                categories.any { it.id == PrizeProductCatalog.LEGENDARY }
        }
        val weights = CategoryProbabilityResolver.weightsForAvailableCategories(
            tierWeights = tier.categoryWeights,
            dispensableCategoryIds = dispensableIds,
        )
        return weights.values.sum() > 0
    }

    fun selectCategory(
        tier: PrizeTierDefinition,
        categories: List<PrizeCategory>,
        stock: List<Spiral>,
        jackpot: JackpotState,
    ): String? {
        val dispensableIds = engine.dispensableCategories(categories, stock).map { it.id }.toSet()
        if (dispensableIds.isEmpty()) return null

        val forceLegendary = shouldGuaranteeLegendary(jackpot, dispensableIds)
        if (forceLegendary) return PrizeProductCatalog.LEGENDARY

        val weights = CategoryProbabilityResolver.weightsForAvailableCategories(
            tierWeights = tier.categoryWeights,
            dispensableCategoryIds = dispensableIds,
        )
        return CategoryProbabilityResolver.pickWeightedCategory(weights, random)
    }

    fun shouldGuaranteeLegendary(
        jackpot: JackpotState,
        dispensableCategoryIds: Set<String>,
    ): Boolean =
        jackpot.enabled &&
            jackpot.accumulatedRevenueCents >= JackpotConfig.LEGENDARY_GUARANTEE_THRESHOLD_CENTS &&
            PrizeProductCatalog.LEGENDARY in dispensableCategoryIds

    fun jackpotAfterSuccessfulPlay(
        current: JackpotState,
        tierPriceCents: Long,
        wonCategoryId: String,
    ): JackpotState {
        if (!current.enabled) return current
        return if (wonCategoryId == PrizeProductCatalog.LEGENDARY) {
            current.copy(accumulatedRevenueCents = 0L)
        } else {
            current.copy(accumulatedRevenueCents = current.accumulatedRevenueCents + tierPriceCents)
        }
    }
}
