package com.zuratouch.prizewheel.feedback

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

interface FeedbackController {
    fun setSoundEnabled(enabled: Boolean)
    fun buttonTap()
    fun paymentConfirmed()
    fun spinStart()
    fun spinTick()
    fun win()
    fun error()
}

class FeedbackManager(context: Context) : FeedbackController {
    private val appContext = context.applicationContext
    private var soundEnabled = true

    override fun setSoundEnabled(enabled: Boolean) {
        soundEnabled = enabled
    }

    override fun buttonTap() {
        vibrate(30)
        playTone(ToneGenerator.TONE_PROP_BEEP, 40)
    }

    override fun paymentConfirmed() {
        vibrate(50)
        playTone(ToneGenerator.TONE_CDMA_CONFIRM, 80)
    }

    override fun spinStart() {
        playTone(ToneGenerator.TONE_PROP_ACK, 60)
    }

    override fun spinTick() {
        playTone(ToneGenerator.TONE_PROP_BEEP2, 25)
    }

    override fun win() {
        vibrate(140)
        playTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 120)
        playTone(ToneGenerator.TONE_CDMA_CONFIRM, 100)
    }

    override fun error() {
        vibrate(200)
        playTone(ToneGenerator.TONE_CDMA_ABBR_REORDER, 180)
    }

    private fun playTone(tone: Int, durationMs: Int) {
        if (!soundEnabled) return
        runCatching {
            val generator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 70)
            generator.startTone(tone, durationMs)
            generator.release()
        }
    }

    private fun vibrate(durationMs: Long) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = appContext.getSystemService(VibratorManager::class.java)
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            appContext.getSystemService(Vibrator::class.java)
        } ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }
}
