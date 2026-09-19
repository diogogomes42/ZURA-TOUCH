package com.zuratouch.feature.salecards.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.feature.salecards.R
import com.zuratouch.feature.salecards.theme.ZuraColors
import com.zuratouch.prizewheel.domain.JackpotDisplayState

@Composable
fun LegendaryJackpotBar(
    state: JackpotDisplayState,
    modifier: Modifier = Modifier,
) {
    if (!state.visible) return

    val pulse = rememberInfiniteTransition(label = "jackpotPulse")
    val glowAlpha by pulse.animateFloat(
        initialValue = if (state.isGuaranteeReady) 0.55f else 0.35f,
        targetValue = if (state.isGuaranteeReady) 1f else 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (state.isGuaranteeReady) 700 else 1400),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "jackpotGlow",
    )

    val borderColor = if (state.isGuaranteeReady) {
        ZuraColors.Accent.copy(alpha = glowAlpha)
    } else {
        ZuraColors.Accent.copy(alpha = 0.3f)
    }

    val title = if (state.isGuaranteeReady) {
        stringResource(R.string.jackpot_bar_ready_title)
    } else {
        stringResource(R.string.jackpot_bar_title)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(ZuraColors.WheelRing.copy(alpha = 0.75f))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = if (state.isGuaranteeReady) ZuraColors.Accent else Color.White.copy(alpha = 0.9f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 148.dp),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color.White.copy(alpha = 0.12f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(state.progress.coerceIn(0f, 1f))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                ZuraColors.AccentMuted,
                                ZuraColors.Accent,
                                Color(0xFFFFE08A),
                            ),
                        ),
                    ),
            )
        }
    }
}
