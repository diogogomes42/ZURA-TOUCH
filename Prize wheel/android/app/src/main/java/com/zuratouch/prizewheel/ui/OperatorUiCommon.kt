package com.zuratouch.prizewheel.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.prizewheel.ui.theme.ZuraColors

@Composable
fun OperatorHelpBox(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = ZuraColors.TextMuted,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        modifier = modifier
            .fillMaxWidth()
            .background(ZuraColors.WheelRing.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
            .padding(12.dp),
    )
}

@Composable
fun OperatorSectionTitle(title: String, description: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(description, color = ZuraColors.TextMuted, fontSize = 12.sp, lineHeight = 16.sp)
    }
}

@Composable
fun OperatorFieldHint(text: String) {
    Text(text, color = ZuraColors.TextMuted, fontSize = 11.sp, lineHeight = 14.sp)
}

fun slotPositionLabel(shelf: Int, slot: Int): String {
    val horizontal = when (slot) {
        1 -> "esquerda"
        2 -> "centro"
        else -> "direita"
    }
    return "Camada $shelf, $horizontal"
}

fun spiralSizeLabel(capacity: Int): String = when (capacity) {
    5 -> "Espiral pequena (5)"
    15 -> "Espiral grande (15)"
    else -> "Espiral média (10)"
}

fun saleResultLabel(result: com.zuratouch.prizewheel.domain.SaleLogResult): String = when (result) {
    com.zuratouch.prizewheel.domain.SaleLogResult.SUCCESS -> "Entrega OK"
    com.zuratouch.prizewheel.domain.SaleLogResult.PAYMENT_FAILED -> "Pagamento falhou"
    com.zuratouch.prizewheel.domain.SaleLogResult.DISPENSE_FAILED -> "Entrega falhou"
    com.zuratouch.prizewheel.domain.SaleLogResult.REFUNDED -> "Reembolsado"
}
