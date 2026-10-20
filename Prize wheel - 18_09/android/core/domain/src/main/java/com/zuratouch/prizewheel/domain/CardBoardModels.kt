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
    /** Real dispense mapping — only the target slot is used when awarding the prize. */
    val dispensePlan: DispensePlan,
    /** Product shown when the card is revealed (full catalog decoy or actual win on target). */
    val displayProduct: QueuedProduct,
    val category: PrizeCategory,
    /** Exactly one slot per board carries the weighted premium/target outcome. */
    val isTargetSlot: Boolean,
) {
    fun faceProduct(selectedIndex: Int?, winningProduct: QueuedProduct): QueuedProduct =
        if (index == selectedIndex) winningProduct else displayProduct

    fun faceCategory(selectedIndex: Int?, winningCategory: PrizeCategory): PrizeCategory =
        if (index == selectedIndex) winningCategory else category
}

data class CardBoard(
    val slots: List<CardSlot>,
    val generatedAtMs: Long,
    val boardSeed: Long,
) {
    init {
        require(slots.isNotEmpty()) { "CardBoard must have at least one slot" }
    }
}

/** Paid or trial round state — persisted across process death until the round completes. */
data class CardGameSession(
    val board: CardBoard,
    val paymentConfirmed: Boolean,
    val selectedSlot: CardSlot? = null,
    val isTrialRound: Boolean = false,
)
