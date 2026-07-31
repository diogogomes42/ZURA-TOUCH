package com.zuratouch.prizewheel.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.prizewheel.data.MachineLayoutSeed
import com.zuratouch.prizewheel.domain.PrizeCategory
import com.zuratouch.prizewheel.domain.QueuedProduct
import com.zuratouch.prizewheel.domain.Spiral
import com.zuratouch.prizewheel.ui.theme.ZuraColors
import kotlin.math.roundToInt

internal val CATEGORY_COLOR_OPTIONS = listOf(
    0xFF2B6CB0, 0xFF38A169, 0xFFB7791F, 0xFF805AD5,
    0xFFE53E3E, 0xFFDD6B20, 0xFF319795, 0xFFD53F8C,
)

private fun weightPercent(weight: Int, categories: List<PrizeCategory>): Int {
    val total = categories.sumOf { it.weight.coerceAtLeast(1) }
    if (total <= 0) return 0
    return (weight.coerceAtLeast(1) * 100f / total).roundToInt()
}

private fun slugFromLabel(label: String): String =
    label.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_').take(20).ifBlank { "tipo" }

@Composable
fun OperatorCatalogMessage(message: String?, onDismiss: () -> Unit) {
    if (message == null) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .background(ZuraColors.WheelRing, RoundedCornerShape(8.dp))
            .clickable(onClick = onDismiss)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(message, color = Color.White, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text("OK", color = ZuraColors.Accent, fontSize = 13.sp)
    }
}

/** Tipos de prémio que aparecem como segmentos na roleta. */
@Composable
fun WheelCategoriesTab(state: OperatorUiState, viewModel: OperatorViewModel) {
    var showNewForm by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        OperatorCatalogMessage(state.catalogMessage, viewModel::clearCatalogMessage)
        OperatorHelpBox(
            "A roleta mostra estes tipos de prémio (ex: Bebida, Snack). " +
                "Quanto maior a probabilidade, mais vezes esse tipo sai. " +
                "Só aparecem tipos que tenham pelo menos um produto com stock.",
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = { showNewForm = !showNewForm },
            modifier = Modifier.fillMaxWidth(),
        ) { Text(if (showNewForm) "Cancelar" else "+ Adicionar tipo na roleta") }
        if (showNewForm) {
            Spacer(modifier = Modifier.height(8.dp))
            CategoryForm(
                existing = null,
                categories = state.categories,
                onSave = { id, label, icon, color, weight ->
                    viewModel.saveCategory(id, label, icon, color, weight, isNew = true)
                    showNewForm = false
                },
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            state.categories.forEach { category ->
                CategoryCard(
                    category = category,
                    categories = state.categories,
                    productCount = viewModel.productCountForCategory(category.id),
                    onSave = { label, icon, color, weight ->
                        viewModel.saveCategory(category.id, label, icon, color, weight, isNew = false)
                    },
                    onDelete = { viewModel.deleteCategory(category.id) },
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: PrizeCategory,
    categories: List<PrizeCategory>,
    productCount: Int,
    onSave: (label: String, icon: String, color: Long, weight: Int) -> Unit,
    onDelete: () -> Unit,
) {
    var editing by remember(category.id) { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ZuraColors.WheelRing, RoundedCornerShape(8.dp))
            .padding(12.dp),
    ) {
        if (!editing) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(category.color)),
                    contentAlignment = Alignment.Center,
                ) { Text(category.icon, fontSize = 20.sp) }
                Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                    Text(category.label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        "Probabilidade: ${weightPercent(category.weight, categories)}% · $productCount unidade(s) na máquina",
                        color = ZuraColors.TextMuted,
                        fontSize = 12.sp,
                    )
                }
                OutlinedButton(onClick = { editing = true }) { Text("Alterar") }
            }
        } else {
            CategoryForm(
                existing = category,
                categories = categories,
                onSave = { _, label, icon, color, weight ->
                    onSave(label, icon, color, weight)
                    editing = false
                },
                onCancel = { editing = false },
                onDelete = if (productCount == 0) onDelete else null,
                deleteBlockedReason = if (productCount > 0) {
                    "Remova primeiro os produtos deste tipo na aba Stock."
                } else {
                    null
                },
            )
        }
    }
}

