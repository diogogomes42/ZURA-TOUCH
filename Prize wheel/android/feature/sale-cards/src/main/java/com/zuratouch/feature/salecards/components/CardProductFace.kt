package com.zuratouch.feature.salecards.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.feature.salecards.theme.ZuraColors

enum class CardFaceVariant {
    Standard,
    Hero,
    Spotlight,
}

@Composable
internal fun CardProductFace(
    productName: String,
    imageRes: Int?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 10.dp,
    imagePadding: Dp = 6.dp,
    categoryColor: Long? = null,
    variant: CardFaceVariant = CardFaceVariant.Standard,
) {
    val footerFraction = when (variant) {
        CardFaceVariant.Hero -> 0.38f
        CardFaceVariant.Spotlight -> 0.34f
        CardFaceVariant.Standard -> 0.32f
    }
    val nameSize = when (variant) {
        CardFaceVariant.Hero -> 9.sp
        CardFaceVariant.Spotlight -> 10.sp
        CardFaceVariant.Standard -> 8.sp
    }
    val nameLines = when (variant) {
        CardFaceVariant.Hero -> 4
        else -> 2
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color(0xFF0B1220))
            .border(1.dp, ZuraColors.Accent.copy(alpha = 0.22f), RoundedCornerShape(cornerRadius)),
    ) {
        if (imageRes != null) {
            SplitCardLayout(
                productName = productName,
                imageRes = imageRes,
                footerFraction = footerFraction,
                nameSize = nameSize,
                nameLines = nameLines,
                imagePadding = imagePadding,
                variant = variant,
            )
        } else {
            TextOnlyCardFace(
                productName = productName,
                categoryColor = categoryColor,
                nameSize = nameSize,
                nameLines = nameLines,
                variant = variant,
            )
        }
    }
}

@Composable
private fun SplitCardLayout(
    productName: String,
    imageRes: Int,
    footerFraction: Float,
    nameSize: TextUnit,
    nameLines: Int,
    imagePadding: Dp,
    variant: CardFaceVariant,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f - footerFraction)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF121C2E), Color(0xFF0E1628)),
                    ),
                )
                .padding(imagePadding),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = productName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }
        CardFooterPanel(
            productName = productName,
            nameSize = nameSize,
            nameLines = nameLines,
            variant = variant,
            modifier = Modifier
                .fillMaxWidth()
                .weight(footerFraction),
        )
    }
}

@Composable
private fun TextOnlyCardFace(
    productName: String,
    categoryColor: Long?,
    nameSize: TextUnit,
    nameLines: Int,
    variant: CardFaceVariant,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(categoryColor ?: 0xFF15213A).copy(alpha = 0.95f),
                        ZuraColors.CardBackBottom,
                    ),
                ),
            ),
    ) {
        Spacer(modifier = Modifier.weight(0.2f))
        CardFooterPanel(
            productName = productName,
            nameSize = nameSize,
            nameLines = nameLines,
            variant = variant,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f),
        )
    }
}

@Composable
private fun CardFooterPanel(
    productName: String,
    nameSize: TextUnit,
    nameLines: Int,
    variant: CardFaceVariant,
    modifier: Modifier = Modifier,
) {
    val accentLine = when (variant) {
        CardFaceVariant.Hero -> Color(0xFFFFD700)
        CardFaceVariant.Spotlight -> ZuraColors.Accent
        CardFaceVariant.Standard -> ZuraColors.Accent.copy(alpha = 0.55f)
    }
    Column(
        modifier = modifier
            .background(Color(0xFF0A101C))
            .border(
                width = 1.dp,
                color = accentLine.copy(alpha = 0.35f),
                shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp),
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = productName,
            color = Color.White,
            fontSize = nameSize,
            fontWeight = if (variant == CardFaceVariant.Hero) FontWeight.Bold else FontWeight.Black,
            textAlign = TextAlign.Center,
            maxLines = nameLines,
            overflow = TextOverflow.Ellipsis,
            lineHeight = nameSize * 1.15f,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
