package com.zuratouch.feature.salewheel.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.feature.salewheel.theme.ZuraColors
import java.util.Locale

@Composable
fun PlayCtaWithTierPicker(
    actionLabel: String,
    selectedTierId: String,
    priceCents: Long,
    enabled: Boolean,
    onPlay: () -> Unit,
    onPriceClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pulse = rememberInfiniteTransition(label = "ctaPulse")
    val scale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = if (enabled) 1.03f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ctaScale",
    )
    val containerColor = if (enabled) ZuraColors.Accent else ZuraColors.Accent.copy(alpha = 0.5f)
    val contentColor = ZuraColors.BackgroundDark.copy(alpha = if (enabled) 1f else 0.55f)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(8.dp, RoundedCornerShape(13.dp))
            .clip(RoundedCornerShape(13.dp))
            .background(containerColor)
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable(
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = ZuraColors.BackgroundDark.copy(alpha = 0.2f)),
                    onClick = onPlay,
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = actionLabel,
                color = contentColor,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                letterSpacing = 0.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight(0.5f),
            color = ZuraColors.BackgroundDark.copy(alpha = 0.16f),
            thickness = 1.dp,
        )
        TierPriceChip(
            priceCents = priceCents,
            contentColor = contentColor,
            onClick = onPriceClick,
            modifier = Modifier.padding(start = 8.dp, end = 10.dp),
        )
    }
}

@Composable
private fun TierPriceChip(
    priceCents: Long,
    contentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = ZuraColors.BackgroundDark.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, ZuraColors.BackgroundDark.copy(alpha = 0.22f)),
        shadowElevation = 0.dp,
    ) {
        Text(
            text = "${formatPrice(priceCents)} ▾",
            color = contentColor,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
        )
    }
}

private fun formatPrice(cents: Long): String =
    String.format(Locale("pt", "PT"), "€%.2f", cents / 100.0)
