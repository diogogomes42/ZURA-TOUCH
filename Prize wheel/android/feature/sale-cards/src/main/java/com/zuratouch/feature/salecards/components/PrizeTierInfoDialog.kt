package com.zuratouch.feature.salecards.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zuratouch.feature.salecards.R
import com.zuratouch.feature.salecards.theme.ZuraColors
import com.zuratouch.prizewheel.domain.PrizeTierCatalog
import com.zuratouch.prizewheel.domain.PrizeTierCustomerInfo
import com.zuratouch.prizewheel.domain.PrizeTierInfoEntry
import java.util.Locale

@Composable
fun PrizeTierInfoDialog(
    availableTierIds: List<String>?,
    onDismiss: () -> Unit,
) {
    val tiers = remember(availableTierIds) {
        val catalog = PrizeTierCatalog.tiers
        if (availableTierIds != null) catalog.filter { it.id in availableTierIds } else catalog
    }
    val entries = remember(tiers) { PrizeTierCustomerInfo.entriesFor(tiers) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 340.dp),
            shape = RoundedCornerShape(18.dp),
            color = ZuraColors.WheelRing,
            tonalElevation = 8.dp,
            shadowElevation = 14.dp,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 18.dp, vertical = 16.dp)
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = stringResource(R.string.tier_info_title),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(R.string.tier_info_subtitle),
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                )
                entries.forEach { entry ->
                    TierInfoRow(
                        entry = entry,
                        categoryIds = PrizeTierCustomerInfo.categoryIdsFor(entry.tier),
                    )
                }
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ZuraColors.Accent,
                        contentColor = ZuraColors.BackgroundDark,
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = stringResource(R.string.tier_info_close),
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun TierInfoRow(
    entry: PrizeTierInfoEntry,
    categoryIds: List<String>,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = entry.tier.name,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    letterSpacing = 0.6.sp,
                )
                Text(
                    text = formatTierPrice(entry.tier.priceCents),
                    color = ZuraColors.Accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }
            Text(
                text = entry.headline,
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
            PrizeRarityChipsRow(categoryIds = categoryIds)
        }
    }
}

private fun formatTierPrice(cents: Long): String =
    String.format(Locale("pt", "PT"), "€%.2f", cents / 100.0)
