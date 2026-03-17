package com.alejandroestevemaza.gymtimerpro.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.alejandroestevemaza.gymtimerpro.R
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTheme
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.displayLabel
import com.alejandroestevemaza.gymtimerpro.core.model.AppSettings
import com.alejandroestevemaza.gymtimerpro.core.model.EnergySavingMode
import com.alejandroestevemaza.gymtimerpro.core.model.MaxSetsPreference
import com.alejandroestevemaza.gymtimerpro.core.model.RestIncrementPreference
import com.alejandroestevemaza.gymtimerpro.core.model.TimerDisplayFormat
import com.alejandroestevemaza.gymtimerpro.core.model.WeightUnitPreference

enum class SettingsPreviewMenu {
    WeightUnit,
    MaxSets,
    EnergySaving,
}

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onWeightUnitPreferenceSelected: (WeightUnitPreference) -> Unit,
    onTimerDisplayFormatSelected: (TimerDisplayFormat) -> Unit,
    onMaxSetsPreferenceSelected: (MaxSetsPreference) -> Unit,
    onRestIncrementPreferenceSelected: (RestIncrementPreference) -> Unit,
    onEnergySavingModeSelected: (EnergySavingMode) -> Unit,
    onManageClassifications: () -> Unit = {},
    previewExpandedMenu: SettingsPreviewMenu? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = GymTheme.spacing.s16, vertical = GymTheme.spacing.s12),
        verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s20),
    ) {
        Text(
            text = stringResource(R.string.app_navigation_settings),
            style = GymTheme.type.title2Bold,
            color = GymTheme.colors.textPrimary,
        )

        SettingsMenuSection(
            title = stringResource(R.string.settings_section_weight_unit),
            rowLabel = stringResource(R.string.settings_section_weight_unit),
            selectedOption = settings.weightUnitPreference,
            selectedLabel = settings.weightUnitPreference.displayLabel(),
            options = WeightUnitPreference.entries.toList(),
            optionLabel = { option -> option.displayLabel() },
            onOptionSelected = onWeightUnitPreferenceSelected,
            forceExpanded = previewExpandedMenu == SettingsPreviewMenu.WeightUnit,
        )

        SettingsSegmentedSection(
            title = stringResource(R.string.settings_section_timer_display),
            selected = settings.timerDisplayFormat,
            options = TimerDisplayFormat.entries.toList(),
            optionLabel = { option -> option.displayLabel() },
            onOptionSelected = onTimerDisplayFormatSelected,
        )

        SettingsMenuSection(
            title = stringResource(R.string.settings_section_max_sets),
            rowLabel = stringResource(R.string.settings_section_max_sets),
            selectedOption = settings.maxSetsPreference,
            selectedLabel = "${settings.maxSetsPreference.maxSets} ${stringResource(R.string.routines_sets_label).lowercase()}",
            options = MaxSetsPreference.entries.toList(),
            optionLabel = { option ->
                "${option.maxSets} ${stringResource(R.string.routines_sets_label).lowercase()}"
            },
            onOptionSelected = onMaxSetsPreferenceSelected,
            forceExpanded = previewExpandedMenu == SettingsPreviewMenu.MaxSets,
        )

        SettingsSegmentedSection(
            title = stringResource(R.string.settings_section_rest_increment),
            selected = settings.restIncrementPreference,
            options = RestIncrementPreference.entries.toList(),
            optionLabel = { option -> "${option.seconds} sec" },
            onOptionSelected = onRestIncrementPreferenceSelected,
        )

        SettingsMenuSection(
            title = stringResource(R.string.settings_section_energy_saving),
            rowLabel = stringResource(R.string.settings_mode_label),
            selectedOption = settings.energySavingMode,
            selectedLabel = settings.energySavingMode.displayLabel(),
            options = EnergySavingMode.entries.toList(),
            optionLabel = { option -> option.displayLabel() },
            onOptionSelected = onEnergySavingModeSelected,
            forceExpanded = previewExpandedMenu == SettingsPreviewMenu.EnergySaving,
        )
        Text(
            text = stringResource(R.string.settings_energy_description),
            style = GymTheme.type.footnoteRegular,
            color = GymTheme.colors.textSecondary,
        )

        SettingsNavigationRow(
            label = stringResource(R.string.routines_manage_classifications),
            onClick = onManageClassifications,
        )
    }
}

