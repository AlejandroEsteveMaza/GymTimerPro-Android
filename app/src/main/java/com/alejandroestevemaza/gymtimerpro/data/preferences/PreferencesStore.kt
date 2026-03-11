package com.alejandroestevemaza.gymtimerpro.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

private const val GYM_TIMER_PRO_PREFERENCES = "gymtimerpro_preferences"

val Context.gymTimerProDataStore by preferencesDataStore(name = GYM_TIMER_PRO_PREFERENCES)

object PreferencesKeys {
    val weightUnitPreference = intPreferencesKey("weight.unit_preference")
    val timerDisplayFormat = intPreferencesKey("timer.display_format")
    val trainingMaxSets = intPreferencesKey("training.max_sets")
    val trainingRestIncrement = intPreferencesKey("training.rest_increment")
    val energySavingMode = intPreferencesKey("energy_saving.mode")
    val cachedIsPro = booleanPreferencesKey("purchase.cachedIsPro")
    val trainingTotalSets = intPreferencesKey("training.total_sets")
    val trainingRestSeconds = intPreferencesKey("training.rest_seconds")
    val trainingCurrentSet = intPreferencesKey("training.current_set")
    val trainingCompleted = booleanPreferencesKey("training.completed")
    val trainingAppliedRoutineId = stringPreferencesKey("training.applied_routine_id")
    val trainingAppliedRoutineName = stringPreferencesKey("training.applied_routine_name")
    val trainingAppliedRoutineReps = intPreferencesKey("training.applied_routine_reps")
    val trainingAppliedClassificationId = stringPreferencesKey("training.applied_classification_id")
    val trainingAppliedClassificationName = stringPreferencesKey("training.applied_classification_name")
    val timerIsRunning = booleanPreferencesKey("timer.is_running")
    val timerEndEpochMillis = longPreferencesKey("timer.end_epoch_millis")
    val timerRemainingSeconds = intPreferencesKey("timer.remaining_seconds")
    val timerDidFinish = booleanPreferencesKey("timer.did_finish")
    val trainingCompletedAtEpochMillis = longPreferencesKey("training.completed_at_epoch_millis")
    val dailyUsageDayStartEpochMillis = longPreferencesKey("daily_usage.day_start_epoch_millis")
    val dailyUsageConsumedCount = intPreferencesKey("daily_usage.consumed_count")
}
