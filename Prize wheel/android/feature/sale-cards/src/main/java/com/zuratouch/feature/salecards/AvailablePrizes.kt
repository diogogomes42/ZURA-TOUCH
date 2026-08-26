package com.zuratouch.feature.salecards

import androidx.annotation.DrawableRes
import com.zuratouch.prizewheel.domain.PrizeCategory
import com.zuratouch.prizewheel.domain.Spiral
import kotlin.random.Random

data class AvailablePrizeDisplay(
    val productName: String,
    val categoryLabel: String,
    val categoryColor: Long,
    @DrawableRes val imageRes: Int? = null,
)

object AvailablePrizes {
    /** Prémios únicos presentes nas filas da máquina (stock real). */
    fun fromStock(
        categories: List<PrizeCategory>,
        stock: List<Spiral>,
        random: Random = Random.Default,
    ): List<AvailablePrizeDisplay> {
        val categoryById = categories.associateBy { it.id }
        return stock
            .asSequence()
            .flatMap { it.queue.asSequence() }
            .mapNotNull { product ->
                val category = categoryById[product.categoryId] ?: return@mapNotNull null
                AvailablePrizeDisplay(
                    productName = product.productName,
                    categoryLabel = category.label,
                    categoryColor = category.color,
                    imageRes = CardProductImages.drawableFor(product.productName),
                )
            }
            .distinctBy { it.productName }
            .shuffled(random)
            .toList()
    }
}
