package com.alejandroestevemaza.gymtimerpro.design

import android.app.Activity
import android.net.Uri
import com.alejandroestevemaza.gymtimerpro.core.model.AppSettings
import com.alejandroestevemaza.gymtimerpro.core.model.BillingPeriod
import com.alejandroestevemaza.gymtimerpro.core.model.BillingPeriodUnit
import com.alejandroestevemaza.gymtimerpro.core.model.CalendarDayState
import com.alejandroestevemaza.gymtimerpro.core.model.CalendarWeekState
import com.alejandroestevemaza.gymtimerpro.core.model.DayCompletionState
import com.alejandroestevemaza.gymtimerpro.core.model.PeriodSummary
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumPlanKind
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumProduct
import com.alejandroestevemaza.gymtimerpro.core.model.ProgressBadgeId
import com.alejandroestevemaza.gymtimerpro.core.model.ProgressBadgeState
import com.alejandroestevemaza.gymtimerpro.core.model.ProgressBucket
import com.alejandroestevemaza.gymtimerpro.core.model.ProgressDerivedState
import com.alejandroestevemaza.gymtimerpro.core.model.ProgressPeriod
import com.alejandroestevemaza.gymtimerpro.core.model.RecentActivityCardState
import com.alejandroestevemaza.gymtimerpro.core.model.RecentActivityType
import com.alejandroestevemaza.gymtimerpro.core.model.Routine
import com.alejandroestevemaza.gymtimerpro.core.model.RoutineClassification
import com.alejandroestevemaza.gymtimerpro.core.model.TrainingSessionState
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallConfig
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallEntryPoint
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallInfoLevel
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallPlanDefaults
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallPresentationContext
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallPresentationRequest
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.copySpec
import com.alejandroestevemaza.gymtimerpro.feature.paywall.ui.PaywallScreenContent
import com.alejandroestevemaza.gymtimerpro.feature.paywall.ui.PaywallUiState
import com.alejandroestevemaza.gymtimerpro.feature.progress.ui.ProgressScreen
import com.alejandroestevemaza.gymtimerpro.feature.progress.ui.ProgressUiState
import com.alejandroestevemaza.gymtimerpro.feature.routines.ui.RoutineEditorState
import com.alejandroestevemaza.gymtimerpro.feature.routines.ui.RoutinesScreen
import com.alejandroestevemaza.gymtimerpro.feature.routines.ui.RoutinesUiState
import com.alejandroestevemaza.gymtimerpro.feature.settings.ui.SettingsScreen
import com.alejandroestevemaza.gymtimerpro.feature.settings.ui.SettingsPreviewMenu
import com.alejandroestevemaza.gymtimerpro.feature.training.ui.TrainingScreen
import com.alejandroestevemaza.gymtimerpro.feature.training.ui.TrainingUiState
import java.time.LocalDate
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w430dp-h932dp-xxhdpi")
class DesignScreensRoborazziTest {
    @Test
    fun training_idle_light() = captureScreenRoboImage("training__idle__light__pixel5") {
        TrainingScreen(
            uiState = TrainingUiState(
                isPro = true,
                session = TrainingSessionState(
                    totalSets = 4,
                    currentSet = 1,
                    restSeconds = 90,
                ),
            ),
            routines = listOf(sampleRoutine),
            classifications = listOf(sampleClassification),
            showRoutinePicker = false,
            pickerSearchQuery = "",
            pickerExpandedSectionId = null,
            onIncreaseTotalSets = {},
            onDecreaseTotalSets = {},
            onIncreaseRestSeconds = {},
            onDecreaseRestSeconds = {},
            onStartRest = {},
            onResetWorkout = {},
            onDismissDailyLimitDialog = {},
            onRequestPaywall = {},
            onOpenRoutinePicker = {},
            onDismissRoutinePicker = {},
            onPickerSearchQueryChanged = {},
            onPickerToggleSection = {},
            onApplyRoutine = {},
            onClearAppliedRoutine = {},
        )
    }

