package com.zuratouch.prizewheel.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zuratouch.prizewheel.AppInitState
import com.zuratouch.prizewheel.R
import com.zuratouch.prizewheel.ZuraTouchApp
import com.zuratouch.feature.salewheel.theme.ZuraColors

@Composable
fun AppRoot(app: ZuraTouchApp) {
    val initState by app.initState.collectAsState()
    when (val state = initState) {
        AppInitState.Loading -> AppInitLoadingScreen()
        is AppInitState.Failed -> AppInitErrorScreen(
            message = state.message,
            onRetry = app::retryInitialization,
        )
        AppInitState.Ready -> ExperienceHost(app = app)
    }
}

@Composable
private fun AppInitLoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZuraColors.BackgroundDark),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CircularProgressIndicator(color = ZuraColors.Accent)
            Text(
                text = stringResource(R.string.app_init_loading),
                style = MaterialTheme.typography.bodyLarge,
                color = ZuraColors.TextMuted,
            )
        }
    }
}

@Composable
private fun AppInitErrorScreen(
    message: String,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZuraColors.BackgroundDark)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.app_init_failed),
                style = MaterialTheme.typography.titleMedium,
                color = ZuraColors.Error,
                textAlign = TextAlign.Center,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = ZuraColors.TextMuted,
                textAlign = TextAlign.Center,
            )
            Button(onClick = onRetry) {
                Text(stringResource(R.string.app_init_retry))
            }
        }
    }
}