@Composable
private fun <T> SettingsMenuSection(
    title: String,
    rowLabel: String,
    selectedOption: T,
    selectedLabel: String,
    options: List<T>,
    optionLabel: @Composable (T) -> String,
    onOptionSelected: (T) -> Unit,
    forceExpanded: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8)) {
        SettingsSectionLabel(title = title)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = GymTheme.layout.minTapHeight)
                .clickable {
                    if (!forceExpanded) {
                        expanded = true
                    }
                },
            shape = RoundedCornerShape(GymTheme.radii.r12),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = GymTheme.spacing.s16, vertical = GymTheme.spacing.s12),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = rowLabel,
                    style = GymTheme.type.subheadlineRegular,
                    color = GymTheme.colors.textPrimary,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = selectedLabel,
                        style = GymTheme.type.subheadlineRegular,
                        color = GymTheme.colors.iconTint,
                        textAlign = TextAlign.End,
                    )
                    Icon(
                        imageVector = Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        tint = GymTheme.colors.iconTint,
                    )
                }
            }
        }

        if (forceExpanded) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.58f)
                    .align(Alignment.End)
                    .padding(top = GymTheme.spacing.s4),
                shape = RoundedCornerShape(GymTheme.radii.r20),
            ) {
                Column {
                    options.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = GymTheme.layout.minTapHeight)
                                .clickable { onOptionSelected(option) }
                                .padding(horizontal = GymTheme.spacing.s16),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val isSelected = option == selectedOption
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = GymTheme.colors.textPrimary,
                                    modifier = Modifier.padding(end = GymTheme.spacing.s12),
                                )
                            }
                            Text(
                                text = optionLabel(option),
                                style = GymTheme.type.subheadlineRegular,
                                color = GymTheme.colors.textPrimary,
                            )
                        }
                    }
                }
            }
        } else {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(
                    color = GymTheme.colors.cardBackground,
                    shape = RoundedCornerShape(GymTheme.radii.r12),
                ),
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = optionLabel(option),
                                style = GymTheme.type.subheadlineRegular,
                                color = GymTheme.colors.textPrimary,
                            )
                        },
                        leadingIcon = {
                            if (option == selectedOption) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = GymTheme.colors.textPrimary,
                                )
                            }
                        },
                        onClick = {
                            expanded = false
                            onOptionSelected(option)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> SettingsSegmentedSection(
    title: String,
    selected: T,
    options: List<T>,
    optionLabel: @Composable (T) -> String,
    onOptionSelected: (T) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8)) {
        SettingsSectionLabel(title = title)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = GymTheme.colors.secondaryButtonFill,
                    shape = RoundedCornerShape(GymTheme.radii.capsule),
                )
                .padding(GymTheme.spacing.s4),
            horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s4),
        ) {
            options.forEach { option ->
                val isSelected = option == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = GymTheme.layout.minTapHeight)
                        .background(
                            color = if (isSelected) {
                                GymTheme.colors.iconTint.copy(alpha = 0.16f)
                            } else {
                                androidx.compose.ui.graphics.Color.Transparent
                            },
                            shape = RoundedCornerShape(GymTheme.radii.capsule),
                        )
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    width = GymTheme.borders.quaternary,
                                    color = GymTheme.colors.iconTint.copy(alpha = 0.42f),
                                    shape = RoundedCornerShape(GymTheme.radii.capsule),
                                )
                            } else {
                                Modifier
                            }
                        )
                        .clickable { onOptionSelected(option) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = optionLabel(option),
                        style = if (isSelected) GymTheme.type.subheadlineSemibold else GymTheme.type.subheadlineRegular,
                        color = if (isSelected) GymTheme.colors.iconTint else GymTheme.colors.textSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionLabel(
    title: String,
) {
    Text(
        text = title,
        style = GymTheme.type.captionRegular,
        color = GymTheme.colors.textSecondary,
    )
}

@Composable
private fun SettingsNavigationRow(
    label: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = GymTheme.layout.minTapHeight)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(GymTheme.radii.r12),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GymTheme.spacing.s16, vertical = GymTheme.spacing.s12),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = GymTheme.type.subheadlineRegular,
                color = GymTheme.colors.textPrimary,
            )
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = GymTheme.colors.textSecondary,
            )
        }
    }
}
