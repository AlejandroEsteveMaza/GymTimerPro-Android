package com.alejandroestevemaza.gymtimerpro.feature.training.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alejandroestevemaza.gymtimerpro.R
import com.alejandroestevemaza.gymtimerpro.core.format.formatDuration
import com.alejandroestevemaza.gymtimerpro.core.format.formatRoutineSummary
import com.alejandroestevemaza.gymtimerpro.core.model.Routine
import com.alejandroestevemaza.gymtimerpro.core.model.RoutineClassification
import com.alejandroestevemaza.gymtimerpro.data.preferences.AppContainer
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallEntryPoint
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallInfoLevel
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallPresentationContext
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallPresentationRequest
import com.alejandroestevemaza.gymtimerpro.feature.routines.ui.RoutineCatalogSection
import com.alejandroestevemaza.gymtimerpro.feature.routines.ui.RoutinesUiState

@Composable
fun TrainingRoute(
    appContainer: AppContainer,
    onRequestPaywall: (PaywallPresentationRequest) -> Unit,
) {
    val context = LocalContext.current
    var showRoutinePicker by remember { mutableStateOf(false) }
    var pickerSearchQuery by remember { mutableStateOf("") }
    var pickerExpandedSectionId by remember { mutableStateOf<String?>(null) }
    var hasRequestedNotificationPermission by rememberSaveable {
        mutableStateOf(
            context.getSharedPreferences(PERMISSION_PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_POST_NOTIFICATIONS_REQUESTED, false)
        )
    }
    val permissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { _ ->
        hasRequestedNotificationPermission = true
    }
    val trainingViewModel: TrainingViewModel = viewModel(
        factory = TrainingViewModel.factory(
            appSettingsRepository = appContainer.appSettingsRepository,
            premiumStateRepository = appContainer.premiumStateRepository,
            trainingSessionRepository = appContainer.trainingSessionRepository,
            routinesRepository = appContainer.routinesRepository,
            trainingSessionCoordinator = appContainer.trainingSessionCoordinator,
            workoutCompletionRepository = appContainer.workoutCompletionRepository,
            restNotificationCoordinator = appContainer.restNotificationCoordinator,
            quickWorkoutLabel = stringResource(R.string.training_quick_workout),
        )
    )
    val uiState by trainingViewModel.uiState.collectAsStateWithLifecycle()
    val routines by appContainer.routinesRepository.routines.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    val classifications by appContainer.routinesRepository.classifications.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    val requestNotificationPermissionIfNeeded: () -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission && !hasRequestedNotificationPermission) {
                context.getSharedPreferences(PERMISSION_PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(KEY_POST_NOTIFICATIONS_REQUESTED, true)
                    .apply()
                hasRequestedNotificationPermission = true
                permissionRequestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    TrainingScreen(
        uiState = uiState,
        routines = routines,
        classifications = classifications,
        showRoutinePicker = showRoutinePicker,
        pickerSearchQuery = pickerSearchQuery,
        pickerExpandedSectionId = pickerExpandedSectionId,
        onIncreaseTotalSets = trainingViewModel::onIncreaseTotalSets,
        onDecreaseTotalSets = trainingViewModel::onDecreaseTotalSets,
        onIncreaseRestSeconds = trainingViewModel::onIncreaseRestSeconds,
        onDecreaseRestSeconds = trainingViewModel::onDecreaseRestSeconds,
        onStartRest = {
            trainingViewModel.onStartRest()
            requestNotificationPermissionIfNeeded()
        },
        onResetWorkout = trainingViewModel::onResetWorkout,
        onDismissDailyLimitDialog = trainingViewModel::onDismissDailyLimitDialog,
        onRequestPaywall = onRequestPaywall,
        onOpenRoutinePicker = { showRoutinePicker = true },
        onDismissRoutinePicker = {
            showRoutinePicker = false
            pickerSearchQuery = ""
            pickerExpandedSectionId = null
        },
        onPickerSearchQueryChanged = { pickerSearchQuery = it },
        onPickerToggleSection = { sectionId ->
            pickerExpandedSectionId = if (pickerExpandedSectionId == sectionId) null else sectionId
        },
        onApplyRoutine = { routineId ->
            trainingViewModel.onApplyRoutine(routineId)
            showRoutinePicker = false
            pickerSearchQuery = ""
            pickerExpandedSectionId = null
        },
        onClearAppliedRoutine = {
            trainingViewModel.onClearAppliedRoutine()
            showRoutinePicker = false
            pickerSearchQuery = ""
            pickerExpandedSectionId = null
        },
    )
}

private const val PERMISSION_PREFS_NAME = "gymtimerpro.permissions"
private const val KEY_POST_NOTIFICATIONS_REQUESTED = "post_notifications.requested"

@Composable
fun TrainingScreen(
    uiState: TrainingUiState,
    routines: List<Routine>,
    classifications: List<RoutineClassification>,
    showRoutinePicker: Boolean,
    pickerSearchQuery: String,
    pickerExpandedSectionId: String?,
    onIncreaseTotalSets: () -> Unit,
    onDecreaseTotalSets: () -> Unit,
    onIncreaseRestSeconds: () -> Unit,
    onDecreaseRestSeconds: () -> Unit,
    onStartRest: () -> Unit,
    onResetWorkout: () -> Unit,
    onDismissDailyLimitDialog: () -> Unit,
    onRequestPaywall: (PaywallPresentationRequest) -> Unit,
    onOpenRoutinePicker: () -> Unit,
    onDismissRoutinePicker: () -> Unit,
    onPickerSearchQueryChanged: (String) -> Unit,
    onPickerToggleSection: (String) -> Unit,
    onApplyRoutine: (String) -> Unit,
    onClearAppliedRoutine: () -> Unit,
) {
    val pickerCatalogState = RoutinesUiState(
        routines = routines,
        classifications = classifications,
        appliedRoutineId = uiState.session.appliedRoutineId,
        searchQuery = pickerSearchQuery,
        expandedSectionId = pickerExpandedSectionId,
    )

    LaunchedEffect(uiState.showDailyLimitDialog) {
        if (uiState.showDailyLimitDialog) {
            onRequestPaywall(
                PaywallPresentationRequest(
                    context = PaywallPresentationContext(
                        entryPoint = PaywallEntryPoint.DailyLimitDuringWorkout,
                        infoLevel = PaywallInfoLevel.Light,
                    ),
                    dailyLimit = com.alejandroestevemaza.gymtimerpro.core.model.TrainingDefaults.dailyFreeUsageLimit,
                    consumedToday = uiState.dailyUsage.consumedCount,
                )
            )
            onDismissDailyLimitDialog()
        }
    }

    if (showRoutinePicker) {
        RoutinePickerDialog(
            pickerState = pickerCatalogState,
            settings = uiState.settings,
            onDismiss = onDismissRoutinePicker,
            onSearchQueryChanged = onPickerSearchQueryChanged,
            onToggleSection = onPickerToggleSection,
            onApplyRoutine = onApplyRoutine,
            onClearAppliedRoutine = onClearAppliedRoutine,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TrainingConfigurationCard(
            uiState = uiState,
            onOpenRoutinePicker = onOpenRoutinePicker,
            onIncreaseTotalSets = onIncreaseTotalSets,
            onDecreaseTotalSets = onDecreaseTotalSets,
            onIncreaseRestSeconds = onIncreaseRestSeconds,
            onDecreaseRestSeconds = onDecreaseRestSeconds,
            onUpgradeToPro = {
                onRequestPaywall(
                    PaywallPresentationRequest(
                        context = PaywallPresentationContext(
                            entryPoint = PaywallEntryPoint.ProModule,
                            infoLevel = PaywallInfoLevel.Standard,
                        ),
                        dailyLimit = com.alejandroestevemaza.gymtimerpro.core.model.TrainingDefaults.dailyFreeUsageLimit,
                        consumedToday = uiState.dailyUsage.consumedCount,
                    )
                )
            },
        )

        TrainingProgressCard(
            uiState = uiState,
            onResetWorkout = onResetWorkout,
        )

        Spacer(modifier = Modifier.weight(1f, fill = true))

        Button(
            onClick = onStartRest,
            enabled = uiState.startRestEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text(text = stringResource(R.string.app_shell_start_rest))
        }
    }
}

@Composable
private fun TrainingConfigurationCard(
    uiState: TrainingUiState,
    onOpenRoutinePicker: () -> Unit,
    onIncreaseTotalSets: () -> Unit,
    onDecreaseTotalSets: () -> Unit,
    onIncreaseRestSeconds: () -> Unit,
    onDecreaseRestSeconds: () -> Unit,
    onUpgradeToPro: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.training_config_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            RoutineSummaryRow(
                uiState = uiState,
                onOpenRoutinePicker = onOpenRoutinePicker,
            )

            if (!uiState.isPro) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.pro_status_free),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = stringResource(
                                    R.string.pro_usage_today_format,
                                    uiState.dailyUsage.consumedCount,
                                    com.alejandroestevemaza.gymtimerpro.core.model.TrainingDefaults.dailyFreeUsageLimit,
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Button(onClick = onUpgradeToPro) {
                            Text(text = stringResource(R.string.pro_button_upgrade))
                        }
                    }
                }
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.pro_status_pro),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            StepperRow(
                label = stringResource(R.string.training_sets_label),
                value = uiState.session.totalSets.toString(),
                enabled = uiState.canEditConfiguration,
                onDecrease = onDecreaseTotalSets,
                onIncrease = onIncreaseTotalSets,
            )

            StepperRow(
                label = stringResource(R.string.training_rest_label),
                value = formatDuration(
                    totalSeconds = uiState.session.restSeconds,
                    displayFormat = uiState.settings.timerDisplayFormat,
                ),
                enabled = uiState.canEditConfiguration,
                onDecrease = onDecreaseRestSeconds,
                onIncrease = onIncreaseRestSeconds,
            )
        }
    }
}

