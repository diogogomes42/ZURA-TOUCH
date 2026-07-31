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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import com.zuratouch.prizewheel.data.OperatorPinHasher
import com.zuratouch.prizewheel.domain.SaleLogResult
import com.zuratouch.prizewheel.hardware.HardwareStatus
import com.zuratouch.prizewheel.ui.components.HardwareDegradedBanner
import com.zuratouch.prizewheel.ui.theme.ZuraColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToLong
import kotlinx.coroutines.delay

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

private const val OPERATOR_INACTIVITY_TIMEOUT_MS = 30_000L

@Composable
fun OperatorScreen(
    viewModel: OperatorViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    var lastPinInteractionMs by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(lastPinInteractionMs, state.authenticated) {
        if (state.authenticated) return@LaunchedEffect
        delay(OPERATOR_INACTIVITY_TIMEOUT_MS)
        viewModel.logout()
        onBack()
    }

    val pinInactivityModifier = if (!state.authenticated) {
        Modifier.pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    if (event.changes.any { it.pressed }) {
                        lastPinInteractionMs = System.currentTimeMillis()
                    }
                }
            }
        }
    } else {
        Modifier
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(pinInactivityModifier)
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
        if (state.authenticated) {
            when (val hardwareStatus = state.hardwareStatus) {
                is HardwareStatus.Degraded -> {
                    HardwareDegradedBanner(status = hardwareStatus)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                else -> Unit
            }
        }
        if (!state.authenticated) {
            PinEntry(
                pin = state.pinInput,
                error = state.pinError,
                title = when {
                    state.needsPinSetup && state.pinSetupPhase == PinSetupPhase.Confirm ->
                        "Confirmar PIN"
                    state.needsPinSetup ->
                        "Criar PIN (mín. ${OperatorPinHasher.MIN_LENGTH} dígitos)"
                    else ->
                        "Introduza o PIN"
                },
                onDigit = {
                    lastPinInteractionMs = System.currentTimeMillis()
                    viewModel.appendPin(it)
                },
                onClear = {
                    lastPinInteractionMs = System.currentTimeMillis()
                    viewModel.clearPin()
                },
                onSubmit = {
                    lastPinInteractionMs = System.currentTimeMillis()
                    viewModel.submitPin()
                },
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
    title: String,
    onDigit: (String) -> Unit,
    onClear: () -> Unit,
    onSubmit: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(title, color = ZuraColors.TextMuted)
        Text(
            text = "•".repeat(pin.length),
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
    val tabs = listOf(
        "Roleta" to "Tipos de prémio e probabilidades",
        "Stock" to "Produtos na máquina",
        "Definições" to "Preço e sistema",
        "Registos" to "Vendas e testes",
    )
    TabRow(
        selectedTabIndex = tab,
        containerColor = ZuraColors.WheelRing,
        contentColor = Color.White,
    ) {
        tabs.forEachIndexed { index, (title, _) ->
            Tab(
                selected = tab == index,
                onClick = { tab = index },
                text = { Text(title, color = if (tab == index) ZuraColors.Accent else Color.White, fontSize = 12.sp) },
            )
        }
    }
    Text(
        tabs[tab].second,
        color = ZuraColors.TextMuted,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
    )
    when (tab) {
        0 -> WheelCategoriesTab(state, viewModel)
        1 -> StockTab(state, viewModel)
        2 -> ConfigTab(state, viewModel)
        3 -> RecordsTab(state, viewModel)
    }
}

@Composable
private fun ConfigTab(state: OperatorUiState, viewModel: OperatorViewModel) {
    val config = state.config ?: return
    var price by remember(config) { mutableStateOf(formatEuroAmount(config.spinPriceCents / 100.0)) }
    var serial by remember(config) { mutableStateOf(config.serialPortPath) }
    var label by remember(config) { mutableStateOf(config.mysteryBoxLabel) }
    var pin by remember(config) { mutableStateOf("") }
    var pinError by remember(config) { mutableStateOf<String?>(null) }
    var sound by remember(config) { mutableStateOf(config.soundEnabled) }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OperatorHelpBox("Preço cobrado por cada jogada e ligação à máquina de vending.")

        Text("Preço por jogada (€)", color = Color.White, fontSize = 14.sp)
        OperatorFieldHint("Valor que o cliente paga para rodar a roleta.")
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
            label = "Porta de ligação à máquina",
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        OperatorFieldHint("Normalmente /dev/ttyS0 — só altere se o técnico indicar.")

        OperatorTextField(
            value = label,
            onValueChange = { label = it },
            label = "Nome do prémio surpresa",
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        OperatorFieldHint("Texto usado para prémios especiais na animação.")

        OperatorTextField(
            value = pin,
            onValueChange = {
                pin = it.filter { char -> char.isDigit() }.take(OperatorPinHasher.MAX_LENGTH)
                pinError = null
            },
            label = "Novo PIN de operador (opcional)",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
            visualTransformation = PasswordVisualTransformation(),
        )
        OperatorFieldHint("Mínimo ${OperatorPinHasher.MIN_LENGTH} dígitos. Deixe vazio para manter o actual.")
        pinError?.let { Text(it, color = ZuraColors.Error) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Sons na roleta", color = Color.White)
                OperatorFieldHint("Tiques durante a rotação e fanfarra ao ganhar.")
            }
            Button(onClick = { sound = !sound }) { Text(if (sound) "Ligado" else "Desligado") }
        }
        Button(
            onClick = {
                val cents = euroAmountToCents(price) ?: return@Button
                if (pin.isNotBlank() && !OperatorPinHasher.isValidPin(pin)) {
                    pinError = "PIN deve ter pelo menos ${OperatorPinHasher.MIN_LENGTH} dígitos"
                    return@Button
                }
                pinError = null
                viewModel.saveConfig(cents, serial, label, pin.ifBlank { null }, sound)
                pin = ""
                price = formatEuroAmount(cents / 100.0)
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Guardar") }
    }
}

@Composable
internal fun OperatorTextField(
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
private fun RecordsTab(state: OperatorUiState, viewModel: OperatorViewModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OperatorSectionTitle(
            title = "Histórico de jogadas",
            description = "Últimas tentativas: pagamento, entrega e reembolsos.",
        )
        if (state.sales.isEmpty()) {
            Text("Ainda não há registos.", color = ZuraColors.TextMuted)
        } else {
            state.sales.forEach { sale ->
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
                    Text("Produto: ${sale.productName ?: "—"}", color = ZuraColors.TextMuted, fontSize = 12.sp)
                    Text(saleResultLabel(sale.result), color = resultColor, fontSize = 12.sp)
                }
            }
        }

        OperatorSectionTitle(
            title = "Testes da máquina",
            description = "Verificar ligação e entrega manual por motor. Use só para manutenção.",
        )
        DiagnosticsPanel(state, viewModel)
    }
}

@Composable
private fun DiagnosticsPanel(state: OperatorUiState, viewModel: OperatorViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        when (val hardwareStatus = state.hardwareStatus) {
            HardwareStatus.Connected -> Text("Ligação: OK", color = Color(0xFF6EE7B7))
            HardwareStatus.Simulated -> Text("Modo de teste (sem máquina real)", color = ZuraColors.TextMuted)
            is HardwareStatus.Degraded -> {
                HardwareDegradedBanner(status = hardwareStatus)
                Text(
                    text = "Máquina desligada — pagamentos e entregas bloqueados até reconectar.",
                    color = ZuraColors.TextMuted,
                    fontSize = 12.sp,
                )
            }
        }
        Button(onClick = viewModel::runReconnectTest, modifier = Modifier.fillMaxWidth()) {
            Text("Reconectar à máquina")
        }
        Button(onClick = viewModel::runDeviceIdTest, modifier = Modifier.fillMaxWidth()) {
            Text("Identificar controlador")
        }
        Text("Testar entrega por motor:", color = Color.White, fontSize = 13.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1, 2, 3, 4).forEach { lane ->
                Button(onClick = { viewModel.runDispenseTest(lane) }, modifier = Modifier.weight(1f)) {
                    Text("Motor $lane")
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
