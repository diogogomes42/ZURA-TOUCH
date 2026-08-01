package com.zuratouch.prizewheel.domain

data class PrizeCategory(
    val id: String,
    val label: String,
    val color: Long,
    val weight: Int = 100,
    val icon: String = "🎁",
)

data class ProductSlot(
    val id: String,
    val categoryId: String,
    val productName: String,
    val shelf: Int,
    val slot: Int,
    val vmcLane: Int,
    val quantity: Int,
) {
    val isAvailable: Boolean get() = quantity > 0
}

enum class SaleState {
    IDLE, PAYMENT_PENDING, SPINNING, DISPENSING, REVEALING_PRIZE, ERROR
}

enum class SaleLogResult {
    SUCCESS, PAYMENT_FAILED, DISPENSE_FAILED, REFUNDED,
}

data class SaleLogEntry(
    val id: Long,
    val timestampMs: Long,
    val categoryLabel: String,
    val productName: String?,
    val vmcLane: Int,
    val result: SaleLogResult,
    val message: String?,
)

sealed interface DispenseResult {
    data object Delivered : DispenseResult
    data class Failed(val reason: String) : DispenseResult
}

data class SaleSession(
    val category: PrizeCategory,
    val productSlot: ProductSlot,
)
