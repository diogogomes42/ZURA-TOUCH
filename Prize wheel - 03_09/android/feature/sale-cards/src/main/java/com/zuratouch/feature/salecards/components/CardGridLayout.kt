package com.zuratouch.feature.salecards.components

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

object CardGridSpec {
    const val COLUMNS = 4
    const val ROWS = 6
    const val ASPECT = 0.72f
    val SPACING: Dp = 10.dp
    val BANNER_SLOT_HEIGHT: Dp = 96.dp
}

data class CardGridMetrics(
    val cardWidthPx: Float,
    val cardHeightPx: Int,
    val spacingPx: Int,
    val gridWidthPx: Float,
    val gridHeightPx: Float,
    val columns: Int = CardGridSpec.COLUMNS,
)

/** Mesma lógica de tamanho usada no tabuleiro de jogo — largura da grelha define o tamanho das cartas. */
fun computeCardGridMetrics(
    areaWidthPx: Float,
    rowCount: Int,
    density: Density,
): CardGridMetrics {
    val columns = CardGridSpec.COLUMNS
    val spacingPx = with(density) { CardGridSpec.SPACING.roundToPx() }
    val cardWidthPx = ((areaWidthPx - spacingPx * (columns - 1)) / columns).coerceAtLeast(1f)
    val cardHeightPx = (cardWidthPx / CardGridSpec.ASPECT).roundToInt()
    val gridWidthPx = columns * cardWidthPx + spacingPx * (columns - 1)
    val gridHeightPx = rowCount * cardHeightPx + (rowCount - 1).coerceAtLeast(0) * spacingPx
    return CardGridMetrics(
        cardWidthPx = cardWidthPx,
        cardHeightPx = cardHeightPx,
        spacingPx = spacingPx,
        gridWidthPx = gridWidthPx,
        gridHeightPx = gridHeightPx.toFloat(),
    )
}
