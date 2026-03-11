package com.alejandroestevemaza.gymtimerpro.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandroestevemaza.gymtimerpro.R
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.displayLabel
import com.alejandroestevemaza.gymtimerpro.core.model.AppSettings
import com.alejandroestevemaza.gymtimerpro.core.model.EnergySavingMode
import com.alejandroestevemaza.gymtimerpro.core.model.MaxSetsPreference
import com.alejandroestevemaza.gymtimerpro.core.model.RestIncrementPreference
import com.alejandroestevemaza.gymtimerpro.core.model.TimerDisplayFormat
import com.alejandroestevemaza.gymtimerpro.core.model.WeightUnitPreference

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onWeightUnitPreferenceSelected: (WeightUnitPreference) -> Unit,
    onTimerDisplayFormatSelected: (TimerDisplayFormat) -> Unit,
    onMaxSetsPreferenceSelected: (MaxSetsPreference) -> Unit,
    onRestIncrementPreferenceSelected: (RestIncrementPreference) -> Unit,
    onEnergySavingModeSelected: (EnergySavingMode) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            SettingsCard(
                title = stringResource(R.string.app_shell_settings_title),
                body = stringResource(R.string.app_shell_settings_body),
            )
        }
        item {
            SettingsOptionsCard(
                title = stringResource(R.string.settings_section_weight_unit),
                selectedOption = settings.weightUnitPreference,
                options = WeightUnitPreference.entries.toList(),
                optionLabel = { option -> option.displayLabel() },
                onOptionSelected = onWeightUnitPreferenceSelected,
            )
        }
        item {
            SettingsOptionsCard(
                title = stringResource(R.string.settings_section_timer_display),
                selectedOption = settings.timerDisplayFormat,
                options = TimerDisplayFormat.entries.toList(),
                optionLabel = { option -> option.displayLabel() },
                onOptionSelected = onTimerDisplayFormatSelected,
            )
        }
        item {
            SettingsOptionsCard(
                title = stringResource(R.string.settings_section_max_sets),
                selectedOption = settings.maxSetsPreference,
                options = MaxSetsPreference.entries.toList(),
                optionLabel = { option -> option.maxSets.toString() },
                onOptionSelected = onMaxSetsPreferenceSelected,
            )
        }
        item {
            SettingsOptionsCard(
                title = stringResource(R.string.settings_section_rest_increment),
                selectedOption = settings.restIncrementPreference,
                options = RestIncrementPreference.entries.toList(),
                optionLabel = { option -> "${option.seconds} sec" },
                onOptionSelected = onRestIncrementPreferenceSelected,
            )
        }
        item {
            SettingsOptionsCard(
                title = stringResource(R.string.settings_section_energy_saving),
                selectedOption = settings.energySavingMode,
                options = EnergySavingMode.entries.toList(),
                optionLabel = { option -> option.displayLabel() },
                onOptionSelected = onEnergySavingModeSelected,
            )
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    body: String,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun <T> SettingsOptionsCard(
    title: String,
    selectedOption: T,
    options: List<T>,
    optionLabel: @Composable (T) -> String,
    onOptionSelected: (T) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = optionLabel(selectedOption),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { option ->
                    SettingsOptionRow(
                        label = optionLabel(option),
                        selected = option == selectedOption,
                        onClick = { onOptionSelected(option) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            if (selected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
