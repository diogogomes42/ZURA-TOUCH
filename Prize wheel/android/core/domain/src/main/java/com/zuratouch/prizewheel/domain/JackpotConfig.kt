package com.zuratouch.prizewheel.domain

/** Internal legendary guarantee ("pity") configuration — never expose values in customer UI. */
data class JackpotState(
    val accumulatedRevenueCents: Long = 0L,
    val enabled: Boolean = true,
)

object JackpotConfig {
    /** Revenue accumulated since the last Legendary before guarantee kicks in (€6_000). */
    const val LEGENDARY_GUARANTEE_THRESHOLD_CENTS: Long = 600_000L

    val DEFAULT = JackpotState()
}
