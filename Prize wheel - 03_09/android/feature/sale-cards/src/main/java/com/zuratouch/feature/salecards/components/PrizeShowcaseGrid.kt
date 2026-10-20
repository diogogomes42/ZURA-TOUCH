package com.zuratouch.feature.salecards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.feature.salecards.AvailablePrizeDisplay
import com.zuratouch.feature.salecards.AvailablePrizes
import com.zuratouch.feature.salecards.theme.ZuraColors

/** Até 24 prémios (4×6) — mesmo tamanho de carta que o tabuleiro de jogo. */
@Composable
fun PrizeShowcaseGrid(
    prizes: List<AvailablePrizeDisplay>,
    modifier: Modifier = Modifier,
    onPrizeClick: ((AvailablePrizeDisplay) -> Unit)? = null,
    prizesClickable: Boolean = false,
) {
    val visible = prizes.take(AvailablePrizes.MAX_SHOWCASE)
    val rowCount = ((visible.size + CardGridSpec.COLUMNS - 1) / CardGridSpec.COLUMNS)
        .coerceIn(1, CardGridSpec.ROWS)

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        val density = LocalDensity.current
        val metrics = computeCardGridMetrics(
            areaWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f),
            rowCount = rowCount,
            density = density,
        )
        val cardW = with(density) { metrics.cardWidthPx.toDp() }
        val cardH = with(density) { metrics.cardHeightPx.toDp() }
        val gridW = with(density) { metrics.gridWidthPx.toDp() }
        val gridH = with(density) { metrics.gridHeightPx.toDp() }
        val needsScroll = metrics.gridHeightPx > constraints.maxHeight
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (needsScroll) Modifier.verticalScroll(scrollState) else Modifier),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .width(gridW)
                    .height(gridH),
                verticalArrangement = Arrangement.spacedBy(CardGridSpec.SPACING),
            ) {
                visible.chunked(CardGridSpec.COLUMNS).forEach { rowPrizes ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = CardGridSpec.SPACING,
                            alignment = Alignment.CenterHorizontally,
                        ),
                    ) {
                        rowPrizes.forEach { prize ->
                            val cardModifier = Modifier.size(width = cardW, height = cardH)
                            CardProductFace(
                                productName = prize.productName,
                                imageRes = prize.imageRes,
                                categoryColor = prize.categoryColor,
                                modifier = if (onPrizeClick != null) {
                                    cardModifier.clickable(
                                        enabled = prizesClickable,
                                        interactionSource = remember(prize.productName) { MutableInteractionSource() },
                                        indication = null,
                                        onClick = { onPrizeClick(prize) },
                                    )
                                } else {
                                    cardModifier
                                },
                                cornerRadius = 12.dp,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Generic card back — not a product photo. */
@Composable
fun CardBackFace(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ZuraColors.CardBackTop, ZuraColors.CardBackBottom),
                ),
            )
            .border(1.5.dp, ZuraColors.Accent.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "?",
            color = ZuraColors.Accent.copy(alpha = 0.85f),
            fontSize = 22.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
        )
    }
}
