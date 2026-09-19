package com.zuratouch.prizewheel.domain

/**
 * Central configuration for play tiers (price + category probabilities).
 * Customer UI reads [PrizeTierDefinition.name] and [PrizeTierDefinition.priceCents] only.
 */
data class PrizeTierDefinition(
    val id: String,
    val name: String,
    val priceCents: Long,
    /** categoryId → weight in basis points (10_000 = 100%). */
    val categoryWeights: Map<String, Int>,
)

/** Internal company costs per prize category — never shown in customer UI. */
object PrizeCategoryEconomics {
    val internalCostCents: Map<String, Long> = mapOf(
        PrizeProductCatalog.BASIC to 60L,
        PrizeProductCatalog.COMMON to 200L,
        PrizeProductCatalog.RARE to 600L,
        PrizeProductCatalog.EPIC to 1_500L,
        PrizeProductCatalog.MYTHIC to 2_500L,
        PrizeProductCatalog.LEGENDARY to 70_000L,
    )
}

object PrizeTierCatalog {
    const val BASIS_POINTS_TOTAL = 10_000

    const val LITE = "lite"
    const val PLUS = "plus"
    const val PRO = "pro"
    const val ULTRA = "ultra"
    const val PRIME = "prime"

    private val allTiers: List<PrizeTierDefinition> = listOf(
        PrizeTierDefinition(
            id = LITE,
            name = "LITE",
            priceCents = 250L,
            categoryWeights = mapOf(
                PrizeProductCatalog.BASIC to 9_400,
                PrizeProductCatalog.COMMON to 400,
                PrizeProductCatalog.RARE to 150,
                PrizeProductCatalog.EPIC to 40,
                PrizeProductCatalog.MYTHIC to 9,
                PrizeProductCatalog.LEGENDARY to 1,
            ),
        ),
        PrizeTierDefinition(
            id = PLUS,
            name = "PLUS",
            priceCents = 650L,
            categoryWeights = mapOf(
                PrizeProductCatalog.COMMON to 9_100,
                PrizeProductCatalog.RARE to 700,
                PrizeProductCatalog.EPIC to 150,
                PrizeProductCatalog.MYTHIC to 49,
                PrizeProductCatalog.LEGENDARY to 1,
            ),
        ),
        PrizeTierDefinition(
            id = PRO,
            name = "PRO",
            priceCents = 1_400L,
            categoryWeights = mapOf(
                PrizeProductCatalog.RARE to 9_099,
                PrizeProductCatalog.EPIC to 800,
                PrizeProductCatalog.MYTHIC to 100,
                PrizeProductCatalog.LEGENDARY to 1,
            ),
        ),
        PrizeTierDefinition(
            id = ULTRA,
            name = "ULTRA",
            priceCents = 3_000L,
            categoryWeights = mapOf(
                PrizeProductCatalog.EPIC to 8_800,
                PrizeProductCatalog.MYTHIC to 1_100,
                PrizeProductCatalog.LEGENDARY to 100,
            ),
        ),
        PrizeTierDefinition(
            id = PRIME,
            name = "PRIME",
            priceCents = 5_000L,
            categoryWeights = mapOf(
                PrizeProductCatalog.MYTHIC to 9_400,
                PrizeProductCatalog.LEGENDARY to 600,
            ),
        ),
    )

    val tiers: List<PrizeTierDefinition> = allTiers

    fun tierById(id: String): PrizeTierDefinition? = allTiers.find { it.id == id }

    fun tierByIdOrThrow(id: String): PrizeTierDefinition =
        tierById(id) ?: error("Unknown play tier: $id")

    fun allowedCategoryIds(tier: PrizeTierDefinition): Set<String> =
        tier.categoryWeights.filterValues { it > 0 }.keys
}
