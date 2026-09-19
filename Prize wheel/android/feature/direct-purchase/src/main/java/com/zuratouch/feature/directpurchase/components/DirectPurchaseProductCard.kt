package com.zuratouch.feature.directpurchase.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.feature.directpurchase.R
import com.zuratouch.feature.directpurchase.theme.ZuraColors
import com.zuratouch.feature.salecards.CardProductImages
import com.zuratouch.feature.salecards.components.PrizeRarityChip
import com.zuratouch.prizewheel.domain.DirectPurchaseOffer
import java.util.Locale

@Composable
fun DirectPurchaseProductCard(
    offer: DirectPurchaseOffer,
    onBuyClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBuyButton: Boolean = true,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DirectPurchaseProductImage(
                productName = offer.productName,
                categoryColor = offer.categoryColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.05f),
            )
            Text(
                text = offer.productName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PrizeRarityChip(
                    label = offer.categoryLabel,
                    color = Color(offer.categoryColor),
                )
                Text(
                    text = formatPrice(offer.priceCents),
                    color = ZuraColors.Accent,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                )
            }
            if (showBuyButton) {
                Button(
                    onClick = onBuyClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ZuraColors.Accent,
                        contentColor = ZuraColors.BackgroundDark,
                    ),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        text = stringResource(R.string.direct_purchase_buy),
                        fontWeight = FontWeight.Black,
                    )
                }
            }
        }
    }
}

@Composable
private fun DirectPurchaseProductImage(
    productName: String,
    categoryColor: Long,
    modifier: Modifier = Modifier,
) {
    val imageRes = CardProductImages.drawableFor(productName)
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)),
        border = BorderStroke(1.5.dp, Color(categoryColor).copy(alpha = 0.55f)),
    ) {
        if (imageRes != null) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(imageRes),
                contentDescription = productName,
                modifier = Modifier.fillMaxWidth(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = productName,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

private fun formatPrice(cents: Long): String =
    String.format(Locale("pt", "PT"), "€%.2f", cents / 100.0)
