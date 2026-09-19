package com.zuratouch.feature.salewheel.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.feature.salewheel.theme.ZuraColors
import com.zuratouch.prizewheel.domain.PrizeTierCustomerInfo

@Composable
fun TierLevelBadge(
    tierId: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val tint = if (selected) ZuraColors.Accent else Color.White.copy(alpha = 0.88f)

    Surface(
        modifier = modifier.size(28.dp),
        shape = CircleShape,
        color = tint.copy(alpha = if (selected) 0.18f else 0.1f),
        border = BorderStroke(1.dp, tint.copy(alpha = if (selected) 0.55f else 0.28f)),
        shadowElevation = 0.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = PrizeTierCustomerInfo.badgeEmojiFor(tierId),
                fontSize = 14.sp,
            )
        }
    }
}
