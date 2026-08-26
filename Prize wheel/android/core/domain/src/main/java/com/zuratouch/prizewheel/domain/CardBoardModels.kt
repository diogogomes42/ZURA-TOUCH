package com.zuratouch.prizewheel.domain

data class CardBoardRules(
    val cardCount: Int = 24,
    val targetSlotCount: Int = 1,
) {
    init {
        require(cardCount > 0) { "cardCount must be positive" }
        require(targetSlotCount in 1..cardCount) { "targetSlotCount must be between 1 and cardCount" }
    }
}

data class CardSlot(
    /** Position 0..23 after shuffle — the index the customer sees. */
    val index: Int,
    val dispensePlan: DispensePlan,
    val category: PrizeCategory,
    /** Exactly one slot per board carries the weighted premium/target outcome. */
    val isTargetSlot: Boolean,
)

data class CardBoard(
    val slots: List<CardSlot>,
    val generatedAtMs: Long,
    val boardSeed: Long,
) {
    init {
        require(slots.isNotEmpty()) { "CardBoard must have at least one slot" }
    }
}

/** Paid round state — persisted across process death until the round completes. */
data class CardGameSession(
    val board: CardBoard,
    val paymentConfirmed: Boolean,
    val selectedSlot: CardSlot? = null,
)
