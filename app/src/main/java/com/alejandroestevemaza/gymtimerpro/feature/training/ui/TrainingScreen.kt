package com.alejandroestevemaza.gymtimerpro.feature.training.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alejandroestevemaza.gymtimerpro.R
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.GymComponentState
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.NumericConfigRow
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.PrimaryCtaButton
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.RoutineCatalogRow
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.RoutineCatalogSearchBar
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.RoutineCatalogSectionHeader
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
    val lifecycleOwner = LocalLifecycleOwner.current
    var notificationsEnabled by remember {
        mutableStateOf(NotificationManagerCompat.from(context).areNotificationsEnabled())
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    var showRoutinePicker by remember { mutableStateOf(false) }
    var pickerSearchQuery by remember { mutableStateOf("") }
    var pickerExpandedSectionId by remember { mutableStateOf<String?>(null) }
    var hasRequestedNotificationPermission by rememberSaveable {
        mutableStateOf(
            context.getSharedPreferences(PERMISSION_PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_POST_NOTIFICATIONS_REQUESTED, false)
        )
    }
    var soundTipDismissed by remember {
        mutableStateOf(!shouldShowSoundTipToday(context))
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
            restFinishedSoundPlayer = appContainer.restFinishedSoundPlayer,
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
        notificationsEnabled = notificationsEnabled,
        soundTipDismissed = soundTipDismissed,
        onDismissSoundTip = { soundTipDismissed = true },
        showRoutinePicker = showRoutinePicker,
        pickerSearchQuery = pickerSearchQuery,
        pickerExpandedSectionId = pickerExpandedSectionId,
        onTotalSetsChanged = trainingViewModel::onTotalSetsChanged,
        onRestSecondsChanged = trainingViewModel::onRestSecondsChanged,
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
private const val KEY_SOUND_TIP_LAST_DATE = "sound_tip.last_date"
private const val KEY_SOUND_TIP_DISTINCT_DAYS = "sound_tip.distinct_days"
private const val SOUND_TIP_MAX_DAYS = 4


@Composable
fun TrainingScreen(
    uiState: TrainingUiState,
    routines: List<Routine>,
    classifications: List<RoutineClassification>,
    notificationsEnabled: Boolean = true,
    soundTipDismissed: Boolean = false,
    onDismissSoundTip: () -> Unit = {},
    showRoutinePicker: Boolean,
    pickerSearchQuery: String,
    pickerExpandedSectionId: String?,
    onTotalSetsChanged: (Int) -> Unit,
    onRestSecondsChanged: (Int) -> Unit,
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
                leadingIcon = Icons.Rounded.Pause,
                leadingIconInCircle = true,
                uppercaseText = true,
                boldText = true,
                modifier = Modifier
                    .padding(
                        start = GymTheme.spacing.s20,
                        end = GymTheme.spacing.s20,
                        bottom = GymTheme.spacing.s12,
                    ),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = GymTheme.spacing.s20,
                end = GymTheme.spacing.s20,
                top = GymTheme.spacing.s12,
                bottom = GymTheme.spacing.s12,
            ),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s20),
        ) {
            if (!notificationsEnabled) {
                item {
                    NotificationsDisabledBanner()
                }
            }
            if (!soundTipDismissed) {
                item {
                    SoundReminderBanner(onDismiss = onDismissSoundTip)
                }
            }
            item {
                TrainingConfigurationCard(
                    uiState = uiState,
                    onOpenRoutinePicker = onOpenRoutinePicker,
                    onTotalSetsChanged = onTotalSetsChanged,
                    onRestSecondsChanged = onRestSecondsChanged,
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
    onTotalSetsChanged: (Int) -> Unit,
    onRestSecondsChanged: (Int) -> Unit,
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
        Column {
            RoutineSummaryRow(
                uiState = uiState,
                onOpenRoutinePicker = onOpenRoutinePicker,
            )
            HorizontalDivider(color = GymTheme.colors.divider)

            NumericConfigRow(
                icon = Icons.Rounded.Layers,
                title = stringResource(R.string.training_sets_label),
                valueText = uiState.session.totalSets.toString(),
                value = uiState.session.totalSets,
                valueRange = 1..uiState.settings.maxSetsPreference.maxSets,
                valueStep = 1,
                onValueChange = onTotalSetsChanged,
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
                value = uiState.session.restSeconds,
                valueRange = 15..300,
                valueStep = uiState.settings.restIncrementPreference.seconds,
                onValueChange = onRestSecondsChanged,
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
}

@Composable
private fun RoutineSummaryRow(
    uiState: TrainingUiState,
    onOpenRoutinePicker: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = GymTheme.layout.minTapHeight)
            .clickable(onClick = onOpenRoutinePicker)
            .padding(vertical = GymTheme.spacing.s2),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(GymTheme.layout.configIconFrame)
                    .background(
                        color = GymTheme.colors.iconBackground,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.r8),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.FitnessCenter,
                    contentDescription = null,
                    tint = GymTheme.colors.iconTint,
                    modifier = Modifier.size(GymTheme.layout.configIconGlyph),
                )
            }
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
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = stringResource(R.string.training_select_routine))
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(GymTheme.layout.minTapHeight),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.routines_cancel),
                        tint = GymTheme.colors.iconTint,
                        modifier = Modifier.size(GymTheme.layout.icon18),
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
            ) {
                RoutineCatalogSearchBar(
                    query = pickerState.searchQuery,
                    onQueryChange = onSearchQueryChanged,
                    placeholder = stringResource(R.string.routines_search_hint),
                )

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

                if (pickerState.appliedRoutineId != null) {
                    OutlinedButton(
                        onClick = onClearAppliedRoutine,
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(
                            width = GymTheme.borders.card,
                            color = GymTheme.colors.error.copy(alpha = 0.45f),
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = GymTheme.colors.error,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            modifier = Modifier.size(GymTheme.layout.icon18),
                        )
                        Spacer(modifier = Modifier.width(GymTheme.spacing.s8))
                        Text(text = stringResource(R.string.training_remove_routine))
                    }
                }
            }
        },
        confirmButton = {},
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
            val sectionTitle = when (section.id) {
                RoutinesUiState.UNCLASSIFIED_SECTION_ID -> stringResource(R.string.routines_unclassified)
                RoutinesUiState.MATCHING_ROUTINES_SECTION_ID -> stringResource(R.string.routines_matching_routines)
                else -> section.title
            }
            RoutineCatalogSectionHeader(
                title = sectionTitle,
                routineCount = section.routines.size,
                isExpanded = pickerState.isSearchMode || section.isExpanded,
                onClick = { if (!pickerState.isSearchMode) onToggleSection(section.id) },
            )

            if (pickerState.isSearchMode || section.isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8)) {
                    section.routines.forEach { routine ->
                        val isApplied = pickerState.appliedRoutineId == routine.id
                        RoutineCatalogRow(
                            name = routine.name,
                            summary = formatRoutineSummary(
                                totalSets = routine.totalSets,
                                reps = routine.reps,
                                restSeconds = routine.restSeconds,
                                weightKg = routine.weightKg,
                                timerDisplayFormat = settings.timerDisplayFormat,
                                weightUnitPreference = settings.weightUnitPreference,
                            ),
                            onClick = { onApplyRoutine(routine.id) },
                            isHighlighted = isApplied,
                            trailing = {
                                Box(
                                    modifier = Modifier
                                        .size(GymTheme.layout.configIconFrame)
                                        .background(
                                            color = if (isApplied) {
                                                GymTheme.colors.iconTint.copy(alpha = 0.16f)
                                            } else {
                                                GymTheme.colors.cardBackground
                                            },
                                            shape = RoundedCornerShape(GymTheme.radii.r8),
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = if (isApplied) Icons.Rounded.Check else Icons.Rounded.Add,
                                        contentDescription = null,
                                        tint = if (isApplied) GymTheme.colors.iconTint else GymTheme.colors.textSecondary,
                                        modifier = Modifier.size(GymTheme.layout.icon18),
                                    )
                                }
                            },
                        )
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
    val isCompleted = uiState.session.completed

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
            if (uiState.canReset && !isCompleted) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(color = GymTheme.colors.secondaryButtonFill)
                        .clickable { onResetWorkout() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.RestartAlt,
                        contentDescription = stringResource(R.string.training_reset),
                        tint = GymTheme.colors.textSecondary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        },
    ) {
        if (GymTheme.animationsEnabled) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioNoBouncy,
                        ),
                    ),
            ) {
                AnimatedContent(
                    targetState = isCompleted,
                    transitionSpec = {
                        (
                            fadeIn(animationSpec = tween(durationMillis = 220, delayMillis = 40)) +
                                expandVertically(animationSpec = spring(stiffness = Spring.StiffnessLow))
                            ) togetherWith (
                            fadeOut(animationSpec = tween(durationMillis = 140)) +
                                shrinkVertically(animationSpec = tween(durationMillis = 180))
                            ) using SizeTransform(clip = false)
                    },
                    label = "TrainingProgressCompletionTransition",
                ) { completed ->
                    if (completed) {
                        WorkoutCompletedBanner()
                    } else {
                        TrainingProgressLiveContent(uiState = uiState)
                    }
                }
            }
        } else {
            if (isCompleted) {
                WorkoutCompletedBanner()
            } else {
                TrainingProgressLiveContent(uiState = uiState)
            }
        }
    }
}

