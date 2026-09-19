package com.zuratouch.prizewheel.domain

/** Customer-safe copy for tier info — no probabilities, costs or internal thresholds. */
data class PrizeTierInfoEntry(
    val tier: PrizeTierDefinition,
    val headline: String,
    val prizeCategoriesLabel: String,
)

object PrizeTierCustomerInfo {
    fun entryFor(tier: PrizeTierDefinition): PrizeTierInfoEntry =
        PrizeTierInfoEntry(
            tier = tier,
            headline = headlineFor(tier.id),
            prizeCategoriesLabel = formatPrizeCategories(tier),
        )

    fun entriesFor(tiers: List<PrizeTierDefinition>): List<PrizeTierInfoEntry> =
        tiers.map(::entryFor)

    fun progressionIndex(tierId: String): Int =
        PrizeTierCatalog.tiers.indexOfFirst { it.id == tierId }.coerceAtLeast(0)

    fun badgeEmojiFor(tierId: String): String = when (tierId) {
        PrizeTierCatalog.LITE -> "🎁"
        PrizeTierCatalog.PLUS -> "⭐"
        PrizeTierCatalog.PRO -> "💎"
        PrizeTierCatalog.ULTRA -> "🔥"
        PrizeTierCatalog.PRIME -> "👑"
        else -> "🎁"
    }

    fun categoryIdsFor(tier: PrizeTierDefinition): List<String> =
        PrizeTierCatalog.allowedCategoryIds(tier)
            .sortedBy { categoryId ->
                PrizeProductCatalog.rarityOrder.indexOf(categoryId).let { index ->
                    if (index < 0) Int.MAX_VALUE else index
                }
            }

    fun formatPrizeCategories(tier: PrizeTierDefinition): String =
        categoryIdsFor(tier).joinToString(" · ") { PrizeProductCatalog.catalogLabelFor(it) }

    internal fun headlineFor(tierId: String): String = when (tierId) {
        PrizeTierCatalog.LITE ->
            "Exemplos: 🍫 pastilhas, 🧢 bonés, 🦆 patos de borracha e 🧸 peluches"
        PrizeTierCatalog.PLUS ->
            "Exemplos: 🧸 peluches, 🎵 bilhetes para concerto, 🍽️ jantares e 🎁 cartões presente"
        PrizeTierCatalog.PRO ->
            "Exemplos: 🍽️ jantares, 🎵 bilhetes para concerto, 🏨 noites em hotel e 🎢 experiências de aventura"
        PrizeTierCatalog.ULTRA ->
            "Exemplos: 🏨 noites em hotel, 🍽️ jantares, 🎢 experiências de aventura e 🎁 cartões presente"
        PrizeTierCatalog.PRIME ->
            "Exemplos: 📱 iPhone, ✈️ viagens, 🏨 noites em hotel e 🎢 experiências de aventura"
        else -> "Escolha o valor que prefere para jogar."
    }
}
