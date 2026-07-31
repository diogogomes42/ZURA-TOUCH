package com.zuratouch.prizewheel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zuratouch.prizewheel.R
import com.zuratouch.prizewheel.hardware.HardwareStatus
import com.zuratouch.prizewheel.ui.theme.ZuraColors

@Composable
fun HardwareDegradedBanner(
    status: HardwareStatus.Degraded,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(
            R.string.hardware_degraded_banner,
            status.serialPath,
            status.reason,
        ),
        modifier = modifier
            .fillMaxWidth()
            .background(ZuraColors.AccentMuted.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        style = MaterialTheme.typography.bodySmall,
        color = ZuraColors.TextPrimary,
        textAlign = TextAlign.Center,
    )
}
