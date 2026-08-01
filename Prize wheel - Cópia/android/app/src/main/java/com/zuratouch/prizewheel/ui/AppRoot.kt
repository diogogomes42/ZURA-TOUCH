package com.zuratouch.prizewheel.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zuratouch.prizewheel.ZuraTouchApp

enum class AppScreen { Game, Operator }

@Composable
fun AppRoot(
    app: ZuraTouchApp,
    onOperatorModeChanged: (Boolean) -> Unit = {},
) {
    var screen by rememberSaveable { mutableStateOf(AppScreen.Game) }
    androidx.compose.runtime.LaunchedEffect(screen) {
        onOperatorModeChanged(screen == AppScreen.Operator)
    }
    when (screen) {
        AppScreen.Game -> {
            val viewModel: PrizeWheelViewModel = viewModel(factory = PrizeWheelViewModelFactory(app))
            PrizeWheelScreen(
                viewModel = viewModel,
                onOpenOperator = { screen = AppScreen.Operator },
            )
        }
        AppScreen.Operator -> {
            val viewModel: OperatorViewModel = viewModel(factory = OperatorViewModelFactory(app))
            OperatorScreen(
                viewModel = viewModel,
                onBack = { screen = AppScreen.Game },
            )
        }
    }
}
