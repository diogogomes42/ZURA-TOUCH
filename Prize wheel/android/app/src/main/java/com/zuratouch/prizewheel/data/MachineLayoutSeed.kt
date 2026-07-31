package com.zuratouch.prizewheel.data

import com.zuratouch.prizewheel.data.local.CategoryEntity
import com.zuratouch.prizewheel.data.local.SpiralEntity
import com.zuratouch.prizewheel.domain.QueuedProduct
import kotlin.random.Random

/** 8 layers × 3 slots test layout for a standard spiral vending machine. */
object MachineLayoutSeed {
    const val SHELVES = 8
    const val SLOTS_PER_SHELF = 3

    enum class SpiralSize(val capacity: Int, val label: String) {
        SMALL(5, "S"),
        MEDIUM(10, "M"),
        LARGE(15, "L"),
    }

    fun vmcLaneFor(shelf: Int, slot: Int): Int = (shelf - 1) * SLOTS_PER_SHELF + slot

    fun slotId(shelf: Int, slot: Int): String = "L${shelf}S$slot"

    fun defaultCategories(): List<CategoryEntity> = listOf(
        CategoryEntity(PrizeCatalog.COMMON, "Comum", 0xFF718096, 50, "⚪"),
        CategoryEntity(PrizeCatalog.RARE, "Raro", 0xFF3182CE, 25, "🔵"),
        CategoryEntity(PrizeCatalog.EPIC, "Épico", 0xFF805AD5, 15, "🟣"),
        CategoryEntity(PrizeCatalog.LEGENDARY, "Lendário", 0xFFD69E2E, 7, "🟡"),
        CategoryEntity(PrizeCatalog.MYTHIC, "Mítico", 0xFFE53E3E, 3, "🔴"),
    )

    /** All 24 physical spirals — empty queues until the operator fills them. */
    fun allPhysicalSpirals(): List<SpiralEntity> =
        (1..SHELVES).flatMap { shelf ->
            (1..SLOTS_PER_SHELF).map { slot ->
                SpiralEntity(
                    id = slotId(shelf, slot),
                    shelf = shelf,
                    slot = slot,
                    vmcLane = vmcLaneFor(shelf, slot),
                    maxCapacity = spiralSizeFor(shelf, slot).capacity,
                    queueJson = "[]",
                )
            }
        }

    /**
     * Fills all 24 spirals with catalog prizes, mixed across categories.
     * Products are distributed round-robin so every layer gets stock — large spirals
     * at the top are not allowed to drain the pool before lower layers receive any.
     */
    fun testSpirals(): List<SpiralEntity> {
        val layout = testSpiralLayout()
        val fillTarget = layout.associate { (shelf, slot, size) ->
            slotId(shelf, slot) to minOf(size.capacity, 6)
        }
        val totalItems = fillTarget.values.sum()
        val pool = PrizeCatalog.testMachinePool(totalItems).shuffled(Random(42)).toMutableList()
        val queues = layout.associate { (shelf, slot, _) ->
            slotId(shelf, slot) to mutableListOf<QueuedProduct>()
        }

        var progress = true
        while (progress && pool.isNotEmpty()) {
            progress = false
            for ((shelf, slot, _) in layout) {
                val id = slotId(shelf, slot)
                val queue = queues.getValue(id)
                if (queue.size < fillTarget.getValue(id) && pool.isNotEmpty()) {
                    queue.add(pool.removeAt(0))
                    progress = true
                }
            }
        }

        return layout.map { (shelf, slot, size) ->
            spiral(shelf, slot, size, queues.getValue(slotId(shelf, slot)))
        }
    }

    /** All 8×3 positions with varied spiral sizes. */
    private fun testSpiralLayout(): List<Triple<Int, Int, SpiralSize>> =
        (1..SHELVES).flatMap { shelf ->
            (1..SLOTS_PER_SHELF).map { slot ->
                Triple(shelf, slot, spiralSizeFor(shelf, slot))
            }
        }

    private fun spiralSizeFor(shelf: Int, slot: Int): SpiralSize = when {
        shelf == 1 || shelf == 4 || (shelf == 7 && slot == 3) -> when (slot) {
            3 -> SpiralSize.MEDIUM
            else -> SpiralSize.LARGE
        }
        slot == 3 -> SpiralSize.SMALL
        else -> SpiralSize.MEDIUM
    }

    private fun spiral(
        shelf: Int,
        slot: Int,
        size: SpiralSize,
        queue: List<QueuedProduct>,
    ): SpiralEntity {
        require(queue.size <= size.capacity) {
            "${slotId(shelf, slot)}: fila ${queue.size} excede capacidade ${size.capacity}"
        }
        return SpiralEntity(
            id = slotId(shelf, slot),
            shelf = shelf,
            slot = slot,
            vmcLane = vmcLaneFor(shelf, slot),
            maxCapacity = size.capacity,
            queueJson = SpiralQueueCodec.encode(queue),
        )
    }
}
