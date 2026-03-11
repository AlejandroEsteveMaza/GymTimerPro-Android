package com.alejandroestevemaza.gymtimerpro.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.alejandroestevemaza.gymtimerpro.R
import com.alejandroestevemaza.gymtimerpro.core.model.EnergySavingMode
import com.alejandroestevemaza.gymtimerpro.core.model.TimerDisplayFormat
import com.alejandroestevemaza.gymtimerpro.core.model.WeightUnitPreference

@Composable
fun WeightUnitPreference.displayLabel(): String = when (this) {
    WeightUnitPreference.Automatic -> stringResource(R.string.settings_weight_unit_automatic)
    WeightUnitPreference.Kilograms -> stringResource(R.string.settings_weight_unit_kilograms)
    WeightUnitPreference.Pounds -> stringResource(R.string.settings_weight_unit_pounds)
}

@Composable
fun TimerDisplayFormat.displayLabel(): String = when (this) {
    TimerDisplayFormat.Seconds -> stringResource(R.string.settings_timer_display_seconds)
    TimerDisplayFormat.MinutesAndSeconds -> stringResource(
        R.string.settings_timer_display_minutes_seconds
    )
}

@Composable
fun EnergySavingMode.displayLabel(): String = when (this) {
    EnergySavingMode.Off -> stringResource(R.string.settings_energy_off)
    EnergySavingMode.Automatic -> stringResource(R.string.settings_energy_automatic)
    EnergySavingMode.On -> stringResource(R.string.settings_energy_on)
}
