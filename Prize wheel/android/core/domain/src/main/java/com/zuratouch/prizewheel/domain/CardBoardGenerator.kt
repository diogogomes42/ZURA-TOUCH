package com.zuratouch.prizewheel.domain

import kotlin.random.Random

/**
 * Builds a shuffled [cardCount]-card board where every slot maps to a real [DispensePlan].
 * Exactly [CardBoardRules.targetSlotCount] slot(s) carry the weighted premium/target outcome.
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
        val unshuffled = mutableListOf<CardSlot>()

        repeat(rules.cardCount - rules.targetSlotCount) {
            val plan = samplePlanFromPool(categories, pool, random) ?: return null
            val category = categoryById[plan.product.categoryId] ?: return null
            unshuffled += CardSlot(
                index = -1,
                dispensePlan = plan,
                category = category,
                isTargetSlot = false,
            )
        }

        repeat(rules.targetSlotCount) {
            unshuffled += CardSlot(
                index = -1,
                dispensePlan = targetPlan,
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

    private fun deliverablePlans(stock: List<Spiral>): List<DispensePlan> =
        stock.mapNotNull { spiral ->
            val product = spiral.frontProduct ?: return@mapNotNull null
            DispensePlan(spiral, product)
        }

    private fun samplePlanFromPool(
        categories: List<PrizeCategory>,
        pool: List<DispensePlan>,
        random: Random,
    ): DispensePlan? {
        val availableIds = pool.map { it.product.categoryId }.toSet()
        val weighted = categories.filter { it.id in availableIds }
        val picked = prizeEngine.pickWeightedCategory(weighted) ?: return null
        val matching = pool.filter { it.product.categoryId == picked.id }
        return matching[random.nextInt(matching.size)]
    }
}
