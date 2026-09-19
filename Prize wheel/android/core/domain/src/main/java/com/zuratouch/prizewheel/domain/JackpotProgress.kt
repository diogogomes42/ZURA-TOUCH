package com.zuratouch.prizewheel.domain

/** Customer-safe jackpot display — progress only, never exposes revenue or threshold amounts. */
data class JackpotDisplayState(
    val visible: Boolean = false,
    val progress: Float = 0f,
    val isGuaranteeReady: Boolean = false,
)

object JackpotProgress {
    fun displayStateFor(jackpot: JackpotState): JackpotDisplayState {
        if (!jackpot.enabled) {
            return JackpotDisplayState(visible = false)
        }
        val threshold = JackpotConfig.LEGENDARY_GUARANTEE_THRESHOLD_CENTS
        val ready = jackpot.accumulatedRevenueCents >= threshold
        val progress = if (threshold <= 0L) {
            0f
        } else {
            (jackpot.accumulatedRevenueCents.toFloat() / threshold.toFloat()).coerceIn(0f, 1f)
        }
        return JackpotDisplayState(
            visible = true,
            progress = if (ready) 1f else progress,
            isGuaranteeReady = ready,
        )
    }
}
