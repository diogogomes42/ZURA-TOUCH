package com.zuratouch.prizewheel.domain

data class PrizeCategory(
    val id: String,
    val label: String,
    val color: Long,
    val weight: Int = 100,
    val icon: String = "🎁",
)

/** One product waiting in a spiral queue (FIFO). */
data class QueuedProduct(
    val categoryId: String,
    val productName: String,
)

/**
 * One physical spiral in the machine. [queue] is front-to-back; only [frontProduct] can be dispensed.
 * Different categories may appear in the same spiral.
 */
data class Spiral(
    val id: String,
    val shelf: Int,
    val slot: Int,
    val vmcLane: Int,
    val maxCapacity: Int,
    val queue: List<QueuedProduct>,
) {
    val frontProduct: QueuedProduct? get() = queue.firstOrNull()
    val queueLength: Int get() = queue.size
    val isEmpty: Boolean get() = queue.isEmpty()

    fun containsCategory(categoryId: String): Boolean = queue.any { it.categoryId == categoryId }

    fun frontMatchesCategory(categoryId: String): Boolean = frontProduct?.categoryId == categoryId
}

data class DispensePlan(
    val spiral: Spiral,
    val product: QueuedProduct,
)

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
    /** Categories frozen at sale creation so the wheel layout cannot drift during payment. */
    val wheelCategories: List<PrizeCategory>,
)
