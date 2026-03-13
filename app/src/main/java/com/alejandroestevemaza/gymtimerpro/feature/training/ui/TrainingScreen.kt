package com.alejandroestevemaza.gymtimerpro.feature.training.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alejandroestevemaza.gymtimerpro.R
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.GymComponentState
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.NumericConfigRow
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.PrimaryCtaButton
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.SectionCard
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTheme
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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            PrimaryCtaButton(
                text = stringResource(R.string.app_shell_start_rest),
                onClick = onStartRest,
                state = if (uiState.startRestEnabled) GymComponentState.Normal else GymComponentState.Disabled,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = GymTheme.spacing.s20, vertical = GymTheme.spacing.s12),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding(),
            contentPadding = PaddingValues(
                start = GymTheme.spacing.s20,
                end = GymTheme.spacing.s20,
                top = GymTheme.spacing.s12,
                bottom = GymTheme.layout.scrollBottomPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s20),
        ) {
            item {
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
            }
            item {
                TrainingProgressCard(
                    uiState = uiState,
                    onResetWorkout = onResetWorkout,
                )
            }
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
    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        state = if (uiState.canEditConfiguration) GymComponentState.Normal else GymComponentState.Disabled,
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = null,
                    tint = GymTheme.colors.textSecondary,
                    modifier = Modifier.size(GymTheme.spacing.s16),
                )
                Text(
                    text = stringResource(R.string.training_config_title),
                    style = GymTheme.type.headlineSemibold,
                    color = GymTheme.colors.textPrimary,
                )
            }
        },
        trailing = {
            ProStatusChip(isPro = uiState.isPro)
        },
    ) {
        RoutineSummaryRow(
            uiState = uiState,
            onOpenRoutinePicker = onOpenRoutinePicker,
        )
        HorizontalDivider(color = GymTheme.colors.divider)

        NumericConfigRow(
            icon = Icons.Rounded.Layers,
            title = stringResource(R.string.training_sets_label),
            valueText = uiState.session.totalSets.toString(),
            onDecrease = onDecreaseTotalSets,
            onIncrease = onIncreaseTotalSets,
            state = if (uiState.canEditConfiguration) GymComponentState.Normal else GymComponentState.Disabled,
        )
        HorizontalDivider(color = GymTheme.colors.divider)

        NumericConfigRow(
            icon = Icons.Rounded.Timer,
            title = stringResource(R.string.training_rest_label),
            valueText = formatDuration(
                totalSeconds = uiState.session.restSeconds,
                displayFormat = uiState.settings.timerDisplayFormat,
            ),
            onDecrease = onDecreaseRestSeconds,
            onIncrease = onIncreaseRestSeconds,
            state = if (uiState.canEditConfiguration) GymComponentState.Normal else GymComponentState.Disabled,
        )
        if (!uiState.isPro) {
            HorizontalDivider(color = GymTheme.colors.divider)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(
                        R.string.pro_usage_today_format,
                        uiState.dailyUsage.consumedCount,
                        com.alejandroestevemaza.gymtimerpro.core.model.TrainingDefaults.dailyFreeUsageLimit,
                    ),
                    style = GymTheme.type.footnoteRegular,
                    color = GymTheme.colors.textSecondary,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = onUpgradeToPro) {
                    Text(text = stringResource(R.string.pro_button_upgrade))
                }
            }
        }
    }
}

