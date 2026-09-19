package com.zuratouch.prizewheel.data

import com.zuratouch.prizewheel.domain.PrizeProductCatalog
import com.zuratouch.prizewheel.domain.QueuedProduct
import kotlin.random.Random

/** Official prize catalog — product names per wheel category. */
object PrizeCatalog {
    const val BASIC = PrizeProductCatalog.BASIC
    const val COMMON = PrizeProductCatalog.COMMON
    const val RARE = PrizeProductCatalog.RARE
    const val EPIC = PrizeProductCatalog.EPIC
    const val LEGENDARY = PrizeProductCatalog.LEGENDARY
    const val MYTHIC = PrizeProductCatalog.MYTHIC

    val categoryWeights = listOf(
        BASIC to 60,
        COMMON to 50,
        RARE to 25,
        EPIC to 15,
        LEGENDARY to 3,
        MYTHIC to 7,
    )

    val commonProducts = PrizeProductCatalog.commonProducts
    val rareProducts = PrizeProductCatalog.rareProducts
    val epicProducts = PrizeProductCatalog.epicProducts
    val legendaryProducts = PrizeProductCatalog.legendaryProducts
    val mythicProducts = PrizeProductCatalog.mythicProducts

    fun productsFor(categoryId: String): List<String> = PrizeProductCatalog.productsFor(categoryId)

    /**
     * Builds [count] units for the test machine, spread across categories using the same
     * weights as the wheel (50 / 25 / 15 / 7 / 3) — Mítico less rare than Lendário.
     */
    fun testMachinePool(count: Int, random: Random = Random(42)): List<QueuedProduct> {
        val totalWeight = categoryWeights.sumOf { it.second }
        return List(count) {
            var roll = random.nextInt(totalWeight)
            val categoryId = categoryWeights.first { (_, weight) ->
                roll -= weight
                roll < 0
            }.first
            val names = productsFor(categoryId)
            QueuedProduct(categoryId, names[random.nextInt(names.size)])
        }.shuffled(random)
    }
}
