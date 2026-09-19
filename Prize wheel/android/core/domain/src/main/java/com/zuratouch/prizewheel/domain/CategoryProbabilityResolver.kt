package com.zuratouch.prizewheel.domain

import kotlin.random.Random

/** Redistributes tier weights when categories are out of stock. */
object CategoryProbabilityResolver {
    fun weightsForAvailableCategories(
        tierWeights: Map<String, Int>,
        dispensableCategoryIds: Set<String>,
    ): Map<String, Int> {
        if (dispensableCategoryIds.isEmpty()) return emptyMap()
        return tierWeights.filter { (categoryId, weight) ->
            categoryId in dispensableCategoryIds && weight > 0
        }
    }

    fun pickWeightedCategory(weights: Map<String, Int>, random: Random): String? {
        val total = weights.values.sum()
        if (total <= 0) return null
        var roll = random.nextInt(total)
        for ((categoryId, weight) in weights) {
            roll -= weight
            if (roll < 0) return categoryId
        }
        return weights.keys.lastOrNull()
    }
}
