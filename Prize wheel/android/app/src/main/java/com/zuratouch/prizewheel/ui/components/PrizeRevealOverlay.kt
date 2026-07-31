package com.zuratouch.prizewheel.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.prizewheel.R
import com.zuratouch.prizewheel.ui.theme.ZuraColors

@Composable
fun PrizeRevealOverlay(
    prizeName: String,
    visible: Boolean,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + scaleIn(initialScale = 0.8f, animationSpec = tween(600)),
        modifier = modifier.fillMaxSize(),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ConfettiEffect(active = visible)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCC030712)),
                contentAlignment = Alignment.Center,
            ) {
                val glowTransition = rememberInfiniteTransition(label = "glow")
                val glowAlpha by glowTransition.animateFloat(
                    initialValue = 0.35f,
                    targetValue = 0.75f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(900),
                        repeatMode = RepeatMode.Reverse,
                    ),
                    label = "glowAlpha",
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    ZuraColors.Accent.copy(alpha = glowAlpha * 0.35f),
                                    Color(0xFF15213A),
                                ),
                            ),
                        )
                        .padding(horizontal = 28.dp, vertical = 36.dp),
                ) {
                    Text(
                        text = stringResource(R.string.prize_congrats),
                        color = ZuraColors.Accent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                    )
                    Text(
                        text = prizeName,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 8.dp)
                            .graphicsLayer(scaleX = 1f + glowAlpha * 0.05f, scaleY = 1f + glowAlpha * 0.05f),
                    )
                    Text(
                        text = stringResource(R.string.prize_subtitle),
                        color = ZuraColors.TextMuted,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp),
                    )
                    Button(
                        onClick = onFinish,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ZuraColors.Accent,
                            contentColor = ZuraColors.BackgroundDark,
                        ),
                        shape = RoundedCornerShape(13.dp),
                    ) {
                        Text(stringResource(R.string.finish_cta), fontWeight = FontWeight.Black)
                    }
                    Text(
                        text = stringResource(R.string.auto_return_hint),
                        color = ZuraColors.TextFooter,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
            }
        }
    }
}
