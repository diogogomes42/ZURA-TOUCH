package com.zuratouch.feature.directpurchase.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.feature.directpurchase.theme.ZuraColors
import com.zuratouch.prizewheel.domain.DirectPurchaseOffer

private const val GRID_COLUMNS = 4

@Composable
fun DirectPurchaseCatalogLayout(
    machineOffers: List<DirectPurchaseOffer>,
    digitalOffers: List<DirectPurchaseOffer>,
    onSelectOffer: (DirectPurchaseOffer) -> Unit,
    machineSectionTitle: String,
    digitalSectionTitle: String,
    machineEmptyMessage: String,
    modifier: Modifier = Modifier,
) {
    val machineWeight = machineOffers.size.coerceAtLeast(if (machineOffers.isEmpty()) 0 else 1)
    val digitalWeight = digitalOffers.size.coerceAtLeast(1)

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (machineOffers.isNotEmpty()) {
            CatalogSectionGrid(
                title = machineSectionTitle,
                offers = machineOffers,
                onSelectOffer = onSelectOffer,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(machineWeight.toFloat()),
            )
        } else {
            Text(
                text = machineEmptyMessage,
                color = ZuraColors.TextMuted,
                fontSize = 10.sp,
                modifier = Modifier.padding(bottom = 2.dp),
            )
        }
        CatalogSectionGrid(
            title = digitalSectionTitle,
            offers = digitalOffers,
            onSelectOffer = onSelectOffer,
            modifier = Modifier
                .fillMaxWidth()
                .weight(digitalWeight.toFloat()),
        )
    }
}

@Composable
private fun CatalogSectionGrid(
    title: String,
    offers: List<DirectPurchaseOffer>,
    onSelectOffer: (DirectPurchaseOffer) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = title,
            color = ZuraColors.Accent,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 0.5.sp,
        )
        AdaptiveOfferGrid(
            offers = offers,
            onSelectOffer = onSelectOffer,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
    }
}

@Composable
private fun AdaptiveOfferGrid(
    offers: List<DirectPurchaseOffer>,
    onSelectOffer: (DirectPurchaseOffer) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (offers.isEmpty()) return

    BoxWithConstraints(modifier = modifier) {
        val columns = GRID_COLUMNS
        val rows = (offers.size + columns - 1) / columns
        val rowSpacing = 6.dp
        val totalRowSpacing = rowSpacing * (rows - 1).coerceAtLeast(0)
        val rowHeight = (maxHeight - totalRowSpacing) / rows.coerceAtLeast(1)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(rowSpacing),
        ) {
            offers.chunked(columns).forEach { rowOffers ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rowHeight),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    rowOffers.forEach { offer ->
                        DirectPurchaseGridCard(
                            offer = offer,
                            onBuyClick = { onSelectOffer(offer) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        )
                    }
                    repeat(columns - rowOffers.size) {
                        Row(modifier = Modifier.weight(1f)) {}
                    }
                }
            }
        }
    }
}
