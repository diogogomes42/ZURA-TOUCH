package com.zuratouch.feature.salecards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.feature.salecards.AvailablePrizeDisplay
import com.zuratouch.feature.salecards.theme.ZuraColors

/** Prémios em stock real da máquina — atualiza quando o operador repõe stock. */
@Composable
fun PrizeShowcaseGrid(
    prizes: List<AvailablePrizeDisplay>,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        items(prizes, key = { it.productName }) { prize ->
            CardProductFace(
                productName = prize.productName,
                imageRes = prize.imageRes,
                categoryColor = prize.categoryColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.72f),
                cornerRadius = 10.dp,
            )
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
