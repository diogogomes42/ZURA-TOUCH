package com.zuratouch.prizewheel

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import com.zuratouch.prizewheel.ui.AppRoot
import com.zuratouch.prizewheel.ui.OperatorKeyboard
import com.zuratouch.prizewheel.ui.theme.ZuraColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        OperatorKeyboard.restoreKioskMode(this)

        val app = application as ZuraTouchApp
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = ZuraColors.BackgroundDark,
                    surface = ZuraColors.WheelRing,
                    onBackground = ZuraColors.TextPrimary,
                    onSurface = ZuraColors.TextPrimary,
                    primary = ZuraColors.Accent,
                    onPrimary = ZuraColors.BackgroundDark,
                ),
            ) {
                AppRoot(
                    app = app,
                    onOperatorModeChanged = { operatorOpen ->
                        if (operatorOpen) OperatorKeyboard.prepareForInput(this) else OperatorKeyboard.restoreKioskMode(this)
                    },
                )
            }
        }
    }
}
