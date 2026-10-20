package com.zuratouch.operator.ui

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zuratouch.operator.AppInitState
import com.zuratouch.operator.OperatorApp
import com.zuratouch.operator.R
import com.zuratouch.operator.ui.theme.ZuraColors

@Composable
fun OperatorRoot(app: OperatorApp) {
    val initState by app.initState.collectAsState()
    when (val state = initState) {
        AppInitState.Loading -> InitLoadingScreen()
        is AppInitState.Failed -> InitErrorScreen(
            message = state.message,
            onRetry = app::retryInitialization,
        )
        AppInitState.Ready -> {
            val viewModel: OperatorViewModel = viewModel(factory = OperatorViewModelFactory(app))
            OperatorScreen(viewModel = viewModel)
        }
    }
}

@Composable
private fun InitLoadingScreen() {
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
private fun InitErrorScreen(message: String, onRetry: () -> Unit) {
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
