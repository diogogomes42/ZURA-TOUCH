package com.zuratouch.prizewheel.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.prizewheel.domain.SaleLogResult
import com.zuratouch.prizewheel.ui.theme.ZuraColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToLong

private fun parseEuroAmount(input: String): Double? {
    val cleaned = input.trim().replace("€", "").replace(" ", "").replace(',', '.')
    if (cleaned.isEmpty()) return null
    return cleaned.toDoubleOrNull()
}

private fun formatEuroAmount(amount: Double): String = String.format(Locale.US, "%.2f", amount)

private fun euroAmountToCents(input: String): Long? {
    val amount = parseEuroAmount(input) ?: return null
    if (amount < 0.01) return null
    return (amount * 100.0).roundToLong()
}

@Composable
fun OperatorScreen(
    viewModel: OperatorViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZuraColors.BackgroundDark)
            .padding(24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Operador", color = Color.White, fontSize = 22.sp)
            Button(onClick = {
                if (state.authenticated) viewModel.logout()
                onBack()
            }) { Text("Fechar") }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (!state.authenticated) {
            PinEntry(
                pin = state.pinInput,
                error = state.pinError,
                onDigit = viewModel::appendPin,
                onClear = viewModel::clearPin,
                onSubmit = viewModel::submitPin,
            )
        } else {
            OperatorTabs(viewModel = viewModel, state = state)
        }
    }
}

@Composable
private fun PinEntry(
    pin: String,
    error: String?,
    onDigit: (String) -> Unit,
    onClear: () -> Unit,
    onSubmit: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("Introduza o PIN", color = ZuraColors.TextMuted)
        Text(
            text = pin.padEnd(4, '•').take(4),
            color = Color.White,
            fontSize = 32.sp,
            modifier = Modifier.padding(vertical = 16.dp),
        )
        if (error != null) Text(error, color = ZuraColors.Error)
        val digits = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "OK")
        digits.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                row.forEach { key ->
                    Button(onClick = {
                        when (key) {
                            "C" -> onClear()
                            "OK" -> onSubmit()
                            else -> onDigit(key)
                        }
                    }) { Text(key) }
                }
            }
        }
    }
}

@Composable
private fun OperatorTabs(viewModel: OperatorViewModel, state: OperatorUiState) {
    var tab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Stock", "Config", "Histórico", "Diagnóstico")
    TabRow(
        selectedTabIndex = tab,
        containerColor = ZuraColors.WheelRing,
        contentColor = Color.White,
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = tab == index,
                onClick = { tab = index },
                text = { Text(title, color = if (tab == index) ZuraColors.Accent else Color.White) },
            )
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
    when (tab) {
        0 -> StockTab(state, viewModel)
        1 -> ConfigTab(state, viewModel)
        2 -> HistoryTab(state)
        3 -> DiagnosticsTab(state, viewModel)
    }
}

@Composable
private fun StockTab(state: OperatorUiState, viewModel: OperatorViewModel) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(state.stock) { slot ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ZuraColors.WheelRing, androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(slot.productName, color = Color.White)
                    Text("Corredor ${slot.vmcLane} · Prateleira ${slot.shelf}", color = ZuraColors.TextMuted, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { viewModel.updateSlotQuantity(slot.id, slot.quantity - 1) }) { Text("-") }
                    Text("${slot.quantity}", color = Color.White, modifier = Modifier.padding(horizontal = 12.dp))
                    Button(onClick = { viewModel.updateSlotQuantity(slot.id, slot.quantity + 1) }) { Text("+") }
                }
            }
        }
    }
}

