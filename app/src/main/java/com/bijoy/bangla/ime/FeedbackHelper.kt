package com.bijoy.bangla.ime

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import com.bijoy.bangla.BijoyApp

class FeedbackHelper(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun playKeyClick(view: View? = null, isSpecialKey: Boolean = false) {
        if (!BijoyApp.prefs.isSoundEnabled) return

        try {
            val fx = if (isSpecialKey) {
                AudioManager.FX_KEYPRESS_STANDARD
            } else {
                AudioManager.FX_KEY_CLICK
            }
            val volume = BijoyApp.prefs.soundVolume
            audioManager?.playSoundEffect(fx, volume)
        } catch (_: Exception) {}
    }

    fun performHaptic(view: View? = null) {
        if (!BijoyApp.prefs.isVibrationEnabled) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(VibrationEffect.createOneShot(15L, 120))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(15L)
            }
        } catch (_: Exception) {
            view?.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }
}