    @Test
    fun training_resting_light() = captureScreenRoboImage("training__resting__light__pixel5") {
        TrainingScreen(
            uiState = TrainingUiState(
                isPro = true,
                session = TrainingSessionState(
                    totalSets = 6,
                    currentSet = 2,
                    restSeconds = 60,
                    timerIsRunning = true,
                    timerRemainingSeconds = 57,
                ),
            ),
            routines = listOf(sampleRoutine),
            classifications = listOf(sampleClassification),
            showRoutinePicker = false,
            pickerSearchQuery = "",
            pickerExpandedSectionId = null,
            onIncreaseTotalSets = {},
            onDecreaseTotalSets = {},
            onIncreaseRestSeconds = {},
            onDecreaseRestSeconds = {},
            onStartRest = {},
            onResetWorkout = {},
            onDismissDailyLimitDialog = {},
            onRequestPaywall = {},
            onOpenRoutinePicker = {},
            onDismissRoutinePicker = {},
            onPickerSearchQueryChanged = {},
            onPickerToggleSection = {},
            onApplyRoutine = {},
            onClearAppliedRoutine = {},
        )
    }

    @Test
    fun training_completed_light() = captureScreenRoboImage("training__completed__light__pixel5") {
        TrainingScreen(
            uiState = TrainingUiState(
                isPro = true,
                session = TrainingSessionState(
                    totalSets = 1,
                    currentSet = 1,
                    completed = true,
                    restSeconds = 30,
                ),
            ),
            routines = listOf(sampleRoutine),
            classifications = listOf(sampleClassification),
            showRoutinePicker = false,
            pickerSearchQuery = "",
            pickerExpandedSectionId = null,
            onIncreaseTotalSets = {},
            onDecreaseTotalSets = {},
            onIncreaseRestSeconds = {},
            onDecreaseRestSeconds = {},
            onStartRest = {},
            onResetWorkout = {},
            onDismissDailyLimitDialog = {},
            onRequestPaywall = {},
            onOpenRoutinePicker = {},
            onDismissRoutinePicker = {},
            onPickerSearchQueryChanged = {},
            onPickerToggleSection = {},
            onApplyRoutine = {},
            onClearAppliedRoutine = {},
        )
    }

    @Test
    fun routines_empty_light() = captureScreenRoboImage("routines__empty__light__pixel5") {
        RoutinesScreen(
            uiState = RoutinesUiState(),
            onSearchQueryChanged = {},
            onToggleSection = {},
            onAddRoutine = {},
            onEditRoutine = {},
            onApplyRoutine = {},
            onOpenClassificationManager = {},
            onCloseClassificationManager = {},
            onClassificationSearchQueryChanged = {},
            onStartCreateClassification = {},
            onStartRenameClassification = {},
            onClassificationDraftChanged = {},
            onCancelClassificationDraft = {},
            onSaveClassificationDraft = {},
            onDeleteClassification = {},
            onDismissEditor = {},
            onEditorNameChanged = {},
            onEditorIncreaseSets = {},
            onEditorDecreaseSets = {},
            onEditorIncreaseReps = {},
            onEditorDecreaseReps = {},
            onEditorIncreaseRest = {},
            onEditorDecreaseRest = {},
            onEditorWeightChanged = {},
            onToggleClassification = {},
            onSaveEditor = {},
            onDeleteRoutine = {},
            onApplyOrRemoveFromTraining = {},
        )
    }