@Composable
private fun ConfigTab(state: OperatorUiState, viewModel: OperatorViewModel) {
    val config = state.config ?: return
    var price by remember(config) { mutableStateOf(formatEuroAmount(config.spinPriceCents / 100.0)) }
    var serial by remember(config) { mutableStateOf(config.serialPortPath) }
    var label by remember(config) { mutableStateOf(config.mysteryBoxLabel) }
    var pin by remember(config) { mutableStateOf(config.operatorPin) }
    var sound by remember(config) { mutableStateOf(config.soundEnabled) }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Preço por roleta (€)", color = Color.White, fontSize = 14.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(onClick = {
                val current = parseEuroAmount(price) ?: 2.0
                price = formatEuroAmount((current - 0.5).coerceAtLeast(0.5))
            }) { Text("−") }
            OperatorTextField(
                value = price,
                onValueChange = { price = it.filter { char -> char.isDigit() || char == '.' || char == ',' } },
                label = "Valor",
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
            )
            Button(onClick = {
                val current = parseEuroAmount(price) ?: 2.0
                price = formatEuroAmount(current + 0.5)
            }) { Text("+") }
        }
        OperatorTextField(
            value = serial,
            onValueChange = { serial = it },
            label = "Porta serial",
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        OperatorTextField(
            value = label,
            onValueChange = { label = it },
            label = "Nome Mystery Box",
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        OperatorTextField(
            value = pin,
            onValueChange = { pin = it },
            label = "PIN operador",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
            visualTransformation = PasswordVisualTransformation(),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Sons activos", color = Color.White, modifier = Modifier.weight(1f))
            Button(onClick = { sound = !sound }) { Text(if (sound) "Sim" else "Não") }
        }
        Button(
            onClick = {
                val cents = euroAmountToCents(price) ?: return@Button
                viewModel.saveConfig(cents, serial, label, pin, sound)
                price = formatEuroAmount(cents / 100.0)
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Guardar") }
    }
}

@Composable
private fun OperatorTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val activity = LocalContext.current as? ComponentActivity
    val view = LocalView.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        disabledTextColor = ZuraColors.TextMuted,
        cursorColor = ZuraColors.Accent,
        focusedBorderColor = ZuraColors.Accent,
        unfocusedBorderColor = ZuraColors.TextMuted,
        focusedLabelColor = ZuraColors.Accent,
        unfocusedLabelColor = ZuraColors.TextMuted,
        focusedContainerColor = ZuraColors.WheelRing,
        unfocusedContainerColor = ZuraColors.WheelRing,
    )
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        colors = colors,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    activity?.let { OperatorKeyboard.show(it, view) }
                    keyboardController?.show()
                }
            },
    )
}

@Composable
private fun HistoryTab(state: OperatorUiState) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(state.sales) { sale ->
            val time = SimpleDateFormat("dd/MM HH:mm", Locale("pt", "PT")).format(Date(sale.timestampMs))
            val resultColor = when (sale.result) {
                SaleLogResult.SUCCESS -> Color(0xFF6EE7B7)
                SaleLogResult.PAYMENT_FAILED -> ZuraColors.Error
                SaleLogResult.DISPENSE_FAILED -> Color(0xFFFBBF24)
                SaleLogResult.REFUNDED -> Color(0xFF93C5FD)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ZuraColors.WheelRing, androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(12.dp),
            ) {
                Text("$time · ${sale.categoryLabel}", color = Color.White)
                Text(sale.productName ?: "—", color = ZuraColors.TextMuted, fontSize = 12.sp)
                Text(sale.result.name, color = resultColor, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun DiagnosticsTab(state: OperatorUiState, viewModel: OperatorViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(onClick = viewModel::runReconnectTest, modifier = Modifier.fillMaxWidth()) { Text("Reconectar UART") }
        Button(onClick = viewModel::runDeviceIdTest, modifier = Modifier.fillMaxWidth()) { Text("Ler DEVICE_ID") }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1, 2, 3, 4).forEach { lane ->
                Button(onClick = { viewModel.runDispenseTest(lane) }, modifier = Modifier.weight(1f)) {
                    Text("L$lane")
                }
            }
        }
        if (state.isRunningDiagnostic) {
            CircularProgressIndicator(color = ZuraColors.Accent, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        state.diagnosticsMessage?.let {
            Text(it, color = ZuraColors.TextMuted, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
