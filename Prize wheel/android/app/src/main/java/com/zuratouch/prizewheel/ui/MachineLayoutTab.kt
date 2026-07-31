package com.zuratouch.prizewheel.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.prizewheel.data.MachineLayoutSeed
import com.zuratouch.prizewheel.domain.Spiral
import com.zuratouch.prizewheel.ui.theme.ZuraColors

@Composable
fun MachineLayoutGrid(state: OperatorUiState) {
    val slotsByPosition = state.stock.associateBy { it.shelf to it.slot }
    val categoryColors = state.categories.associate { it.id to Color(it.color) }
    val categoryLabels = state.categories.associate { it.id to it.label }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 52.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            listOf("Esquerda", "Centro", "Direita").forEach { label ->
                Text(
                    label,
                    color = ZuraColors.TextMuted,
                    fontSize = 10.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            for (shelf in 1..MachineLayoutSeed.SHELVES) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        "Cam. $shelf",
                        color = ZuraColors.TextMuted,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(end = 4.dp),
                    )
                    for (slot in 1..MachineLayoutSeed.SLOTS_PER_SHELF) {
                        MachineCell(
                            spiral = slotsByPosition[shelf to slot],
                            categoryColor = slotsByPosition[shelf to slot]?.frontProduct?.let {
                                categoryColors[it.categoryId]
                            },
                            categoryLabel = slotsByPosition[shelf to slot]?.frontProduct?.let {
                                categoryLabels[it.categoryId]
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
        Text(
            "▼ Ecrã / cliente",
            color = ZuraColors.Accent,
            fontSize = 11.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun MachineLayoutSection(state: OperatorUiState, onLoadTestLayout: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OperatorSectionTitle(
            title = "Mapa da máquina",
            description = "Cada quadrado = uma espiral. Cor = tipo do produto na frente da fila (dispensável).",
        )
        MachineLayoutGrid(state)
        OperatorFieldHint("«3/10» = 3 produtos na fila numa espiral de capacidade 10.")
        OutlinedButton(onClick = onLoadTestLayout, modifier = Modifier.fillMaxWidth()) {
            Text("Carregar exemplo de teste")
        }
    }
}

@Composable
private fun MachineCell(
    spiral: Spiral?,
    categoryColor: Color?,
    categoryLabel: String?,
    modifier: Modifier = Modifier,
) {
    val fillRatio = if (spiral != null && spiral.maxCapacity > 0) {
        spiral.queueLength.toFloat() / spiral.maxCapacity.toFloat()
    } else {
        0f
    }
    Column(
        modifier = modifier
            .aspectRatio(0.9f)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, ZuraColors.TextMuted.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .background(
                if (spiral != null && !spiral.isEmpty && categoryColor != null) {
                    categoryColor.copy(alpha = 0.2f + fillRatio.coerceIn(0f, 1f) * 0.6f)
                } else {
                    Color(0xFF1A2030)
                },
            )
            .padding(5.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        if (spiral == null || spiral.isEmpty) {
            Text(
                "Vazio",
                color = ZuraColors.TextMuted,
                fontSize = 10.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        } else {
            Text(
                spiral.frontProduct?.productName.orEmpty(),
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 11.sp,
            )
            Column {
                Text(
                    categoryLabel ?: "—",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${spiral.queueLength}/${spiral.maxCapacity}",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

fun spiralSizeForCapacity(maxCapacity: Int): MachineLayoutSeed.SpiralSize = when (maxCapacity) {
    MachineLayoutSeed.SpiralSize.SMALL.capacity -> MachineLayoutSeed.SpiralSize.SMALL
    MachineLayoutSeed.SpiralSize.LARGE.capacity -> MachineLayoutSeed.SpiralSize.LARGE
    else -> MachineLayoutSeed.SpiralSize.MEDIUM
}
