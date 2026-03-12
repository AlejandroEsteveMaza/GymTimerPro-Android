package com.alejandroestevemaza.gymtimerpro.feature.training.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alejandroestevemaza.gymtimerpro.core.model.TrainingDefaults
import com.alejandroestevemaza.gymtimerpro.data.preferences.DataStoreTrainingSessionRepository
import kotlinx.coroutines.runBlocking

class RestTimerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action != AndroidRestNotificationCoordinator.ACTION_REST_END_ALARM) {
            return
        }

        val currentSet = intent.getIntExtra(
            AndroidRestNotificationCoordinator.EXTRA_CURRENT_SET,
            TrainingDefaults.currentSet,
        )
        val totalSets = intent.getIntExtra(
            AndroidRestNotificationCoordinator.EXTRA_TOTAL_SETS,
            TrainingDefaults.totalSets,
        )

        val coordinator = AndroidRestNotificationCoordinator(context.applicationContext)
        coordinator.onRestEndAlarmFired(
            currentSet = currentSet,
            totalSets = totalSets,
        )

        // Keep persisted timer state coherent when rest finishes outside an active UI process.
        runBlocking {
            DataStoreTrainingSessionRepository(context.applicationContext).updateSession { session ->
                if (!session.timerIsRunning) {
                    session
                } else {
                    session.copy(
                        timerIsRunning = false,
                        timerEndEpochMillis = null,
                        timerRemainingSeconds = 0,
                        timerDidFinish = true,
                    )
                }
            }
        }
    }
}
