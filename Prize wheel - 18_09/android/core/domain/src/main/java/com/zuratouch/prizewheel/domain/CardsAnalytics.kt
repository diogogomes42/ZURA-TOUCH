package com.zuratouch.prizewheel.domain

/**
 * Local analytics for Cards mode — derived from [SaleLogEntry.message] audit fields.
 * Expected random target pick rate = 1 / [CardBoardRules.cardCount].
 */
data class CardsAnalyticsSummary(
    val completedRounds: Int,
    val targetSlotPicks: Int,
    val observedTargetRate: Double?,
    val expectedRandomRate: Double,
) {
    val deltaFromRandom: Double?
        get() = observedTargetRate?.minus(expectedRandomRate)
}

object CardsAnalytics {
    private const val EXPERIENCE_MARKER = "experience=CARDS"
    private const val TARGET_TRUE = "wasTargetSlot=true"
    private const val TARGET_FALSE = "wasTargetSlot=false"

    fun summarize(
        sales: List<SaleLogEntry>,
        cardCount: Int = CardBoardRules().cardCount,
    ): CardsAnalyticsSummary {
        val completed = sales.filter { sale ->
            sale.result == SaleLogResult.SUCCESS &&
                sale.message?.contains(EXPERIENCE_MARKER) == true
        }
        val targetPicks = completed.count { it.message?.contains(TARGET_TRUE) == true }
        val observed = completed.takeIf { it.isNotEmpty() }?.let { targetPicks.toDouble() / it.size }
        return CardsAnalyticsSummary(
            completedRounds = completed.size,
            targetSlotPicks = targetPicks,
            observedTargetRate = observed,
            expectedRandomRate = 1.0 / cardCount,
        )
    }

    fun parseWasTargetSlot(message: String?): Boolean? = when {
        message == null -> null
        message.contains(TARGET_TRUE) -> true
        message.contains(TARGET_FALSE) -> false
        else -> null
    }
}
