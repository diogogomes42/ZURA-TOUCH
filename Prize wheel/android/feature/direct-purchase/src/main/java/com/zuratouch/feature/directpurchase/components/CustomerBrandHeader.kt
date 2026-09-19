package com.zuratouch.feature.directpurchase.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zuratouch.feature.directpurchase.R
import com.zuratouch.feature.directpurchase.theme.ZuraColors
import com.zuratouch.feature.salecards.R as CardsR
import androidx.compose.foundation.Image

@Composable
fun CustomerBrandHeader(
    onDirectPurchaseClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDirectPurchaseEntry: Boolean = true,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(CardsR.drawable.zura_touch_logo),
            contentDescription = stringResource(CardsR.string.brand_title),
            modifier = Modifier
                .widthIn(max = 140.dp)
                .heightIn(min = 56.dp, max = 80.dp),
            contentScale = ContentScale.Fit,
        )
        if (showDirectPurchaseEntry) {
            OutlinedButton(
                onClick = onDirectPurchaseClick,
                modifier = Modifier.align(Alignment.CenterEnd),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, ZuraColors.DirectPurchaseAccent.copy(alpha = 0.55f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = ZuraColors.DirectPurchaseAccent.copy(alpha = 0.12f),
                    contentColor = ZuraColors.DirectPurchaseAccent,
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = stringResource(R.string.direct_purchase_entry),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.2.sp,
                )
            }
        }
    }
}
