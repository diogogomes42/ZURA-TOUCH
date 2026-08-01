package com.zuratouch.prizewheel.domain

import kotlin.random.Random

/** Business rules only: no UI, payment, or hardware dependencies. */
class PrizeWheelEngine(private val random: Random = Random.Default) {
    fun availableCategories(categories: List<PrizeCategory>, stock: List<ProductSlot>): List<PrizeCategory> =
        categories.filter { category -> stock.any { it.categoryId == category.id && it.isAvailable } }

    fun createSale(categories: List<PrizeCategory>, stock: List<ProductSlot>): SaleSession? {
        val available = availableCategories(categories, stock)
        val category = pickWeightedCategory(available) ?: return null
        val product = slotsForCategory(stock, category.id).firstOrNull() ?: return null
        return SaleSession(category, product)
    }

    fun slotsForCategory(stock: List<ProductSlot>, categoryId: String): List<ProductSlot> =
        stock.filter { it.categoryId == categoryId && it.isAvailable }
            .sortedWith(compareBy({ it.shelf }, { it.slot }))

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

    fun registerSuccessfulDelivery(stock: List<ProductSlot>, deliveredSlotId: String): List<ProductSlot> =
        stock.map { slot ->
            if (slot.id == deliveredSlotId && slot.quantity > 0) slot.copy(quantity = slot.quantity - 1) else slot
        }
}