@Composable
private fun RoutineSummaryRow(
    uiState: TrainingUiState,
    onOpenRoutinePicker: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(R.string.training_routine_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = uiState.session.appliedRoutineName ?: stringResource(R.string.training_routine_none),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            if (uiState.isPro && uiState.session.appliedRoutineReps != null) {
                Text(
                    text = stringResource(
                        R.string.training_routine_reps,
                        uiState.session.appliedRoutineReps,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (uiState.isPro) {
                OutlinedButton(onClick = onOpenRoutinePicker) {
                    Text(text = stringResource(R.string.training_select_routine))
                }
            }
        }
    }
}

@Composable
private fun StepperRow(
    label: String,
    value: String,
    enabled: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onDecrease,
                enabled = enabled,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Remove,
                    contentDescription = null,
                )
            }
            IconButton(
                onClick = onIncrease,
                enabled = enabled,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoutinePickerDialog(
    pickerState: RoutinesUiState,
    settings: com.alejandroestevemaza.gymtimerpro.core.model.AppSettings,
    onDismiss: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onToggleSection: (String) -> Unit,
    onApplyRoutine: (String) -> Unit,
    onClearAppliedRoutine: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.training_select_routine)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = pickerState.searchQuery,
                    onValueChange = onSearchQueryChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(R.string.routines_search_hint)) },
                    singleLine = true,
                )

                if (pickerState.appliedRoutineId != null) {
                    OutlinedButton(onClick = onClearAppliedRoutine) {
                        Text(text = stringResource(R.string.training_remove_routine))
                    }
                }

                if (pickerState.groupedSections.isEmpty()) {
                    Text(
                        text = stringResource(R.string.routines_empty_title),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Column(
                        modifier = Modifier.height(320.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        pickerState.groupedSections.forEach { section ->
                            PickerSectionCard(
                                section = section,
                                pickerState = pickerState,
                                settings = settings,
                                onToggleSection = onToggleSection,
                                onApplyRoutine = onApplyRoutine,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.routines_cancel))
            }
        },
    )
}

