package com.alejandroestevemaza.gymtimerpro.feature.training.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.alejandroestevemaza.gymtimerpro.MainActivity
import com.alejandroestevemaza.gymtimerpro.R
import com.alejandroestevemaza.gymtimerpro.core.model.TrainingSessionState
import com.alejandroestevemaza.gymtimerpro.core.util.AppForegroundState

interface RestNotificationCoordinator {
    fun syncRestState(session: TrainingSessionState)
    fun notifyRestFinished(currentSet: Int, totalSets: Int)
    fun clearAll()
}

class AndroidRestNotificationCoordinator(
    context: Context,
) : RestNotificationCoordinator {
    private val appContext = context.applicationContext
    private val notificationManager = NotificationManagerCompat.from(appContext)
    private val alarmManager = appContext.getSystemService(AlarmManager::class.java)

    override fun syncRestState(session: TrainingSessionState) {
        val endEpochMillis = session.timerEndEpochMillis
        if (!session.timerIsRunning || endEpochMillis == null) {
            cancelPendingRestEndAlarm()
            cancelLiveNotification()
            return
        }

        ensureChannels()
        postOrUpdateLiveRestNotification(
            currentSet = session.currentSet,
            totalSets = session.totalSets,
            endEpochMillis = endEpochMillis,
        )
        scheduleRestEndAlarm(
            currentSet = session.currentSet,
            totalSets = session.totalSets,
            endEpochMillis = endEpochMillis,
        )
    }

    override fun notifyRestFinished(
        currentSet: Int,
        totalSets: Int,
    ) {
        ensureChannels()
        cancelPendingRestEndAlarm()
        cancelLiveNotification()
        cancelRestEndNotification()
        if (AppForegroundState.isForeground()) {
            return
        }
        postRestFinishedNotification(
            currentSet = currentSet,
            totalSets = totalSets,
        )
    }

    override fun clearAll() {
        cancelPendingRestEndAlarm()
        cancelLiveNotification()
        cancelRestEndNotification()
    }

    fun onRestEndAlarmFired(
        currentSet: Int,
        totalSets: Int,
    ) {
        notifyRestFinished(
            currentSet = currentSet,
            totalSets = totalSets,
        )
    }

    private fun postOrUpdateLiveRestNotification(
        currentSet: Int,
        totalSets: Int,
        endEpochMillis: Long,
    ) {
        if (!canPostNotifications()) return

        val safeTotalSets = totalSets.coerceAtLeast(1)
        val safeCurrentSet = currentSet.coerceIn(0, safeTotalSets)
        val remainingMillis = (endEpochMillis - System.currentTimeMillis()).coerceAtLeast(0L)
        val baseElapsed = SystemClock.elapsedRealtime() + remainingMillis
        val contentView = buildLiveRestContentView(
            baseElapsed = baseElapsed,
            currentSet = safeCurrentSet,
            totalSets = safeTotalSets,
        )
        val bigContentView = buildLiveRestContentView(
            baseElapsed = baseElapsed,
            currentSet = safeCurrentSet,
            totalSets = safeTotalSets,
        )

        val notification = NotificationCompat.Builder(appContext, CHANNEL_REST_LIVE)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setCustomContentView(contentView)
            .setCustomBigContentView(bigContentView)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setColor(ContextCompat.getColor(appContext, R.color.gymtimer_seed_primary))
            .setColorized(true)
            .setUsesChronometer(true)
            .setChronometerCountDown(true)
            .setWhen(endEpochMillis)
            .setShowWhen(true)
            .setProgress(safeTotalSets, safeCurrentSet, false)
            .setContentIntent(buildOpenAppPendingIntent())
            .build()

        notificationManager.notify(TAG_REST_LIVE, ID_REST_LIVE, notification)
    }

    private fun postRestFinishedNotification(
        currentSet: Int,
        totalSets: Int,
    ) {
        if (!canPostNotifications()) return

        val notification = NotificationCompat.Builder(appContext, CHANNEL_REST_END)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(appContext.getString(R.string.notification_rest_finished_title))
            .setContentText(
                appContext.getString(
                    R.string.notification_rest_finished_body_format,
                    currentSet,
                    totalSets,
                )
            )
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setDefaults(android.app.Notification.DEFAULT_SOUND)
            .setContentIntent(buildOpenAppPendingIntent())
            .build()

        notificationManager.notify(TAG_REST_END, ID_REST_END, notification)
    }

    private fun scheduleRestEndAlarm(
        currentSet: Int,
        totalSets: Int,
        endEpochMillis: Long,
    ) {
        cancelPendingRestEndAlarm()
        val pendingIntent = buildRestEndAlarmPendingIntent(
            currentSet = currentSet,
            totalSets = totalSets,
            flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        ) ?: return
        val manager = alarmManager ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && manager.canScheduleExactAlarms()) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endEpochMillis, pendingIntent)
        } else {
            manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endEpochMillis, pendingIntent)
        }
    }

    private fun cancelPendingRestEndAlarm() {
        val pendingIntent = buildRestEndAlarmPendingIntent(
            currentSet = 0,
            totalSets = 0,
            flags = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        ) ?: return
        alarmManager?.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun cancelLiveNotification() {
        notificationManager.cancel(TAG_REST_LIVE, ID_REST_LIVE)
    }

    private fun cancelRestEndNotification() {
        notificationManager.cancel(TAG_REST_END, ID_REST_END)
    }

    private fun buildRestEndAlarmPendingIntent(
        currentSet: Int,
        totalSets: Int,
        flags: Int,
    ): PendingIntent? {
        val intent = Intent(appContext, RestTimerAlarmReceiver::class.java).apply {
            action = ACTION_REST_END_ALARM
            putExtra(EXTRA_CURRENT_SET, currentSet)
            putExtra(EXTRA_TOTAL_SETS, totalSets)
        }
        return PendingIntent.getBroadcast(
            appContext,
            REQUEST_CODE_REST_END_ALARM,
            intent,
            flags,
        )
    }

    private fun buildOpenAppPendingIntent(): PendingIntent {
        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            appContext,
            REQUEST_CODE_OPEN_APP,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun ensureChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = appContext.getSystemService(NotificationManager::class.java) ?: return

        if (manager.getNotificationChannel(CHANNEL_REST_LIVE) == null) {
            val liveChannel = NotificationChannel(
                CHANNEL_REST_LIVE,
                appContext.getString(R.string.notification_rest_finished_title),
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                setShowBadge(false)
                description = appContext.getString(R.string.notification_rest_finished_title)
            }
            manager.createNotificationChannel(liveChannel)
        }

        val existingEndChannel = manager.getNotificationChannel(CHANNEL_REST_END)
        if (existingEndChannel != null && shouldRecreateEndChannel(existingEndChannel)) {
            manager.deleteNotificationChannel(CHANNEL_REST_END)
        }

        if (manager.getNotificationChannel(CHANNEL_REST_END) == null) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            val endChannel = NotificationChannel(
                CHANNEL_REST_END,
                appContext.getString(R.string.notification_rest_finished_title),
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                enableVibration(false)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    audioAttributes,
                )
                description = appContext.getString(R.string.notification_rest_finished_title)
            }
            manager.createNotificationChannel(endChannel)
        }
    }

    private fun shouldRecreateEndChannel(channel: NotificationChannel): Boolean {
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_END_CHANNEL_MIGRATED, false)) return false
        val needsSound = channel.sound == null
        val needsImportance = channel.importance < NotificationManager.IMPORTANCE_HIGH
        val needsUsage = channel.audioAttributes?.usage != AudioAttributes.USAGE_ALARM
        return if (needsSound || needsImportance || needsUsage) {
            prefs.edit().putBoolean(KEY_END_CHANNEL_MIGRATED, true).apply()
            true
        } else {
            prefs.edit().putBoolean(KEY_END_CHANNEL_MIGRATED, true).apply()
            false
        }
    }

    private fun canPostNotifications(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
    }

    private fun buildLiveRestContentView(
        baseElapsed: Long,
        currentSet: Int,
        totalSets: Int,
    ): RemoteViews {
        val setProgressText = appContext.getString(
            R.string.live_activity_set_progress_expanded_format,
            currentSet,
            totalSets,
        )
        return RemoteViews(appContext.packageName, R.layout.notification_rest_timer).apply {
            setTextViewText(R.id.rest_timer_title, appContext.getString(R.string.notification_rest_live_title))
            setTextViewText(R.id.rest_timer_app_name, appContext.getString(R.string.app_name))
            setTextViewText(R.id.rest_timer_progress, setProgressText)
            setChronometer(R.id.rest_timer_chrono, baseElapsed, null, true)
            setChronometerCountDown(R.id.rest_timer_chrono, true)
            setProgressBar(R.id.rest_timer_series_progress, totalSets, currentSet, false)
        }
    }

    companion object {
        const val ACTION_REST_END_ALARM = "com.alejandroestevemaza.gymtimerpro.rest.END_ALARM"
        const val EXTRA_CURRENT_SET = "extra.current_set"
        const val EXTRA_TOTAL_SETS = "extra.total_sets"

        private const val CHANNEL_REST_LIVE = "rest.live"
        private const val CHANNEL_REST_END = "rest.end"
        private const val REQUEST_CODE_REST_END_ALARM = 11_101
        private const val REQUEST_CODE_OPEN_APP = 11_102
        private const val ID_REST_LIVE = 1_101
        private const val ID_REST_END = 1_102
        private const val TAG_REST_LIVE = "restTimer.live"
        private const val TAG_REST_END = "restTimer.end"
        private const val PREFS_NAME = "rest_notifications"
        private const val KEY_END_CHANNEL_MIGRATED = "rest_end_channel_migrated"
    }
}