@Composable
private fun TrainingProgressLiveContent(
    uiState: TrainingUiState,
) {
    val stateLabel = if (uiState.session.timerIsRunning) {
        stringResource(R.string.training_state_resting).uppercase()
    } else {
        stringResource(R.string.training_state_training).uppercase()
    }
    val stateColor = if (uiState.session.timerIsRunning) {
        GymTheme.colors.resting
    } else {
        GymTheme.colors.training
    }
    val stateIcon = if (uiState.session.timerIsRunning) {
        Icons.Rounded.Timer
    } else {
        Icons.Rounded.FitnessCenter
    }

    Column(verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s10)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.r16),
            color = GymTheme.colors.trainingMetricBackground,
        ) {
            val routineReps = uiState.session.appliedRoutineReps
            if (routineReps != null) {
                // 50/50: sets | reps de la rutina seleccionada
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(GymTheme.spacing.s12),
                    horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
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
                    VerticalDivider(color = GymTheme.colors.divider)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s6),
                    ) {
                        Text(
                            text = stringResource(R.string.routines_reps_label).uppercase(),
                            style = GymTheme.type.captionRegular,
                            color = GymTheme.colors.textSecondary,
                        )
                        Text(
                            text = routineReps.toString(),
                            style = GymTheme.type.numericMetric,
                            color = GymTheme.colors.textPrimary,
                        )
                    }
                }
            } else {
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
                    imageVector = stateIcon,
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
                    AnimatedTimerText(
                        text = formatDuration(
                            totalSeconds = uiState.session.timerRemainingSeconds,
                            displayFormat = uiState.settings.timerDisplayFormat,
                        ),
                        color = GymTheme.colors.resting,
                        animate = GymTheme.animationsEnabled,
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedTimerText(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    animate: Boolean = true,
) {
    if (!animate) {
        Text(
            text = text,
            style = GymTheme.type.numericTimer,
            color = color,
        )
        return
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        text.forEachIndexed { index, char ->
            androidx.compose.runtime.key(index) {
                AnimatedContent(
                    targetState = char,
                    transitionSpec = {
                        (slideInVertically(animationSpec = tween(200)) { it } + fadeIn(tween(200))) togetherWith
                            (slideOutVertically(animationSpec = tween(200)) { -it } + fadeOut(tween(200))) using
                            SizeTransform(clip = true)
                    },
                    label = "timer_char_$index",
                ) { targetChar ->
                    Text(
                        text = targetChar.toString(),
                        style = GymTheme.type.numericTimer,
                        color = color,
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutCompletedBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.r16),
        color = GymTheme.colors.completed.copy(alpha = 0.1f),
        border = BorderStroke(
            width = GymTheme.borders.quaternary,
            color = GymTheme.colors.completed.copy(alpha = 0.28f),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = GymTheme.spacing.s16,
                    vertical = GymTheme.spacing.s14,
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(GymTheme.spacing.s24)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(GymTheme.colors.completed.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = GymTheme.colors.completed,
                    modifier = Modifier.size(GymTheme.spacing.s16),
                )
            }
            Spacer(modifier = Modifier.width(GymTheme.spacing.s10))
            Text(
                text = stringResource(R.string.training_completed_title),
                style = GymTheme.type.title2Bold,
                color = GymTheme.colors.completed,
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

@Composable
private fun SoundReminderBanner(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = GymTheme.colors.iconTint.copy(alpha = 0.10f),
                shape = RoundedCornerShape(GymTheme.radii.r12),
            )
            .padding(
                start = GymTheme.spacing.s16,
                top = GymTheme.spacing.s12,
                bottom = GymTheme.spacing.s12,
                end = GymTheme.spacing.s4,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
            contentDescription = null,
            tint = GymTheme.colors.iconTint,
            modifier = Modifier.size(GymTheme.spacing.s20),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s2),
        ) {
            Text(
                text = stringResource(R.string.training_sound_tip_title),
                style = GymTheme.type.subheadlineSemibold,
                color = GymTheme.colors.iconTint,
            )
            Text(
                text = stringResource(R.string.training_sound_tip_body),
                style = GymTheme.type.captionRegular,
                color = GymTheme.colors.textSecondary,
            )
        }
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = null,
                tint = GymTheme.colors.textSecondary,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun NotificationsDisabledBanner() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = GymTheme.colors.error.copy(alpha = 0.10f),
                shape = RoundedCornerShape(GymTheme.radii.r12),
            )
            .padding(
                horizontal = GymTheme.spacing.s16,
                vertical = GymTheme.spacing.s12,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
    ) {
        Icon(
            imageVector = Icons.Rounded.NotificationsOff,
            contentDescription = null,
            tint = GymTheme.colors.error,
            modifier = Modifier.size(GymTheme.spacing.s20),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s2),
        ) {
            Text(
                text = stringResource(R.string.training_notifications_disabled),
                style = GymTheme.type.subheadlineSemibold,
                color = GymTheme.colors.error,
            )
            Text(
                text = stringResource(R.string.training_notifications_disabled_body),
                style = GymTheme.type.captionRegular,
                color = GymTheme.colors.textSecondary,
            )
        }
        TextButton(
            onClick = {
                context.startActivity(
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                )
            },
        ) {
            Text(
                text = stringResource(R.string.training_notifications_enable),
                style = GymTheme.type.subheadlineSemibold,
                color = GymTheme.colors.error,
            )
        }
    }
}

private fun shouldShowSoundTipToday(context: Context): Boolean {
    val prefs = context.getSharedPreferences(PERMISSION_PREFS_NAME, Context.MODE_PRIVATE)
    val distinctDays = prefs.getInt(KEY_SOUND_TIP_DISTINCT_DAYS, 0)
    if (distinctDays >= SOUND_TIP_MAX_DAYS) return false
    val today = java.time.LocalDate.now().toString()
    if (prefs.getString(KEY_SOUND_TIP_LAST_DATE, null) == today) return false
    prefs.edit()
        .putString(KEY_SOUND_TIP_LAST_DATE, today)
        .putInt(KEY_SOUND_TIP_DISTINCT_DAYS, distinctDays + 1)
        .apply()
    return true
}
