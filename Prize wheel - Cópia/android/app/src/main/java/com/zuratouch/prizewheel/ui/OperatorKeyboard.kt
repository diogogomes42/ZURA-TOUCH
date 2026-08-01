package com.zuratouch.prizewheel.ui

import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

object OperatorKeyboard {
    fun prepareForInput(activity: ComponentActivity) {
        WindowCompat.setDecorFitsSystemWindows(activity.window, true)
        activity.window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE,
        )
        WindowInsetsControllerCompat(activity.window, activity.window.decorView).apply {
            show(WindowInsetsCompat.Type.systemBars())
            show(WindowInsetsCompat.Type.ime())
        }
    }

    fun show(activity: ComponentActivity, focusedView: android.view.View?) {
        prepareForInput(activity)
        focusedView ?: return
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        focusedView.post {
            imm.showSoftInput(focusedView, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun restoreKioskMode(activity: ComponentActivity) {
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        WindowInsetsControllerCompat(activity.window, activity.window.decorView).apply {
            hide(WindowInsetsCompat.Type.ime())
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