@Composable
private fun CategoryForm(
    existing: PrizeCategory?,
    categories: List<PrizeCategory>,
    onSave: (id: String, label: String, icon: String, color: Long, weight: Int) -> Unit,
    onCancel: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    deleteBlockedReason: String? = null,
) {
    var id by remember(existing?.id) { mutableStateOf(existing?.id ?: "") }
    var label by remember(existing?.id) { mutableStateOf(existing?.label ?: "") }
    var icon by remember(existing?.id) { mutableStateOf(existing?.icon ?: "🎁") }
    var weight by remember(existing?.id) { mutableIntStateOf(existing?.weight ?: 10) }
    var color by remember(existing?.id) { mutableStateOf(existing?.color ?: CATEGORY_COLOR_OPTIONS.first()) }
    var error by remember(existing?.id) { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OperatorTextField(
            value = label,
            onValueChange = {
                label = it
                if (existing == null) id = slugFromLabel(it)
                error = null
            },
            label = "Nome na roleta",
        )
        OperatorFieldHint("Texto que o cliente vê no segmento da roleta (ex: Bebida, Snack).")

        OperatorTextField(
            value = icon,
            onValueChange = { icon = it.take(4) },
            label = "Emoji do segmento",
        )

        Text("Cor do segmento", color = Color.White, fontSize = 13.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CATEGORY_COLOR_OPTIONS.forEach { option ->
                val selected = color == option
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(option))
                        .border(
                            width = if (selected) 3.dp else 0.dp,
                            color = if (selected) Color.White else Color.Transparent,
                            shape = CircleShape,
                        )
                        .clickable { color = option },
                )
            }
        }

        Text("Probabilidade de sair", color = Color.White, fontSize = 13.sp)
        OperatorFieldHint("Número relativo — não precisa somar 100. Ex: 65 vs 30 vs 5 ≈ 65% / 30% / 5%.")
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { weight = (weight - 5).coerceAtLeast(1) }) { Text("−") }
            Text(
                "$weight  →  ${weightPercent(weight, categories)}% na roleta",
                color = Color.White,
                fontSize = 14.sp,
            )
            Button(onClick = { weight += 5 }) { Text("+") }
        }

        if (existing == null && id.isNotBlank()) {
            OperatorFieldHint("Código interno: $id")
        }

        deleteBlockedReason?.let { Text(it, color = ZuraColors.TextMuted, fontSize = 11.sp) }
        error?.let { Text(it, color = ZuraColors.Error, fontSize = 12.sp) }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val finalId = (existing?.id ?: id).trim()
                    if (finalId.isBlank() || label.isBlank()) {
                        error = "Escreva o nome na roleta"
                        return@Button
                    }
                    onSave(finalId, label, icon, color, weight)
                },
                modifier = Modifier.weight(1f),
            ) { Text("Guardar") }
            onCancel?.let {
                OutlinedButton(onClick = it, modifier = Modifier.weight(1f)) { Text("Cancelar") }
            }
            onDelete?.let {
                OutlinedButton(onClick = it, modifier = Modifier.weight(1f)) { Text("Remover tipo") }
            }
        }
    }
}

/** Espiras físicas — cada uma tem uma fila FIFO de produtos (frente = dispensável). */
@Composable
fun StockTab(state: OperatorUiState, viewModel: OperatorViewModel) {
    val categoryLabels = state.categories.associate { it.id to it.label }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        OperatorCatalogMessage(state.catalogMessage, viewModel::clearCatalogMessage)

        if (state.categories.isEmpty()) {
            OperatorHelpBox("Primeiro adicione tipos de prémio na aba Roleta.")
            return@Column
        }

        OperatorHelpBox(
            "Cada espiral tem uma fila de produtos (frente → fundo). " +
                "Só o produto na frente pode sair. A fila pode misturar tipos diferentes — " +
                "após cada entrega, o próximo produto na fila passa à frente.",
        )

        MachineLayoutSection(state, viewModel::loadTestMachineLayout)

        Spacer(modifier = Modifier.height(16.dp))
        OperatorSectionTitle(
            title = "Espiras da máquina",
            description = "Toque «Repor» numa espiral vazia ou «Alterar fila» para editar a ordem dos produtos.",
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            state.stock.sortedWith(compareBy({ it.shelf }, { it.slot })).forEach { spiral ->
                SpiralCard(
                    spiral = spiral,
                    categoryLabels = categoryLabels,
                    categories = state.categories,
                    onPopFront = { viewModel.popFrontFromSpiral(spiral.id) },
                    onSave = { queue, capacity ->
                        viewModel.saveSpiralQueue(spiral.id, queue, capacity)
                    },
                    onClear = { viewModel.clearSpiral(spiral.id) },
                )
            }
        }
    }
}

@Composable
private fun SpiralCard(
    spiral: Spiral,
    categoryLabels: Map<String, String>,
    categories: List<PrizeCategory>,
    onPopFront: () -> Unit,
    onSave: (List<QueuedProduct>, Int) -> Unit,
    onClear: () -> Unit,
) {
    var editing by remember(spiral.id) { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ZuraColors.WheelRing, RoundedCornerShape(8.dp))
            .padding(12.dp),
    ) {
        if (!editing) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    slotPositionLabel(spiral.shelf, spiral.slot),
                    color = ZuraColors.Accent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
                if (!spiral.isEmpty) {
                    val front = spiral.frontProduct!!
                    Text(front.productName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        "Frente: ${categoryLabels[front.categoryId] ?: front.categoryId}",
                        color = ZuraColors.TextMuted,
                        fontSize = 12.sp,
                    )
                    Text(
                        "Fila: ${spiral.queueLength} de ${spiral.maxCapacity} (${spiralSizeLabel(spiral.maxCapacity)})",
                        color = Color.White,
                        fontSize = 12.sp,
                    )
                    if (spiral.queue.size > 1) {
                        Text(
                            "A seguir: " + spiral.queue.drop(1).take(3).joinToString(", ") { it.productName },
                            color = ZuraColors.TextMuted,
                            fontSize = 11.sp,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onPopFront, modifier = Modifier.weight(1f)) {
                            Text("Simular entrega")
                        }
                    }
                } else {
                    Text("Vazia — sem produtos na fila", color = ZuraColors.TextMuted, fontSize = 13.sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { editing = true }, modifier = Modifier.weight(1f)) {
                        Text(if (spiral.isEmpty) "Repor espiral" else "Alterar fila")
                    }
                    if (!spiral.isEmpty) {
                        OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f)) {
                            Text("Esvaziar")
                        }
                    }
                }
            }
        } else {
            SpiralQueueForm(
                existing = spiral,
                categories = categories,
                onSave = { queue, capacity ->
                    onSave(queue, capacity)
                    editing = false
                },
                onCancel = { editing = false },
            )
        }
    }
}