    @Test
    fun routines_catalog_light() = captureScreenRoboImage("routines__catalog__light__pixel5") {
        val catalogClassifications = listOf(
            RoutineClassification("c1", "Fuerza", "fuerza"),
            RoutineClassification("c2", "Hipertrofia", "hipertrofia"),
            RoutineClassification("c3", "Movilidad", "movilidad"),
            RoutineClassification("c4", "Resistencia", "resistencia"),
        )
        val unclassifiedRoutines = listOf(
            Routine(
                id = "r1",
                name = "Calistenia",
                totalSets = 6,
                reps = 12,
                restSeconds = 60,
                weightKg = 0.0,
                classifications = emptyList(),
                createdAtEpochMillis = 0L,
                updatedAtEpochMillis = 0L,
            ),
            Routine(
                id = "r2",
                name = "Full Body",
                totalSets = 4,
                reps = 8,
                restSeconds = 90,
                weightKg = 35.0,
                classifications = emptyList(),
                createdAtEpochMillis = 0L,
                updatedAtEpochMillis = 0L,
            ),
        )
        RoutinesScreen(
            uiState = RoutinesUiState(
                routines = unclassifiedRoutines,
                classifications = catalogClassifications,
            ),
            onSearchQueryChanged = {},
            onToggleSection = {},
            onAddRoutine = {},
            onEditRoutine = {},
            onApplyRoutine = {},
            onOpenClassificationManager = {},
            onCloseClassificationManager = {},
            onClassificationSearchQueryChanged = {},
            onStartCreateClassification = {},
            onStartRenameClassification = {},
            onClassificationDraftChanged = {},
            onCancelClassificationDraft = {},
            onSaveClassificationDraft = {},
            onDeleteClassification = {},
            onDismissEditor = {},
            onEditorNameChanged = {},
            onEditorIncreaseSets = {},
            onEditorDecreaseSets = {},
            onEditorIncreaseReps = {},
            onEditorDecreaseReps = {},
            onEditorIncreaseRest = {},
            onEditorDecreaseRest = {},
            onEditorWeightChanged = {},
            onToggleClassification = {},
            onSaveEditor = {},
            onDeleteRoutine = {},
            onApplyOrRemoveFromTraining = {},
        )
    }

    @Test
    fun routines_editor_light() = captureScreenRoboImage("routines__editor__light__pixel5") {
        RoutinesScreen(
            uiState = RoutinesUiState(
                routines = listOf(sampleRoutine),
                classifications = listOf(sampleClassification),
                editorState = RoutineEditorState(
                    routineId = sampleRoutine.id,
                    name = sampleRoutine.name,
                    nameCount = sampleRoutine.name.length,
                    totalSets = sampleRoutine.totalSets,
                    reps = sampleRoutine.reps,
                    restSeconds = sampleRoutine.restSeconds,
                    weightInput = "60",
                    selectedClassificationIds = setOf(sampleClassification.id),
                    isAppliedToTraining = true,
                ),
            ),
            onSearchQueryChanged = {},
            onToggleSection = {},
            onAddRoutine = {},
            onEditRoutine = {},
            onApplyRoutine = {},
            onOpenClassificationManager = {},
            onCloseClassificationManager = {},
            onClassificationSearchQueryChanged = {},
            onStartCreateClassification = {},
            onStartRenameClassification = {},
            onClassificationDraftChanged = {},
            onCancelClassificationDraft = {},
            onSaveClassificationDraft = {},
            onDeleteClassification = {},
            onDismissEditor = {},
            onEditorNameChanged = {},
            onEditorIncreaseSets = {},
            onEditorDecreaseSets = {},
            onEditorIncreaseReps = {},
            onEditorDecreaseReps = {},
            onEditorIncreaseRest = {},
            onEditorDecreaseRest = {},
            onEditorWeightChanged = {},
            onToggleClassification = {},
            onSaveEditor = {},
            onDeleteRoutine = {},
            onApplyOrRemoveFromTraining = {},
            previewShowEditorInline = true,
        )
    }

    @Test
    fun progress_month_light() = captureScreenRoboImage("progress__month__light__pixel5") {
        ProgressScreen(
            uiState = sampleProgressState(period = ProgressPeriod.Month, withSelectedDay = false),
            onSelectPeriod = {},
            onSelectDay = {},
            onDismissDayDetail = {},
        )
    }

    @Test
    fun progress_quarter_light() = captureScreenRoboImage("progress__quarter__light__pixel5") {
        ProgressScreen(
            uiState = sampleProgressState(period = ProgressPeriod.Quarter, withSelectedDay = false),
            onSelectPeriod = {},
            onSelectDay = {},
            onDismissDayDetail = {},
        )
    }

