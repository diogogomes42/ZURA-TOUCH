package com.zuratouch.prizewheel.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay

const val CUSTOMER_INACTIVITY_TIMEOUT_MS = 60_000L

fun Modifier.trackCustomerActivity(onActivity: () -> Unit): Modifier =
    pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                if (event.changes.any { it.pressed }) {
                    onActivity()
                }
            }
        }
    }

@Composable
fun CustomerInactivityTimeout(
    lastInteractionMs: Long,
    enabled: Boolean,
    onTimeout: () -> Unit,
) {
    LaunchedEffect(lastInteractionMs, enabled) {
        if (!enabled) return@LaunchedEffect
        delay(CUSTOMER_INACTIVITY_TIMEOUT_MS)
        onTimeout()
    }
}