@Composable
private fun PickerSectionCard(
    section: RoutineCatalogSection,
    pickerState: RoutinesUiState,
    settings: com.alejandroestevemaza.gymtimerpro.core.model.AppSettings,
    onToggleSection: (String) -> Unit,
    onApplyRoutine: (String) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !pickerState.isSearchMode) { onToggleSection(section.id) },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = when (section.id) {
                        RoutinesUiState.UNCLASSIFIED_SECTION_ID -> stringResource(R.string.routines_unclassified)
                        RoutinesUiState.MATCHING_ROUTINES_SECTION_ID -> stringResource(R.string.routines_matching_routines)
                        else -> section.title
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                if (!pickerState.isSearchMode) {
                    Icon(
                        imageVector = if (section.isExpanded) Icons.Rounded.Remove else Icons.Rounded.Add,
                        contentDescription = null,
                    )
                }
            }

            if (pickerState.isSearchMode || section.isExpanded) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    section.routines.forEach { routine ->
                        OutlinedButton(onClick = { onApplyRoutine(routine.id) }) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(text = routine.name)
                                Text(
                                    text = formatRoutineSummary(
                                        totalSets = routine.totalSets,
                                        reps = routine.reps,
                                        restSeconds = routine.restSeconds,
                                        weightKg = routine.weightKg,
                                        timerDisplayFormat = settings.timerDisplayFormat,
                                        weightUnitPreference = settings.weightUnitPreference,
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrainingProgressCard(
    uiState: TrainingUiState,
    onResetWorkout: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.training_progress_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(
                        R.string.training_set_progress,
                        uiState.session.currentSet,
                        uiState.session.totalSets,
                    ),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = {
                        Text(
                            text = when {
                                uiState.session.completed -> stringResource(R.string.training_state_completed)
                                uiState.session.timerIsRunning -> stringResource(R.string.training_state_resting)
                                else -> stringResource(R.string.training_state_training)
                            }
                        )
                    },
                )
            }

            if (uiState.session.appliedRoutineReps != null && uiState.isPro) {
                Text(
                    text = stringResource(
                        R.string.training_routine_reps,
                        uiState.session.appliedRoutineReps,
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            when {
                uiState.session.completed -> {
                    Text(
                        text = stringResource(R.string.training_completed_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(R.string.training_completed_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                uiState.session.timerIsRunning -> {
                    Text(
                        text = formatDuration(
                            totalSeconds = uiState.session.timerRemainingSeconds,
                            displayFormat = uiState.settings.timerDisplayFormat,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.displayMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                    )
                }

                uiState.session.timerDidFinish -> {
                    Text(
                        text = stringResource(R.string.training_timer_finished),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            if (uiState.canReset) {
                OutlinedButton(
                    onClick = onResetWorkout,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.training_reset))
                }
            }
        }
    }
}
