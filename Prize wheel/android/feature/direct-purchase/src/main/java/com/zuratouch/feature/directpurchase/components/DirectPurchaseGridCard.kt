package com.zuratouch.feature.directpurchase.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.feature.directpurchase.R
import com.zuratouch.feature.directpurchase.theme.ZuraColors
import com.zuratouch.feature.salecards.CardProductImages
import com.zuratouch.prizewheel.domain.DirectPurchaseOffer
import java.util.Locale

@Composable
fun DirectPurchaseGridCard(
    offer: DirectPurchaseOffer,
    onBuyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp)),
            ) {
                GridCardImage(
                    productName = offer.productName,
                    categoryColor = offer.categoryColor,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)),
                            ),
                        )
                        .padding(horizontal = 4.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = formatPrice(offer.priceCents),
                        color = ZuraColors.Accent,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp,
                        maxLines = 1,
                        modifier = Modifier.align(Alignment.CenterEnd),
                    )
                }
                CategoryBadge(
                    label = offer.categoryLabel,
                    color = Color(offer.categoryColor),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(3.dp),
                )
            }
            Text(
                text = offer.productName,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 8.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 9.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 3.dp, bottom = 2.dp),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(ZuraColors.Accent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = Color.White.copy(alpha = 0.25f)),
                        onClick = onBuyClick,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.direct_purchase_buy),
                    color = ZuraColors.BackgroundDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun GridCardImage(
    productName: String,
    categoryColor: Long,
    modifier: Modifier = Modifier,
) {
    val imageRes = CardProductImages.drawableFor(productName)
    Box(
        modifier = modifier.background(Color(0xFF0B1220)),
    ) {
        if (imageRes != null) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = productName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = productName,
                color = Color.White.copy(alpha = 0.75f),
                fontWeight = FontWeight.Medium,
                fontSize = 8.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(4.dp),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(categoryColor).copy(alpha = 0.08f)),
        )
    }
}

@Composable
private fun CategoryBadge(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        color = color,
        fontSize = 7.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .background(color.copy(alpha = 0.22f), RoundedCornerShape(3.dp))
            .padding(horizontal = 4.dp, vertical = 1.dp),
    )
}

private fun formatPrice(cents: Long): String =
    String.format(Locale("pt", "PT"), "€%.2f", cents / 100.0)