@Composable
private fun SpiralQueueForm(
    existing: Spiral,
    categories: List<PrizeCategory>,
    onSave: (List<QueuedProduct>, Int) -> Unit,
    onCancel: () -> Unit,
) {
    var queue by remember(existing.id) { mutableStateOf(existing.queue) }
    var spiralSize by remember(existing.id) {
        mutableStateOf(spiralSizeForCapacity(existing.maxCapacity))
    }
    var newName by remember(existing.id) { mutableStateOf("") }
    var newCategoryId by remember(existing.id) {
        mutableStateOf(categories.firstOrNull()?.id.orEmpty())
    }
    var error by remember(existing.id) { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            if (existing.isEmpty) "Repor espiral — construir fila" else "Editar fila da espiral",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        OperatorFieldHint(
            "O primeiro item é o produto na frente (dispensável). Pode misturar tipos na mesma fila.",
        )

        if (queue.isNotEmpty()) {
            Text("Fila (frente → fundo)", color = Color.White, fontSize = 13.sp)
            queue.forEachIndexed { index, item ->
                val cat = categories.firstOrNull { it.id == item.categoryId }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        if (index == 0) "▶" else "${index + 1}.",
                        color = if (index == 0) ZuraColors.Accent else ZuraColors.TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(end = 4.dp),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.productName, color = Color.White, fontSize = 13.sp)
                        Text(cat?.label ?: item.categoryId, color = ZuraColors.TextMuted, fontSize = 11.sp)
                    }
                    OutlinedButton(onClick = {
                        queue = queue.filterIndexed { i, _ -> i != index }
                    }) { Text("✕") }
                }
            }
        }

        Text("Adicionar ao fundo da fila", color = Color.White, fontSize = 13.sp)
        OperatorTextField(
            value = newName,
            onValueChange = { newName = it; error = null },
            label = "Nome do produto",
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            categories.forEach { category ->
                val selected = newCategoryId == category.id
                OutlinedButton(
                    onClick = { newCategoryId = category.id },
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (selected) Modifier.border(2.dp, ZuraColors.Accent, RoundedCornerShape(8.dp))
                            else Modifier,
                        ),
                ) { Text("${category.icon}  ${category.label}") }
            }
        }
        OutlinedButton(
            onClick = {
                if (newName.isBlank()) {
                    error = "Escreva o nome do produto"
                    return@OutlinedButton
                }
                if (queue.size >= spiralSize.capacity) {
                    error = "Fila cheia (máx. ${spiralSize.capacity})"
                    return@OutlinedButton
                }
                queue = queue + QueuedProduct(newCategoryId, newName.trim())
                newName = ""
                error = null
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("+ Adicionar à fila") }

        Text("Tamanho da espiral", color = Color.White, fontSize = 13.sp)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            MachineLayoutSeed.SpiralSize.entries.forEach { size ->
                val selected = spiralSize == size
                val title = when (size) {
                    MachineLayoutSeed.SpiralSize.SMALL -> "Pequena — até ${size.capacity} produtos"
                    MachineLayoutSeed.SpiralSize.MEDIUM -> "Média — até ${size.capacity} produtos"
                    MachineLayoutSeed.SpiralSize.LARGE -> "Grande — até ${size.capacity} produtos"
                }
                OutlinedButton(
                    onClick = { spiralSize = size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (selected) Modifier.border(2.dp, ZuraColors.Accent, RoundedCornerShape(8.dp))
                            else Modifier,
                        ),
                ) { Text(title) }
            }
        }

        OperatorFieldHint("${slotPositionLabel(existing.shelf, existing.slot)} · Motor ${existing.vmcLane}")

        error?.let { Text(it, color = ZuraColors.Error, fontSize = 12.sp) }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    when {
                        queue.isEmpty() -> error = "Adicione pelo menos um produto à fila"
                        queue.size > spiralSize.capacity ->
                            error = "Fila tem ${queue.size} produtos — máximo ${spiralSize.capacity}"
                        else -> onSave(queue, spiralSize.capacity)
                    }
                },
                modifier = Modifier.weight(1f),
            ) { Text("Guardar") }
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancelar") }
        }
    }
}
