package com.zuratouch.operator

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import com.zuratouch.operator.ui.OperatorKeyboard
import com.zuratouch.operator.ui.OperatorRoot
import com.zuratouch.operator.ui.theme.ZuraColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        OperatorKeyboard.prepareForInput(this)

        val app = application as OperatorApp
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
                OperatorRoot(app = app)
            }
        }
    }
}
