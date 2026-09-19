package com.zuratouch.feature.salewheel.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.zuratouch.feature.salewheel.R
import com.zuratouch.feature.salewheel.theme.ZuraColors
import com.zuratouch.prizewheel.domain.PrizeTierCatalog
import com.zuratouch.prizewheel.domain.PrizeTierCustomerInfo
import com.zuratouch.prizewheel.domain.PrizeTierInfoEntry
import java.util.Locale

@Composable
fun PrizeTierPickerDialog(
    selectedTierId: String,
    onTierSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var showTierInfo by remember { mutableStateOf(false) }
    val tiers = remember { PrizeTierCatalog.tiers }

    if (showTierInfo) {
        PrizeTierInfoDialog(onDismiss = { showTierInfo = false })
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ZuraColors.WheelRing,
            tonalElevation = 8.dp,
            shadowElevation = 14.dp,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Box(modifier = Modifier.size(34.dp))
                    Text(
                        text = stringResource(R.string.tier_selection_title),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.3.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                    TierInfoIconButton(onClick = { showTierInfo = true })
                }
                Text(
                    text = stringResource(R.string.tier_selection_subtitle),
                    color = Color.White.copy(alpha = 0.62f),
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    textAlign = TextAlign.Center,
                )
                tiers.forEach { tier ->
                    TierPickerRow(
                        entry = PrizeTierCustomerInfo.entryFor(tier),
                        categoryIds = PrizeTierCustomerInfo.categoryIdsFor(tier),
                        selected = tier.id == selectedTierId,
                        progressionIndex = PrizeTierCustomerInfo.progressionIndex(tier.id),
                        onClick = {
                            onTierSelected(tier.id)
                            onDismiss()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun TierPickerRow(
    entry: PrizeTierInfoEntry,
    categoryIds: List<String>,
    selected: Boolean,
    progressionIndex: Int,
    onClick: () -> Unit,
) {
    val isPrime = entry.tier.id == PrizeTierCatalog.PRIME
    val progressAlpha = 0.24f + progressionIndex * 0.14f
    val accentBarWidth = (3 + progressionIndex).coerceAtMost(6).dp
    val borderColor = when {
        selected -> ZuraColors.Accent
        isPrime -> ZuraColors.Accent.copy(alpha = 0.42f)
        else -> Color.White.copy(alpha = 0.14f + progressionIndex * 0.02f)
    }
    val containerColor = when {
        selected -> ZuraColors.Accent.copy(alpha = 0.14f)
        isPrime -> ZuraColors.Accent.copy(alpha = 0.08f)
        else -> Color.White.copy(alpha = 0.04f + progressionIndex * 0.012f)
    }
    val borderWidth = when {
        selected -> 2.dp
        isPrime -> 1.5.dp
        else -> 1.dp
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(borderWidth, borderColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            Box(
                modifier = Modifier
                    .width(accentBarWidth)
                    .fillMaxHeight()
                    .background(ZuraColors.Accent.copy(alpha = progressAlpha.coerceIn(0.24f, 0.92f))),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        TierLevelBadge(
                            tierId = entry.tier.id,
                            selected = selected,
                        )
                        Text(
                            text = entry.tier.name,
                            color = if (selected) ZuraColors.Accent else Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = (15.5f + progressionIndex * 0.15f).sp,
                            letterSpacing = 0.7.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        text = formatTierPrice(entry.tier.priceCents),
                        color = ZuraColors.Accent,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        maxLines = 1,
                    )
                }
                Text(
                    text = entry.headline,
                    color = Color.White.copy(alpha = if (selected) 0.82f else 0.68f),
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                PrizeRarityChipsRow(categoryIds = categoryIds)
            }
        }
    }
}

private fun formatTierPrice(cents: Long): String =
    String.format(Locale("pt", "PT"), "€%.2f", cents / 100.0)

@Composable
private fun TierInfoIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infoLabel = stringResource(R.string.tier_info_content_description)
    Surface(
        onClick = onClick,
        modifier = modifier
            .size(34.dp)
            .semantics { contentDescription = infoLabel },
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
        shadowElevation = 0.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "ⓘ",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
