package com.alejandroestevemaza.gymtimerpro.feature.training.notifications

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.alejandroestevemaza.gymtimerpro.GymTimerProApplication
import com.alejandroestevemaza.gymtimerpro.core.model.EnergySavingMode
import com.alejandroestevemaza.gymtimerpro.core.util.AppForegroundState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Foreground service that owns the rest timer coroutine and sound playback.
 *
 * Keeping the timer here — rather than in a ViewModel — ensures it survives
 * tab navigation, app backgrounding, and screen lock. The service starts when
 * a rest timer begins and stops itself when the timer finishes or is cancelled.
 *
 * Communication with the ViewModel is exclusively via DataStore: the service
 * writes [TrainingSessionState] updates (remaining seconds, timerDidFinish, …)
 * and the ViewModel observes those changes for UI rendering.
 */
class RestTimerService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var timerJob: Job? = null

    private val appContainer get() = (application as GymTimerProApplication).appContainer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> handleStart(intent)
            ACTION_CANCEL -> stopForegroundAndSelf()
        }
        return START_NOT_STICKY
    }

    private fun handleStart(intent: Intent) {
        val endEpochMillis = intent.getLongExtra(EXTRA_END_EPOCH_MILLIS, 0L)
        val currentSet = intent.getIntExtra(EXTRA_CURRENT_SET, 1)
        val totalSets = intent.getIntExtra(EXTRA_TOTAL_SETS, 1)
        val energySavingMode = EnergySavingMode.entries.getOrElse(
            intent.getIntExtra(EXTRA_ENERGY_SAVING, 0)
        ) { EnergySavingMode.Off }

        // A minimal placeholder keeps the foreground service alive. The
        // RestNotificationCoordinator immediately replaces it with the full
        // live-timer notification (same channel + ID, no tag).
        val placeholder = NotificationCompat.Builder(this, CHANNEL_REST_LIVE)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .build()
        startForeground(NOTIFICATION_ID, placeholder)

        timerJob?.cancel()
        timerJob = serviceScope.launch {
            runTimer(endEpochMillis, currentSet, totalSets, energySavingMode)
        }
    }

    private suspend fun runTimer(
        endEpochMillis: Long,
        currentSet: Int,
        totalSets: Int,
        energySavingMode: EnergySavingMode,
    ) {
        val coordinator = appContainer.restNotificationCoordinator
        val soundPlayer = appContainer.restFinishedSoundPlayer
        val repository = appContainer.trainingSessionRepository

        var lastPersistedSeconds: Int? = null
        while (true) {
            val remainingMillis = (endEpochMillis - System.currentTimeMillis()).coerceAtLeast(0L)
            val remainingSeconds = ((remainingMillis + 999L) / 1_000L).toInt()

            if (remainingMillis == 0L) {
                // Detach live notification before coordinator cancels it, so Android
                // does not flash a removal animation for the foreground notification.
                ServiceCompat.stopForeground(this@RestTimerService, ServiceCompat.STOP_FOREGROUND_DETACH)
                coordinator.notifyRestFinished(currentSet, totalSets)
                if (AppForegroundState.isForeground()) {
                    soundPlayer.play(energySavingMode)
                }
                repository.updateSession { current ->
                    current.copy(
                        timerIsRunning = false,
                        timerEndEpochMillis = null,
                        timerRemainingSeconds = 0,
                        timerDidFinish = true,
                    )
                }
                stopSelf()
                break
            }

            if (lastPersistedSeconds != remainingSeconds) {
                lastPersistedSeconds = remainingSeconds
                repository.updateSession { current ->
                    current.copy(
                        timerRemainingSeconds = remainingSeconds,
                        timerDidFinish = false,
                    )
                }
            }

            delay(250)
        }
    }

    private fun stopForegroundAndSelf() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        timerJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START = "gymtimerpro.timer.ACTION_START"
        const val ACTION_CANCEL = "gymtimerpro.timer.ACTION_CANCEL"

        private const val EXTRA_END_EPOCH_MILLIS = "gymtimerpro.timer.END_EPOCH_MILLIS"
        private const val EXTRA_CURRENT_SET = "gymtimerpro.timer.CURRENT_SET"
        private const val EXTRA_TOTAL_SETS = "gymtimerpro.timer.TOTAL_SETS"
        private const val EXTRA_ENERGY_SAVING = "gymtimerpro.timer.ENERGY_SAVING"

        // Must stay in sync with AndroidRestNotificationCoordinator.NOTIFICATION_ID_REST_LIVE
        // so that the coordinator's notify() updates the foreground service notification.
        const val NOTIFICATION_ID = 1_101
        private const val CHANNEL_REST_LIVE = "rest.live"

        fun startIntent(
            context: Context,
            endEpochMillis: Long,
            currentSet: Int,
            totalSets: Int,
            energySavingMode: EnergySavingMode,
        ): Intent = Intent(context, RestTimerService::class.java).apply {
            action = ACTION_START
            putExtra(EXTRA_END_EPOCH_MILLIS, endEpochMillis)
            putExtra(EXTRA_CURRENT_SET, currentSet)
            putExtra(EXTRA_TOTAL_SETS, totalSets)
            putExtra(EXTRA_ENERGY_SAVING, energySavingMode.ordinal)
        }

        fun cancelIntent(context: Context): Intent =
            Intent(context, RestTimerService::class.java).apply {
                action = ACTION_CANCEL
            }
    }
}