    @Test
    fun progress_selected_day_light() = captureScreenRoboImage("progress__selected-day__light__pixel5") {
        ProgressScreen(
            uiState = sampleProgressState(period = ProgressPeriod.Month, withSelectedDay = true),
            onSelectPeriod = {},
            onSelectDay = {},
            onDismissDayDetail = {},
            previewShowInlineDayDetail = true,
        )
    }

    @Test
    fun settings_default_light() = captureScreenRoboImage("settings__default__light__pixel5") {
        SettingsScreen(
            settings = AppSettings(),
            onWeightUnitPreferenceSelected = {},
            onTimerDisplayFormatSelected = {},
            onMaxSetsPreferenceSelected = {},
            onRestIncrementPreferenceSelected = {},
            onEnergySavingModeSelected = {},
            onManageClassifications = {},
        )
    }

    @Test
    fun settings_menu_open_light() = captureScreenRoboImage("settings__menu-open__light__pixel5") {
        SettingsScreen(
            settings = AppSettings(),
            onWeightUnitPreferenceSelected = {},
            onTimerDisplayFormatSelected = {},
            onMaxSetsPreferenceSelected = {},
            onRestIncrementPreferenceSelected = {},
            onEnergySavingModeSelected = {},
            onManageClassifications = {},
            previewExpandedMenu = SettingsPreviewMenu.WeightUnit,
        )
    }

    @Test
    fun paywall_standard_pro_light() = captureScreenRoboImage("paywall__standard-pro__light__pixel5") {
        val request = PaywallPresentationRequest(
            context = PaywallPresentationContext(
                entryPoint = PaywallEntryPoint.ProModule,
                infoLevel = PaywallInfoLevel.Standard,
            ),
            dailyLimit = 16,
            consumedToday = 4,
        )
        PaywallScreenContent(
            uiState = PaywallUiState(
                request = request,
                products = sampleProducts,
                selectedProductId = PaywallPlanDefaults.yearlyProductId,
            ),
            config = samplePaywallConfig,
            copySpec = request.context.copySpec(),
            onDismiss = {},
            onSelectProduct = {},
            onPurchase = { _: Activity -> },
            onRestore = {},
        )
    }

    @Test
    fun paywall_light_limit_light() = captureScreenRoboImage("paywall__light-limit__light__pixel5") {
        val request = PaywallPresentationRequest(
            context = PaywallPresentationContext(
                entryPoint = PaywallEntryPoint.DailyLimitDuringWorkout,
                infoLevel = PaywallInfoLevel.Light,
            ),
            dailyLimit = 16,
            consumedToday = 16,
        )
        PaywallScreenContent(
            uiState = PaywallUiState(
                request = request,
                products = sampleProducts,
                selectedProductId = PaywallPlanDefaults.yearlyProductId,
            ),
            config = samplePaywallConfig,
            copySpec = request.context.copySpec(),
            onDismiss = {},
            onSelectProduct = {},
            onPurchase = { _: Activity -> },
            onRestore = {},
        )
    }

    @Test
    fun paywall_detailed_pro_light() = captureScreenRoboImage("paywall__detailed-pro__light__pixel5") {
        val request = PaywallPresentationRequest(
            context = PaywallPresentationContext(
                entryPoint = PaywallEntryPoint.ProModule,
                infoLevel = PaywallInfoLevel.Detailed,
            ),
            dailyLimit = 16,
            consumedToday = 2,
        )
        PaywallScreenContent(
            uiState = PaywallUiState(
                request = request,
                products = sampleProducts,
                selectedProductId = PaywallPlanDefaults.yearlyProductId,
            ),
            config = samplePaywallConfig,
            copySpec = request.context.copySpec(),
            onDismiss = {},
            onSelectProduct = {},
            onPurchase = { _: Activity -> },
            onRestore = {},
        )
    }
}

private val sampleClassification = RoutineClassification(
    id = "c_strength",
    name = "Strength",
    normalizedName = "strength",
)

