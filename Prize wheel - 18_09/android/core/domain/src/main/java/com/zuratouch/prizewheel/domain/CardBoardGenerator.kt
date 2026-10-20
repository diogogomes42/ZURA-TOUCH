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
            val winCategory = PrizeProductCatalog.resolveCategory(
                productName = targetPlan.product.productName,
                stockCategoryId = targetPlan.product.categoryId,
                categories = categories,
            ).let { resolved ->
                resolved.copy(
                    weight = categoryById[resolved.id]?.weight ?: targetCategory.weight,
                    icon = categoryById[resolved.id]?.icon ?: targetCategory.icon,
                )
            }
            unshuffled += CardSlot(
                index = -1,
                dispensePlan = targetPlan,
                displayProduct = targetPlan.product,
                category = winCategory,
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
    ): PrizeCategory = PrizeProductCatalog.resolveCategory(
        productName = product.productName,
        stockCategoryId = product.categoryId,
        categories = categoryById.values.toList(),
    ).let { resolved ->
        resolved.copy(
            weight = categoryById[resolved.id]?.weight ?: fallback.weight,
            icon = categoryById[resolved.id]?.icon ?: fallback.icon,
        )
    }
}
