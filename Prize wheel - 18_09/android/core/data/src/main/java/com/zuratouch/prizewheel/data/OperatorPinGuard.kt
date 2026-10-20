package com.zuratouch.prizewheel.data

import android.content.Context

class OperatorPinGuard(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isLockedOut(): Boolean = System.currentTimeMillis() < prefs.getLong(KEY_LOCKOUT_UNTIL, 0L)

    fun lockoutRemainingSeconds(): Long {
        val remainingMs = prefs.getLong(KEY_LOCKOUT_UNTIL, 0L) - System.currentTimeMillis()
        return if (remainingMs > 0) (remainingMs + 999) / 1000 else 0L
    }

    fun remainingAttempts(maxAttempts: Int = MAX_ATTEMPTS): Int {
        if (isLockedOut()) return 0
        return (maxAttempts - prefs.getInt(KEY_ATTEMPTS, 0)).coerceAtLeast(0)
    }

    /** Returns true when lockout was triggered by this failure. */
    fun recordFailure(maxAttempts: Int = MAX_ATTEMPTS, lockoutMs: Long = LOCKOUT_MS): Boolean {
        val attempts = prefs.getInt(KEY_ATTEMPTS, 0) + 1
        if (attempts >= maxAttempts) {
            prefs.edit()
                .putLong(KEY_LOCKOUT_UNTIL, System.currentTimeMillis() + lockoutMs)
                .putInt(KEY_ATTEMPTS, 0)
                .apply()
            return true
        }
        prefs.edit().putInt(KEY_ATTEMPTS, attempts).apply()
        return false
    }

    fun recordSuccess() {
        prefs.edit()
            .remove(KEY_ATTEMPTS)
            .remove(KEY_LOCKOUT_UNTIL)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "operator_pin_guard"
        private const val KEY_ATTEMPTS = "failed_attempts"
        private const val KEY_LOCKOUT_UNTIL = "lockout_until_ms"
        const val MAX_ATTEMPTS = 5
        const val LOCKOUT_MS = 5 * 60 * 1000L
    }
}
