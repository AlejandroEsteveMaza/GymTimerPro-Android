package com.alejandroestevemaza.gymtimerpro.feature.training.notifications

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.AudioFocusRequest
import android.media.Ringtone
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.alejandroestevemaza.gymtimerpro.BuildConfig
import com.alejandroestevemaza.gymtimerpro.core.model.EnergySavingMode

/**
 * Plays the rest-finished feedback sound using the system default notification tone.
 *
 * To customize the sound, replace the URI in [resolveSoundUri] with a custom asset
 * (e.g. a raw resource) or switch to TYPE_ALARM if a stronger alert is required.
 */
class RestFinishedSoundPlayer(
    context: Context,
) {
    private val appContext = context.applicationContext
    private val handler = Handler(Looper.getMainLooper())
    private var activeRingtone: Ringtone? = null
    private var toneGenerator: ToneGenerator? = null
    private var audioManager: AudioManager? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    private var stopRunnable: Runnable? = null
    private var lastPlayElapsedMillis: Long = 0L

    fun play(energySavingMode: EnergySavingMode) {
        val now = SystemClock.elapsedRealtime()
        if (now - lastPlayElapsedMillis < MIN_REPLAY_WINDOW_MILLIS) {
            log("Skipping rest-finished sound (debounced).")
            return
        }
        lastPlayElapsedMillis = now

        val uri = resolveSoundUri()
        val ringtone = uri?.let { RingtoneManager.getRingtone(appContext, it) }
        if (ringtone == null) {
            log("Failed to resolve ringtone for rest-finished.")
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ringtone.audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        }

        stopRunnable?.let { handler.removeCallbacks(it) }
        activeRingtone?.stop()
        activeRingtone = ringtone

        requestAudioFocus()

        var played = false
        if (ringtone != null) {
            runCatching {
                ringtone.play()
                played = true
            }.onFailure { error ->
                log("Rest-finished ringtone failed: ${error.message}")
            }
        }
        if (!played) {
            playFallbackTone()
        }

        if (!isEnergySavingActive(energySavingMode)) {
            triggerHaptic()
        }

        val stopTask = Runnable {
            ringtone?.stop()
            if (activeRingtone === ringtone) {
                activeRingtone = null
            }
            stopFallbackTone()
            abandonAudioFocus()
        }
        stopRunnable = stopTask
        handler.postDelayed(stopTask, RELEASE_DELAY_MILLIS)
    }

    fun release() {
        stopRunnable?.let { handler.removeCallbacks(it) }
        stopRunnable = null
        activeRingtone?.stop()
        activeRingtone = null
        stopFallbackTone()
        abandonAudioFocus()
    }

    private fun resolveSoundUri(): android.net.Uri? {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    }

    private fun isEnergySavingActive(mode: EnergySavingMode): Boolean = when (mode) {
        EnergySavingMode.On -> true
        EnergySavingMode.Off -> false
        EnergySavingMode.Automatic -> isBatteryLow()
    }

    private fun isBatteryLow(): Boolean {
        val intent = appContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: return false
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        return level >= 0 && scale > 0 && (level * 100 / scale) < BATTERY_LOW_THRESHOLD
    }

    private fun triggerHaptic() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            appContext.getSystemService(VibratorManager::class.java)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
        if (vibrator == null || !vibrator.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    HAPTIC_DURATION_MILLIS,
                    VibrationEffect.DEFAULT_AMPLITUDE,
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(HAPTIC_DURATION_MILLIS)
        }
    }

    private fun playFallbackTone() {
        if (toneGenerator == null) {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME)
        }
        toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, FALLBACK_TONE_DURATION_MILLIS.toInt())
    }

    private fun stopFallbackTone() {
        toneGenerator?.stopTone()
        toneGenerator?.release()
        toneGenerator = null
    }

    private fun requestAudioFocus() {
        val manager = audioManager ?: appContext.getSystemService(AudioManager::class.java).also {
            audioManager = it
        } ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setOnAudioFocusChangeListener { }
                .build()
            audioFocusRequest = request
            manager.requestAudioFocus(request)
        } else {
            @Suppress("DEPRECATION")
            manager.requestAudioFocus(
                null,
                AudioManager.STREAM_ALARM,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK,
            )
        }
    }

    private fun abandonAudioFocus() {
        val manager = audioManager ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { manager.abandonAudioFocusRequest(it) }
            audioFocusRequest = null
        } else {
            @Suppress("DEPRECATION")
            manager.abandonAudioFocus(null)
        }
    }

    private fun log(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message)
        }
    }

    private companion object {
        private const val TAG = "RestFinishedSound"
        private const val RELEASE_DELAY_MILLIS = 2_000L
        private const val MIN_REPLAY_WINDOW_MILLIS = 1_000L
        private const val HAPTIC_DURATION_MILLIS = 80L
        private const val FALLBACK_TONE_DURATION_MILLIS = 220L
        private const val BATTERY_LOW_THRESHOLD = 20
    }
}
