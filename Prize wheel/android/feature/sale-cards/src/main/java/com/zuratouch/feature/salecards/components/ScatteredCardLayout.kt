package com.zuratouch.feature.salecards.components

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

data class ScatteredCardPlacement(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val rotationDegrees: Float,
)

object ScatteredCardLayout {
    private const val MAX_ATTEMPTS_PER_CARD = 120
    private const val MIN_CARD_WIDTH_PX = 44f

    fun compute(
        count: Int,
        areaWidthPx: Float,
        areaHeightPx: Float,
        gapPx: Float,
        aspect: Float = CardGridSpec.ASPECT,
        seed: Long,
    ): List<ScatteredCardPlacement> {
        if (count <= 0 || areaWidthPx <= 0f || areaHeightPx <= 0f) return emptyList()

        val random = Random(seed)
        val columns = CardGridSpec.SHOWCASE_COLUMNS
        val rows = CardGridSpec.SHOWCASE_ROWS.coerceAtLeast((count + columns - 1) / columns)
        val allowedOverlap = CardGridSpec.SHOWCASE_ALLOWED_OVERLAP_FRACTION

        var cardWidth = maxCardWidthForArea(
            areaWidthPx = areaWidthPx,
            areaHeightPx = areaHeightPx,
            gapPx = gapPx,
            aspect = aspect,
            columns = columns,
            rows = rows,
        )
        while (cardWidth >= MIN_CARD_WIDTH_PX) {
            val cardHeight = cardWidth / aspect
            val result = tryPlaceAll(
                count = count,
                areaWidthPx = areaWidthPx,
                areaHeightPx = areaHeightPx,
                cardWidth = cardWidth,
                cardHeight = cardHeight,
                allowedOverlapFraction = allowedOverlap,
                columns = columns,
                rows = rows,
                random = random,
            )
            if (result.size == count) return result
            cardWidth *= 0.97f
        }

        return strictGridLayout(
            count = count,
            areaWidthPx = areaWidthPx,
            areaHeightPx = areaHeightPx,
            gapPx = gapPx,
            aspect = aspect,
            columns = columns,
            rows = rows,
            allowedOverlapFraction = allowedOverlap,
            random = Random(seed),
        )
    }

    private fun tryPlaceAll(
        count: Int,
        areaWidthPx: Float,
        areaHeightPx: Float,
        cardWidth: Float,
        cardHeight: Float,
        allowedOverlapFraction: Float,
        columns: Int,
        rows: Int,
        random: Random,
    ): List<ScatteredCardPlacement> {
        val placed = mutableListOf<ScatteredCardPlacement>()
        for (index in 0 until count) {
            val placement = findPlacement(
                index = index,
                placed = placed,
                areaWidthPx = areaWidthPx,
                areaHeightPx = areaHeightPx,
                cardWidth = cardWidth,
                cardHeight = cardHeight,
                allowedOverlapFraction = allowedOverlapFraction,
                columns = columns,
                rows = rows,
                random = random,
            ) ?: return emptyList()
            placed += placement
        }
        return placed
    }

    private fun findPlacement(
        index: Int,
        placed: List<ScatteredCardPlacement>,
        areaWidthPx: Float,
        areaHeightPx: Float,
        cardWidth: Float,
        cardHeight: Float,
        allowedOverlapFraction: Float,
        columns: Int,
        rows: Int,
        random: Random,
    ): ScatteredCardPlacement? {
        repeat(MAX_ATTEMPTS_PER_CARD) {
            val rotation = random.nextFloat() * 10f - 5f
            val maxX = (areaWidthPx - cardWidth).coerceAtLeast(0f)
            val maxY = (areaHeightPx - cardHeight).coerceAtLeast(0f)
            val candidate = ScatteredCardPlacement(
                x = random.nextFloat() * maxX,
                y = random.nextFloat() * maxY,
                width = cardWidth,
                height = cardHeight,
                rotationDegrees = rotation,
            )
            if (placed.none { overlapsTooMuch(it, candidate, allowedOverlapFraction) }) return candidate
        }
        return jitteredGridSlot(
            index = index,
            placed = placed,
            areaWidthPx = areaWidthPx,
            areaHeightPx = areaHeightPx,
            cardWidth = cardWidth,
            cardHeight = cardHeight,
            allowedOverlapFraction = allowedOverlapFraction,
            columns = columns,
            rows = rows,
            random = random,
        )
    }

