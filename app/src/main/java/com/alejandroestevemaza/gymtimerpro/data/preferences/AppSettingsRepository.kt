package com.alejandroestevemaza.gymtimerpro.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.alejandroestevemaza.gymtimerpro.core.model.AppSettings
import com.alejandroestevemaza.gymtimerpro.core.model.EnergySavingMode
import com.alejandroestevemaza.gymtimerpro.core.model.MaxSetsPreference
import com.alejandroestevemaza.gymtimerpro.core.model.RestIncrementPreference
import com.alejandroestevemaza.gymtimerpro.core.model.TimerDisplayFormat
import com.alejandroestevemaza.gymtimerpro.core.model.WeightUnitPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

interface AppSettingsRepository {
    val settings: Flow<AppSettings>

    suspend fun setWeightUnitPreference(value: WeightUnitPreference)
    suspend fun setTimerDisplayFormat(value: TimerDisplayFormat)
    suspend fun setMaxSetsPreference(value: MaxSetsPreference)
    suspend fun setRestIncrementPreference(value: RestIncrementPreference)
    suspend fun setEnergySavingMode(value: EnergySavingMode)
}

class DataStoreAppSettingsRepository(
    private val context: Context,
) : AppSettingsRepository {
    override val settings: Flow<AppSettings> = context.gymTimerProDataStore.data
        .map { preferences ->
            AppSettings(
                weightUnitPreference = WeightUnitPreference.fromStorageValue(
                    preferences[PreferencesKeys.weightUnitPreference]
                        ?: WeightUnitPreference.Automatic.storageValue
                ),
                timerDisplayFormat = TimerDisplayFormat.fromStorageValue(
                    preferences[PreferencesKeys.timerDisplayFormat]
                        ?: TimerDisplayFormat.Seconds.storageValue
                ),
                maxSetsPreference = MaxSetsPreference.fromStorageValue(
                    preferences[PreferencesKeys.trainingMaxSets]
                        ?: MaxSetsPreference.Ten.maxSets
                ),
                restIncrementPreference = RestIncrementPreference.fromStorageValue(
                    preferences[PreferencesKeys.trainingRestIncrement]
                        ?: RestIncrementPreference.Fifteen.seconds
                ),
                energySavingMode = EnergySavingMode.fromStorageValue(
                    preferences[PreferencesKeys.energySavingMode]
                        ?: EnergySavingMode.Off.storageValue
                ),
            )
        }
        .distinctUntilChanged()

    override suspend fun setWeightUnitPreference(value: WeightUnitPreference) {
        context.gymTimerProDataStore.edit { preferences ->
            preferences[PreferencesKeys.weightUnitPreference] = value.storageValue
        }
    }

    override suspend fun setTimerDisplayFormat(value: TimerDisplayFormat) {
        context.gymTimerProDataStore.edit { preferences ->
            preferences[PreferencesKeys.timerDisplayFormat] = value.storageValue
        }
    }

    override suspend fun setMaxSetsPreference(value: MaxSetsPreference) {
        context.gymTimerProDataStore.edit { preferences ->
            preferences[PreferencesKeys.trainingMaxSets] = value.maxSets
        }
    }

    override suspend fun setRestIncrementPreference(value: RestIncrementPreference) {
        context.gymTimerProDataStore.edit { preferences ->
            preferences[PreferencesKeys.trainingRestIncrement] = value.seconds
        }
    }

    override suspend fun setEnergySavingMode(value: EnergySavingMode) {
        context.gymTimerProDataStore.edit { preferences ->
            preferences[PreferencesKeys.energySavingMode] = value.storageValue
        }
    }
}
