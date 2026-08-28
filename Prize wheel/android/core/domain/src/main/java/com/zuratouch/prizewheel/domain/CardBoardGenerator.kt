package com.zuratouch.prizewheel.domain

import kotlin.random.Random

/**
 * Builds a shuffled [cardCount]-card board.
 * Faces show the full [PrizeProductCatalog] shuffled; exactly one target slot maps to a real
 * [DispensePlan] drawn from current machine stock.
 */
class CardBoardGenerator(
    private val prizeEngine: PrizeEngine,
    private val random: Random = Random.Default,
) {
    fun canGenerateBoard(categories: List<PrizeCategory>, stock: List<Spiral>): Boolean {
        if (prizeEngine.dispensableCategories(categories, stock).isEmpty()) return false
        return deliverablePlans(stock).isNotEmpty()
    }

    fun generate(
        categories: List<PrizeCategory>,
        stock: List<Spiral>,
        rules: CardBoardRules = CardBoardRules(),
        generatedAtMs: Long = System.currentTimeMillis(),
        random: Random = this.random,
    ): CardBoard? {
        if (!canGenerateBoard(categories, stock)) return null

        val dispensable = prizeEngine.dispensableCategories(categories, stock)
        val targetCategory = prizeEngine.pickWeightedCategory(dispensable) ?: return null
        val targetPlan = prizeEngine.planDispense(stock, targetCategory.id) ?: return null

        val pool = deliverablePlans(stock)
        if (pool.isEmpty()) return null

        val categoryById = categories.associateBy { it.id }
        val fallbackPlan = pool[random.nextInt(pool.size)]
        val decoyProducts = buildDecoyProducts(rules.cardCount - rules.targetSlotCount, random)
        val unshuffled = mutableListOf<CardSlot>()

        decoyProducts.forEach { displayProduct ->
            val category = displayCategoryFor(displayProduct, categoryById, targetCategory)
            unshuffled += CardSlot(
                index = -1,
                dispensePlan = fallbackPlan,
                displayProduct = displayProduct,
                category = category,
                isTargetSlot = false,
            )
        }

        repeat(rules.targetSlotCount) {
            unshuffled += CardSlot(
                index = -1,
                dispensePlan = targetPlan,
                displayProduct = targetPlan.product,
                category = targetCategory,
                isTargetSlot = true,
            )
        }

        val slots = unshuffled
            .shuffled(random)
            .mapIndexed { index, slot -> slot.copy(index = index) }

        return CardBoard(
            slots = slots,
            generatedAtMs = generatedAtMs,
            boardSeed = random.nextLong(),
        )
    }

    private fun buildDecoyProducts(count: Int, random: Random): List<QueuedProduct> {
        if (count <= 0) return emptyList()
        val catalog = PrizeProductCatalog.allProducts().shuffled(random)
        return List(count) { index ->
            catalog[index % catalog.size]
        }.shuffled(random)
    }

    private fun deliverablePlans(stock: List<Spiral>): List<DispensePlan> =
        stock.mapNotNull { spiral ->
            val product = spiral.frontProduct ?: return@mapNotNull null
            DispensePlan(spiral, product)
        }

    private fun displayCategoryFor(
        product: QueuedProduct,
        categoryById: Map<String, PrizeCategory>,
        fallback: PrizeCategory,
    ): PrizeCategory = categoryById[product.categoryId] ?: PrizeCategory(
        id = product.categoryId,
        label = catalogLabelFor(product.categoryId),
        color = catalogColorFor(product.categoryId),
        weight = fallback.weight,
        icon = fallback.icon,
    )

    private fun catalogLabelFor(categoryId: String): String = when (categoryId) {
        PrizeProductCatalog.COMMON -> "Comum"
        PrizeProductCatalog.RARE -> "Raro"
        PrizeProductCatalog.EPIC -> "Épico"
        PrizeProductCatalog.LEGENDARY -> "Lendário"
        PrizeProductCatalog.MYTHIC -> "Mítico"
        else -> categoryId
    }

    private fun catalogColorFor(categoryId: String): Long = when (categoryId) {
        PrizeProductCatalog.COMMON -> 0xFF718096
        PrizeProductCatalog.RARE -> 0xFF3182CE
        PrizeProductCatalog.EPIC -> 0xFF805AD5
        PrizeProductCatalog.LEGENDARY -> 0xFFD69E2E
        PrizeProductCatalog.MYTHIC -> 0xFFE53E3E
        else -> 0xFF718096
    }
}