    private fun jitteredGridSlot(
        index: Int,
        placed: List<ScatteredCardPlacement>,
        areaWidthPx: Float,
        areaHeightPx: Float,
        cardWidth: Float,
        cardHeight: Float,
        allowedOverlapFraction: Float,
        columns: Int,
        rows: Int,
        random: Random,
    ): ScatteredCardPlacement? {
        val col = index % columns
        val row = index / columns
        val cellWidth = areaWidthPx / columns
        val cellHeight = areaHeightPx / rows
        repeat(40) {
            val jitterX = (random.nextFloat() - 0.5f) * cellWidth * 0.55f
            val jitterY = (random.nextFloat() - 0.5f) * cellHeight * 0.55f
            val x = (col * cellWidth + (cellWidth - cardWidth) / 2f + jitterX)
                .coerceIn(0f, (areaWidthPx - cardWidth).coerceAtLeast(0f))
            val y = (row * cellHeight + (cellHeight - cardHeight) / 2f + jitterY)
                .coerceIn(0f, (areaHeightPx - cardHeight).coerceAtLeast(0f))
            val candidate = ScatteredCardPlacement(
                x = x,
                y = y,
                width = cardWidth,
                height = cardHeight,
                rotationDegrees = random.nextFloat() * 8f - 4f,
            )
            if (placed.none { overlapsTooMuch(it, candidate, allowedOverlapFraction) }) return candidate
        }
        return null
    }

    private fun maxCardWidthForArea(
        areaWidthPx: Float,
        areaHeightPx: Float,
        gapPx: Float,
        aspect: Float,
        columns: Int,
        rows: Int,
    ): Float {
        val packingGap = gapPx * CardGridSpec.SHOWCASE_PACKING_GAP_FACTOR
        val maxWidth = (areaWidthPx - packingGap * (columns - 1)) / columns
        val maxHeight = (areaHeightPx - packingGap * (rows - 1)) / rows
        val widthFromHeight = maxHeight * aspect
        return min(maxWidth, widthFromHeight) * CardGridSpec.SHOWCASE_SCALE
    }

    private fun strictGridLayout(
        count: Int,
        areaWidthPx: Float,
        areaHeightPx: Float,
        gapPx: Float,
        aspect: Float,
        columns: Int,
        rows: Int,
        allowedOverlapFraction: Float,
        random: Random,
    ): List<ScatteredCardPlacement> {
        val packingGap = gapPx * CardGridSpec.SHOWCASE_PACKING_GAP_FACTOR
        val maxWidth = (areaWidthPx - packingGap * (columns - 1)) / columns
        val maxHeight = (areaHeightPx - packingGap * (rows - 1)) / rows
        val cardHeight = min(maxHeight, maxWidth / aspect) * CardGridSpec.SHOWCASE_SCALE
        val cardWidth = cardHeight * aspect
        val stepX = cardWidth * (1f - allowedOverlapFraction * 0.85f)
        val stepY = cardHeight * (1f - allowedOverlapFraction * 0.85f)
        val gridWidth = stepX * (columns - 1) + cardWidth
        val gridHeight = stepY * (rows - 1) + cardHeight
        val offsetX = ((areaWidthPx - gridWidth) / 2f).coerceAtLeast(0f)
        val offsetY = ((areaHeightPx - gridHeight) / 2f).coerceAtLeast(0f)

        val placed = mutableListOf<ScatteredCardPlacement>()
        for (index in 0 until count) {
            val col = index % columns
            val row = index / columns
            val baseX = offsetX + col * stepX
            val baseY = offsetY + row * stepY
            val jitterX = (random.nextFloat() - 0.5f) * stepX * 0.35f
            val jitterY = (random.nextFloat() - 0.5f) * stepY * 0.35f
            val candidate = ScatteredCardPlacement(
                x = (baseX + jitterX).coerceIn(0f, (areaWidthPx - cardWidth).coerceAtLeast(0f)),
                y = (baseY + jitterY).coerceIn(0f, (areaHeightPx - cardHeight).coerceAtLeast(0f)),
                width = cardWidth,
                height = cardHeight,
                rotationDegrees = random.nextFloat() * 6f - 3f,
            )
            if (placed.none { overlapsTooMuch(it, candidate, allowedOverlapFraction) }) {
                placed += candidate
            } else {
                placed += candidate.copy(
                    x = baseX.coerceIn(0f, (areaWidthPx - cardWidth).coerceAtLeast(0f)),
                    y = baseY.coerceIn(0f, (areaHeightPx - cardHeight).coerceAtLeast(0f)),
                )
            }
        }
        return placed
    }

    internal fun overlapsTooMuch(
        a: ScatteredCardPlacement,
        b: ScatteredCardPlacement,
        allowedOverlapFraction: Float,
    ): Boolean {
        val overlapX = (min(a.x + a.width, b.x + b.width) - max(a.x, b.x)).coerceAtLeast(0f)
        val overlapY = (min(a.y + a.height, b.y + b.height) - max(a.y, b.y)).coerceAtLeast(0f)
        if (overlapX <= 0f || overlapY <= 0f) return false
        val intersectionArea = overlapX * overlapY
        val smallestArea = min(a.width * a.height, b.width * b.height)
        return intersectionArea > smallestArea * allowedOverlapFraction
    }
}