@Composable
private fun RoutineSummaryRow(
    uiState: TrainingUiState,
    onOpenRoutinePicker: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenRoutinePicker)
            .padding(vertical = GymTheme.spacing.s2),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.FitnessCenter,
                contentDescription = null,
                tint = GymTheme.colors.iconTint,
                modifier = Modifier
                    .background(
                        color = GymTheme.colors.iconBackground,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.r8),
                    )
                    .padding(GymTheme.spacing.s6),
            )
            Text(
                text = stringResource(R.string.training_routine_label),
                style = GymTheme.type.valueLabel,
                color = GymTheme.colors.textPrimary,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = uiState.session.appliedRoutineName ?: stringResource(R.string.training_select_routine),
                style = GymTheme.type.subheadlineRegular,
                color = GymTheme.colors.textSecondary,
            )
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = GymTheme.colors.textSecondary,
            )
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
            Column(verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12)) {
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
                        style = GymTheme.type.subheadlineRegular,
                        color = GymTheme.colors.textSecondary,
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .height(GymTheme.layout.editorPopoverMinHeight)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s10),
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
            modifier = Modifier.padding(GymTheme.spacing.s12),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
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
                    style = GymTheme.type.subheadlineSemibold,
                    color = GymTheme.colors.textPrimary,
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
                    horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
                    verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
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
                                    style = GymTheme.type.footnoteRegular,
                                    color = GymTheme.colors.textSecondary,
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
    val stateLabel = when {
        uiState.session.timerIsRunning -> stringResource(R.string.training_state_resting).uppercase()
        else -> stringResource(R.string.training_state_training).uppercase()
    }
    val stateColor = if (uiState.session.timerIsRunning) GymTheme.colors.resting else GymTheme.colors.training

    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.FitnessCenter,
                    contentDescription = null,
                    tint = GymTheme.colors.textSecondary,
                    modifier = Modifier.size(GymTheme.spacing.s16),
                )
                Text(
                    text = stringResource(R.string.training_progress_title),
                    style = GymTheme.type.headlineSemibold,
                    color = GymTheme.colors.textPrimary,
                )
            }
        },
        trailing = {
            if (uiState.canReset) {
                IconButton(
                    onClick = onResetWorkout,
                    modifier = Modifier
                        .background(
                            color = GymTheme.colors.secondaryButtonFill,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.r8),
                        )
                        .size(GymTheme.layout.configIconFrame),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Remove,
                        contentDescription = null,
                        tint = GymTheme.colors.textSecondary,
                    )
                }
            }
        },
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.r16),
            color = GymTheme.colors.metricBackground,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(GymTheme.spacing.s12),
                verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s6),
            ) {
                Text(
                    text = stringResource(R.string.training_sets_label).uppercase(),
                    style = GymTheme.type.captionRegular,
                    color = GymTheme.colors.textSecondary,
                )
                Text(
                    text = "${uiState.session.currentSet} / ${uiState.session.totalSets}",
                    style = GymTheme.type.numericMetric,
                    color = GymTheme.colors.textPrimary,
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s10),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.training_state_label),
                style = GymTheme.type.subheadlineSemibold,
                color = GymTheme.colors.textSecondary,
            )
            Row(
                modifier = Modifier
                    .background(
                        color = stateColor.copy(alpha = 0.14f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.capsule),
                    )
                    .padding(horizontal = GymTheme.spacing.s10, vertical = GymTheme.spacing.s4),
                horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s6),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = if (uiState.session.timerIsRunning) Icons.Rounded.Timer else Icons.Rounded.Check,
                    contentDescription = null,
                    tint = stateColor,
                    modifier = Modifier.size(GymTheme.spacing.s14),
                )
                Text(
                    text = stateLabel,
                    style = GymTheme.type.captionSemibold,
                    color = stateColor,
                )
            }
        }

        if (uiState.session.timerIsRunning) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.r16),
                color = GymTheme.colors.timerBackground,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = GymTheme.spacing.s16, vertical = GymTheme.spacing.s12),
                    verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s6),
                ) {
                    Text(
                        text = stringResource(R.string.training_rest_label).uppercase(),
                        style = GymTheme.type.captionRegular,
                        color = GymTheme.colors.textSecondary,
                    )
                    Text(
                        text = formatDuration(
                            totalSeconds = uiState.session.timerRemainingSeconds,
                            displayFormat = uiState.settings.timerDisplayFormat,
                        ),
                        style = GymTheme.type.numericTimer,
                        color = GymTheme.colors.resting,
                    )
                }
            }
        } else if (uiState.session.timerDidFinish) {
            Text(
                text = stringResource(R.string.training_timer_finished),
                style = GymTheme.type.subheadlineSemibold,
                color = GymTheme.colors.textPrimary,
            )
        }
    }
}

@Composable
private fun ProStatusChip(
    isPro: Boolean,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (isPro) stringResource(R.string.pro_status_pro) else stringResource(R.string.pro_status_free),
            style = GymTheme.type.captionSemibold,
            color = if (isPro) GymTheme.colors.textPrimary else GymTheme.colors.textSecondary,
        )
        Icon(
            imageVector = Icons.Rounded.Verified,
            contentDescription = null,
            tint = if (isPro) GymTheme.colors.completed else GymTheme.colors.textSecondary,
            modifier = Modifier.size(GymTheme.spacing.s14),
        )
    }
}