private val sampleRoutine = Routine(
    id = "r_push",
    name = "Push Day",
    totalSets = 4,
    reps = 10,
    restSeconds = 90,
    weightKg = 60.0,
    classifications = listOf(sampleClassification),
    createdAtEpochMillis = 0L,
    updatedAtEpochMillis = 0L,
)

private val sampleProducts = listOf(
    PremiumProduct(
        id = PaywallPlanDefaults.yearlyProductId,
        title = "Yearly",
        formattedPrice = "\$9.99",
        offerToken = "yearly_offer",
        planKind = PremiumPlanKind.Yearly,
        recurringPeriod = BillingPeriod(1, BillingPeriodUnit.Year),
        freeTrialPeriod = BillingPeriod(7, BillingPeriodUnit.Day),
    ),
    PremiumProduct(
        id = PaywallPlanDefaults.monthlyProductId,
        title = "Monthly",
        formattedPrice = "\$0.99",
        offerToken = "monthly_offer",
        planKind = PremiumPlanKind.Monthly,
        recurringPeriod = BillingPeriod(1, BillingPeriodUnit.Month),
        freeTrialPeriod = null,
    ),
)

private val samplePaywallConfig = PaywallConfig(
    termsUri = Uri.parse("https://example.com/terms"),
    privacyUri = Uri.parse("https://example.com/privacy"),
    manageSubscriptionUri = Uri.parse("https://play.google.com/store/account/subscriptions"),
)

private fun sampleProgressState(
    period: ProgressPeriod,
    withSelectedDay: Boolean,
): ProgressUiState {
    val monthStart = LocalDate.of(2026, 3, 1)
    val today = LocalDate.of(2026, 3, 13)
    val calendarStart = monthStart.minusDays(6)
    val weeks = (0 until 5).map { week ->
        CalendarWeekState(
            days = (0 until 7).map { day ->
                val date = calendarStart.plusDays((week * 7 + day).toLong())
                val inCurrentMonth = date.month == monthStart.month
                val workoutCount = if (inCurrentMonth && day % 3 == 0) 1 else 0
                CalendarDayState(
                    date = date,
                    inCurrentMonth = inCurrentMonth,
                    workoutCount = workoutCount,
                    isToday = date == today,
                    isFuture = date > today,
                )
            },
            showStreakIndicator = week < 4,
        )
    }

    val buckets = listOf(
        ProgressBucket("b1", monthStart, "W1", 2),
        ProgressBucket("b2", monthStart.plusDays(7), "W2", 3),
        ProgressBucket("b3", monthStart.plusDays(14), "W3", 1),
        ProgressBucket("b4", monthStart.plusDays(21), "W4", 4),
    )
    val derivedState = ProgressDerivedState(
        activeWeeklyStreak = 3,
        monthStart = monthStart,
        calendarWeeks = weeks,
        monthlyDayCounts = emptyMap(),
        dayCompletions = emptyMap(),
        recentActivities = listOf(
            RecentActivityCardState("a1", RecentActivityType.Classification, "Core", "Mar 13 at 3:51 PM"),
            RecentActivityCardState("a2", RecentActivityType.Routine, "Pierna", "Mar 13 at 3:51 PM"),
        ),
        badges = ProgressBadgeId.entries.mapIndexed { index, id ->
            ProgressBadgeState(id = id, unlocked = index < 2)
        },
        periodSummary = PeriodSummary(
            period = period,
            totalWorkouts = 10,
            activeDays = 8,
            mostRepeatedRoutineName = "Push Day",
            topClassificationName = "Strength",
            buckets = buckets,
        ),
    )
    return ProgressUiState(
        selectedPeriod = period,
        derivedState = derivedState,
        selectedDay = if (withSelectedDay) today else null,
        selectedDayCompletions = if (withSelectedDay) {
            listOf(
                DayCompletionState(
                    id = "d1",
                    routineName = "Pierna",
                    completionTimeText = "3:51PM",
                ),
                DayCompletionState(
                    id = "d2",
                    routineName = "Core",
                    completionTimeText = "11:19 AM",
                ),
            )
        } else {
            emptyList()
        },
    )
}
