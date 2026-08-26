package com.zuratouch.feature.salecards.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.feature.salecards.theme.ZuraColors

@Composable
internal fun CardProductFace(
    productName: String,
    imageRes: Int?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 10.dp,
    imagePadding: Dp = 6.dp,
    categoryColor: Long? = null,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .border(1.dp, ZuraColors.Accent.copy(alpha = 0.28f), RoundedCornerShape(cornerRadius)),
    ) {
        if (imageRes != null) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = productName,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 22.dp)
                    .padding(imagePadding),
                contentScale = ContentScale.Fit,
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xE6101828)),
                        ),
                    )
                    .padding(horizontal = 4.dp, vertical = 5.dp),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Text(
                    text = productName,
                    color = Color.White,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 9.sp,
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(categoryColor ?: 0xFF15213A).copy(alpha = 0.9f),
                                ZuraColors.CardBackBottom,
                            ),
                        ),
                    )
                    .padding(horizontal = 6.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = productName,
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 11.sp,
                )
            }
        }
    }
}
