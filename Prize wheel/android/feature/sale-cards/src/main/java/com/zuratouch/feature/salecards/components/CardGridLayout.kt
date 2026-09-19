package com.zuratouch.feature.salecards.components

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

object CardGridSpec {
    /** Vitrine espalhada (idle). */
    const val SHOWCASE_COLUMNS = 4
    /** Tabuleiro de jogo — 24 cartas em 6×4 cabem sem scroll. */
    const val BOARD_COLUMNS = 6
    const val BOARD_ROWS = 4
    @Deprecated("Use SHOWCASE_COLUMNS or BOARD_COLUMNS", ReplaceWith("SHOWCASE_COLUMNS"))
    const val COLUMNS = SHOWCASE_COLUMNS
    const val ROWS = 6
    const val SHOWCASE_ROWS = 7
    const val ASPECT = 0.72f
    val SPACING: Dp = 10.dp
    val BOARD_SPACING: Dp = 8.dp
    val SHOWCASE_SPACING: Dp = 12.dp
    /** Cartas grandes — sobreposição controlada permite escala elevada. */
    const val SHOWCASE_SCALE = 1.85f
    /** Gap reduzido no cálculo do tamanho máximo (há overlap permitido). */
    const val SHOWCASE_PACKING_GAP_FACTOR = 0.35f
    /** Até ~10% da área da carta pode sobrepor outra. */
    const val SHOWCASE_ALLOWED_OVERLAP_FRACTION = 0.10f
    val BANNER_SLOT_HEIGHT: Dp = 96.dp
}

data class CardGridMetrics(
    val cardWidthPx: Float,
    val cardHeightPx: Int,
    val spacingPx: Int,
    val gridWidthPx: Float,
    val gridHeightPx: Float,
    val columns: Int = CardGridSpec.BOARD_COLUMNS,
)

/** Tabuleiro de jogo — limita largura e altura para caber no ecrã sem scroll. */
fun computeBoardGridMetrics(
    areaWidthPx: Float,
    areaHeightPx: Float,
    slotCount: Int,
    density: Density,
    columns: Int = CardGridSpec.BOARD_COLUMNS,
    reservedTopPx: Float = 0f,
): CardGridMetrics {
    val rowCount = ((slotCount + columns - 1) / columns).coerceAtLeast(1)
    val spacingPx = with(density) { CardGridSpec.BOARD_SPACING.roundToPx() }
    val usableHeightPx = (areaHeightPx - reservedTopPx).coerceAtLeast(1f)
    val cardWidthFromWidth = ((areaWidthPx - spacingPx * (columns - 1)) / columns).coerceAtLeast(1f)
    val rowGaps = (rowCount - 1).coerceAtLeast(0) * spacingPx
    val cardHeightFromHeight = ((usableHeightPx - rowGaps) / rowCount).coerceAtLeast(1f)
    val cardWidthFromHeight = cardHeightFromHeight * CardGridSpec.ASPECT
    val cardWidthPx = minOf(cardWidthFromWidth, cardWidthFromHeight)
    val cardHeightPx = (cardWidthPx / CardGridSpec.ASPECT).roundToInt()
    val gridWidthPx = columns * cardWidthPx + spacingPx * (columns - 1)
    val gridHeightPx = rowCount * cardHeightPx + rowGaps
    return CardGridMetrics(
        cardWidthPx = cardWidthPx,
        cardHeightPx = cardHeightPx,
        spacingPx = spacingPx,
        gridWidthPx = gridWidthPx,
        gridHeightPx = gridHeightPx.toFloat(),
        columns = columns,
    )
}
