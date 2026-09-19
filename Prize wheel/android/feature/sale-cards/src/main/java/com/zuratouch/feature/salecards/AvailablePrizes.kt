package com.zuratouch.feature.salecards

import androidx.annotation.DrawableRes
import com.zuratouch.prizewheel.domain.PrizeCategory
import com.zuratouch.prizewheel.domain.PrizeProductCatalog
import com.zuratouch.prizewheel.domain.Spiral
import kotlin.random.Random

data class AvailablePrizeDisplay(
    val productName: String,
    val categoryId: String,
    val categoryLabel: String,
    val categoryColor: Long,
    val categoryWeight: Int,
    @DrawableRes val imageRes: Int? = null,
)

object AvailablePrizes {
    const val MAX_SHOWCASE = 28

    /** Prémios únicos presentes nas filas da máquina (stock real). */
    fun fromStock(
        categories: List<PrizeCategory>,
        stock: List<Spiral>,
        random: Random = Random.Default,
    ): List<AvailablePrizeDisplay> {
        return stock
            .asSequence()
            .flatMap { it.queue.asSequence() }
            .mapNotNull { product ->
                val category = PrizeProductCatalog.resolveCategory(
                    productName = product.productName,
                    stockCategoryId = product.categoryId,
                    categories = categories,
                )
                AvailablePrizeDisplay(
                    productName = product.productName,
                    categoryId = category.id,
                    categoryLabel = category.label,
                    categoryColor = category.color,
                    categoryWeight = category.weight,
                    imageRes = CardProductImages.drawableFor(product.productName),
                )
            }
            .distinctBy { it.productName }
            .shuffled(random)
            .take(MAX_SHOWCASE)
            .toList()
    }
}
