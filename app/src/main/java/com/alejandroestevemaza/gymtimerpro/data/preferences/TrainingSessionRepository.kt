package com.alejandroestevemaza.gymtimerpro.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import com.alejandroestevemaza.gymtimerpro.core.model.DailyUsageState
import com.alejandroestevemaza.gymtimerpro.core.model.TrainingDefaults
import com.alejandroestevemaza.gymtimerpro.core.model.TrainingSessionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface TrainingSessionRepository {
    val sessionState: Flow<TrainingSessionState>
    val dailyUsageState: Flow<DailyUsageState>

    suspend fun updateSession(transform: (TrainingSessionState) -> TrainingSessionState)
    suspend fun updateDailyUsage(transform: (DailyUsageState) -> DailyUsageState)
}

class DataStoreTrainingSessionRepository(
    private val context: Context,
) : TrainingSessionRepository {
    private val updateMutex = Mutex()

    override val sessionState: Flow<TrainingSessionState> = context.gymTimerProDataStore.data
        .map { preferences ->
            TrainingSessionState(
                totalSets = preferences[PreferencesKeys.trainingTotalSets] ?: TrainingDefaults.totalSets,
                restSeconds = preferences[PreferencesKeys.trainingRestSeconds] ?: TrainingDefaults.restSeconds,
                currentSet = preferences[PreferencesKeys.trainingCurrentSet] ?: TrainingDefaults.currentSet,
                completed = preferences[PreferencesKeys.trainingCompleted] ?: false,
                appliedRoutineId = preferences[PreferencesKeys.trainingAppliedRoutineId],
                appliedRoutineName = preferences[PreferencesKeys.trainingAppliedRoutineName],
                appliedRoutineReps = preferences[PreferencesKeys.trainingAppliedRoutineReps],
                appliedClassificationId = preferences[PreferencesKeys.trainingAppliedClassificationId],
                appliedClassificationName = preferences[PreferencesKeys.trainingAppliedClassificationName],
                timerIsRunning = preferences[PreferencesKeys.timerIsRunning] ?: false,
                timerEndEpochMillis = preferences[PreferencesKeys.timerEndEpochMillis],
                timerRemainingSeconds = preferences[PreferencesKeys.timerRemainingSeconds] ?: 0,
                timerDidFinish = preferences[PreferencesKeys.timerDidFinish] ?: false,
                completedAtEpochMillis = preferences[PreferencesKeys.trainingCompletedAtEpochMillis],
            )
        }

    override val dailyUsageState: Flow<DailyUsageState> = context.gymTimerProDataStore.data
        .map { preferences ->
            DailyUsageState(
                dayStartEpochMillis = preferences[PreferencesKeys.dailyUsageDayStartEpochMillis],
                consumedCount = preferences[PreferencesKeys.dailyUsageConsumedCount] ?: 0,
            )
        }

    override suspend fun updateSession(transform: (TrainingSessionState) -> TrainingSessionState) {
        updateMutex.withLock {
            val updatedState = transform(sessionState.first())
            context.gymTimerProDataStore.edit { preferences ->
                preferences.writeSession(updatedState)
            }
        }
    }

    override suspend fun updateDailyUsage(transform: (DailyUsageState) -> DailyUsageState) {
        updateMutex.withLock {
            val updatedState = transform(dailyUsageState.first())
            context.gymTimerProDataStore.edit { preferences ->
                preferences.writeDailyUsage(updatedState)
            }
        }
    }
}

private fun MutablePreferences.writeSession(state: TrainingSessionState) {
    this[PreferencesKeys.trainingTotalSets] = state.totalSets
    this[PreferencesKeys.trainingRestSeconds] = state.restSeconds
    this[PreferencesKeys.trainingCurrentSet] = state.currentSet
    this[PreferencesKeys.trainingCompleted] = state.completed
    writeNullableString(PreferencesKeys.trainingAppliedRoutineId, state.appliedRoutineId)
    writeNullableString(PreferencesKeys.trainingAppliedRoutineName, state.appliedRoutineName)
    writeNullableInt(PreferencesKeys.trainingAppliedRoutineReps, state.appliedRoutineReps)
    writeNullableString(PreferencesKeys.trainingAppliedClassificationId, state.appliedClassificationId)
    writeNullableString(
        PreferencesKeys.trainingAppliedClassificationName,
        state.appliedClassificationName,
    )
    this[PreferencesKeys.timerIsRunning] = state.timerIsRunning
    writeNullableLong(PreferencesKeys.timerEndEpochMillis, state.timerEndEpochMillis)
    this[PreferencesKeys.timerRemainingSeconds] = state.timerRemainingSeconds
    this[PreferencesKeys.timerDidFinish] = state.timerDidFinish
    writeNullableLong(PreferencesKeys.trainingCompletedAtEpochMillis, state.completedAtEpochMillis)
}

private fun MutablePreferences.writeDailyUsage(state: DailyUsageState) {
    writeNullableLong(PreferencesKeys.dailyUsageDayStartEpochMillis, state.dayStartEpochMillis)
    this[PreferencesKeys.dailyUsageConsumedCount] = state.consumedCount
}

private fun MutablePreferences.writeNullableString(
    key: androidx.datastore.preferences.core.Preferences.Key<String>,
    value: String?,
) {
    if (value == null) {
        remove(key)
    } else {
        this[key] = value
    }
}

private fun MutablePreferences.writeNullableInt(
    key: androidx.datastore.preferences.core.Preferences.Key<Int>,
    value: Int?,
) {
    if (value == null) {
        remove(key)
    } else {
        this[key] = value
    }
}

private fun MutablePreferences.writeNullableLong(
    key: androidx.datastore.preferences.core.Preferences.Key<Long>,
    value: Long?,
) {
    if (value == null) {
        remove(key)
    } else {
        this[key] = value
    }
}
