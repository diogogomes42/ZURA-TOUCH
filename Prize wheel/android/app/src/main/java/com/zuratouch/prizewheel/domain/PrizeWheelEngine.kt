package com.zuratouch.prizewheel.domain

import kotlin.random.Random

/** Business rules only: no UI, payment, or hardware dependencies. */
class PrizeWheelEngine(private val random: Random = Random.Default) {
    /** Category appears on the wheel when at least one product of that category exists anywhere in the machine. */
    fun availableCategories(categories: List<PrizeCategory>, stock: List<Spiral>): List<PrizeCategory> =
        categories.filter { category -> stock.any { it.containsCategory(category.id) } }

    /** Categories that can actually be won — at least one matching product is at the front of a spiral. */
    fun dispensableCategories(categories: List<PrizeCategory>, stock: List<Spiral>): List<PrizeCategory> =
        categories.filter { category -> stock.any { it.frontMatchesCategory(category.id) } }

    fun createSale(categories: List<PrizeCategory>, stock: List<Spiral>): SaleSession? {
        val wheelCategories = availableCategories(categories, stock)
        if (wheelCategories.isEmpty()) return null
        val drawable = dispensableCategories(categories, stock)
        val category = pickWeightedCategory(drawable) ?: return null
        return SaleSession(category, wheelCategories)
    }

    /** Front matches for [categoryId], sorted by queue length (desc), then shelf/slot — for dispense + fallback. */
    fun spiralsForDispense(stock: List<Spiral>, categoryId: String): List<Spiral> =
        stock.filter { it.frontMatchesCategory(categoryId) }
            .sortedWith(
                compareByDescending<Spiral> { it.queueLength }
                    .thenBy { it.shelf }
                    .thenBy { it.slot },
            )

    fun pickSpiralForCategory(stock: List<Spiral>, categoryId: String): Spiral? =
        spiralsForDispense(stock, categoryId).firstOrNull()

    fun planDispense(stock: List<Spiral>, categoryId: String): DispensePlan? {
        val spiral = pickSpiralForCategory(stock, categoryId) ?: return null
        val product = spiral.frontProduct ?: return null
        return DispensePlan(spiral, product)
    }

    fun pickWeightedCategory(available: List<PrizeCategory>): PrizeCategory? {
        if (available.isEmpty()) return null
        val totalWeight = available.sumOf { it.weight.coerceAtLeast(1) }
        var roll = random.nextInt(totalWeight)
        for (category in available) {
            roll -= category.weight.coerceAtLeast(1)
            if (roll < 0) return category
        }
        return available.last()
    }

    fun registerSuccessfulDelivery(stock: List<Spiral>, deliveredSpiralId: String): List<Spiral> =
        stock.map { spiral ->
            if (spiral.id == deliveredSpiralId && spiral.queue.isNotEmpty()) {
                spiral.copy(queue = spiral.queue.drop(1))
            } else {
                spiral
            }
        }

    fun countProductsInCategory(stock: List<Spiral>, categoryId: String): Int =
        stock.sumOf { spiral -> spiral.queue.count { it.categoryId == categoryId } }
}
