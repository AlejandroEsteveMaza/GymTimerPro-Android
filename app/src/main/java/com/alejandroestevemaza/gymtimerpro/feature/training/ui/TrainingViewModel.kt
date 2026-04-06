package com.alejandroestevemaza.gymtimerpro.feature.training.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alejandroestevemaza.gymtimerpro.R
import com.alejandroestevemaza.gymtimerpro.core.model.AppSettings
import com.alejandroestevemaza.gymtimerpro.core.model.DailyUsageState
import com.alejandroestevemaza.gymtimerpro.core.model.TrainingDefaults
import com.alejandroestevemaza.gymtimerpro.core.model.TrainingSessionState
import com.alejandroestevemaza.gymtimerpro.core.model.WorkoutCompletion
import com.alejandroestevemaza.gymtimerpro.data.preferences.AppSettingsRepository
import com.alejandroestevemaza.gymtimerpro.data.preferences.PremiumStateRepository
import com.alejandroestevemaza.gymtimerpro.data.preferences.TrainingSessionRepository
import com.alejandroestevemaza.gymtimerpro.data.repository.RoutinesRepository
import com.alejandroestevemaza.gymtimerpro.data.repository.TrainingSessionCoordinator
import com.alejandroestevemaza.gymtimerpro.data.repository.WorkoutCompletionRepository
import com.alejandroestevemaza.gymtimerpro.feature.training.notifications.RestFinishedSoundPlayer
import com.alejandroestevemaza.gymtimerpro.feature.training.notifications.RestNotificationCoordinator
import com.alejandroestevemaza.gymtimerpro.core.util.AppForegroundState
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrainingViewModel(
    private val appSettingsRepository: AppSettingsRepository,
    private val premiumStateRepository: PremiumStateRepository,
    private val trainingSessionRepository: TrainingSessionRepository,
    private val routinesRepository: RoutinesRepository,
    private val trainingSessionCoordinator: TrainingSessionCoordinator,
    private val workoutCompletionRepository: WorkoutCompletionRepository,
    private val restNotificationCoordinator: RestNotificationCoordinator,
    private val restFinishedSoundPlayer: RestFinishedSoundPlayer,
    private val clock: Clock,
    private val quickWorkoutLabel: String,
) : ViewModel() {
    private val showDailyLimitDialog = MutableStateFlow(false)

    private var timerJob: Job? = null
    private var activeTimerEndEpochMillis: Long? = null
    private var completionResetJob: Job? = null
    private var scheduledCompletionEpochMillis: Long? = null

    val uiState: StateFlow<TrainingUiState> = combine(
        appSettingsRepository.settings,
        premiumStateRepository.isPro,
        trainingSessionRepository.sessionState,
        trainingSessionRepository.dailyUsageState,
        showDailyLimitDialog,
    ) { settings, isPro, session, dailyUsage, showLimitDialog ->
        TrainingUiState(
            settings = settings,
            isPro = isPro,
            session = session,
            dailyUsage = dailyUsage,
            showDailyLimitDialog = showLimitDialog,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TrainingUiState(),
    )

    init {
        viewModelScope.launch {
            combine(
                appSettingsRepository.settings,
                trainingSessionRepository.sessionState,
            ) { settings, session -> settings to session }
                .collect { (settings, session) ->
                    enforceTrainingConstraints(settings, session)
                    synchronizeTimer(settings, session)
                    synchronizeCompletionReset(session)
                }
        }

        viewModelScope.launch {
            trainingSessionRepository.dailyUsageState.collect { usage ->
                normalizeDailyUsageIfNeeded(usage)
            }
        }
    }

    fun onIncreaseTotalSets() {
        val state = uiState.value
        if (!state.canEditConfiguration) return
        val target = (state.session.totalSets + 1).coerceAtMost(state.settings.maxSetsPreference.maxSets)
        onTotalSetsChanged(target)
    }

    fun onDecreaseTotalSets() {
        val state = uiState.value
        if (!state.canEditConfiguration) return
        val target = (state.session.totalSets - 1).coerceAtLeast(TrainingDefaults.minSets)
        onTotalSetsChanged(target)
    }

    fun onTotalSetsChanged(targetValue: Int) {
        val state = uiState.value
        if (!state.canEditConfiguration) return
        val target = targetValue
            .coerceAtLeast(TrainingDefaults.minSets)
            .coerceAtMost(state.settings.maxSetsPreference.maxSets)
        if (target == state.session.totalSets) return
        persistSession { session ->
            session.copy(
                totalSets = target,
                currentSet = session.currentSet.coerceAtMost(target),
            )
        }
    }

    fun onIncreaseRestSeconds() {
        val state = uiState.value
        if (!state.canEditConfiguration) return
        val step = state.settings.restIncrementPreference.seconds
        val target = (state.session.restSeconds + step).coerceAtMost(TrainingDefaults.maxRestSeconds)
        onRestSecondsChanged(target)
    }

    fun onDecreaseRestSeconds() {
        val state = uiState.value
        if (!state.canEditConfiguration) return
        val step = state.settings.restIncrementPreference.seconds
        val target = (state.session.restSeconds - step).coerceAtLeast(TrainingDefaults.minRestSeconds)
        onRestSecondsChanged(target)
    }

    fun onRestSecondsChanged(targetValue: Int) {
        val state = uiState.value
        if (!state.canEditConfiguration) return
        val step = state.settings.restIncrementPreference.seconds
        val clampedTarget = targetValue
            .coerceIn(TrainingDefaults.minRestSeconds, TrainingDefaults.maxRestSeconds)
        val normalizedTarget = TrainingDefaults.minRestSeconds +
            (((clampedTarget - TrainingDefaults.minRestSeconds) / step) * step)
        if (normalizedTarget == state.session.restSeconds) return
        persistSession { session -> session.copy(restSeconds = normalizedTarget) }
    }

    fun onStartRest() {
        val state = uiState.value
        if (!state.startRestEnabled) return

        viewModelScope.launch {
            if (state.session.currentSet >= state.session.totalSets) {
                completeWorkout(state.session)
                return@launch
            }

            val normalizedUsage = normalizedDailyUsage(state.dailyUsage)
            if (!state.isPro && normalizedUsage.consumedCount >= TrainingDefaults.dailyFreeUsageLimit) {
                if (normalizedUsage != state.dailyUsage) {
                    trainingSessionRepository.updateDailyUsage { normalizedUsage }
                }
                showDailyLimitDialog.value = true
                return@launch
            }

            if (!state.isPro) {
                trainingSessionRepository.updateDailyUsage { usage ->
                    val currentUsage = normalizedDailyUsage(usage)
                    currentUsage.copy(consumedCount = currentUsage.consumedCount + 1)
                }
            }

            val restSeconds = state.session.restSeconds
                .coerceIn(TrainingDefaults.minRestSeconds, TrainingDefaults.maxRestSeconds)
            val endEpochMillis = clock.millis() + restSeconds * 1_000L

            trainingSessionRepository.updateSession { session ->
                session.copy(
                    currentSet = (session.currentSet + 1).coerceAtMost(session.totalSets),
                    completed = false,
                    timerIsRunning = true,
                    timerEndEpochMillis = endEpochMillis,
                    timerRemainingSeconds = restSeconds,
                    timerDidFinish = false,
                    completedAtEpochMillis = null,
                )
            }
        }
    }

    fun onResetWorkout() {
        restNotificationCoordinator.clearAll()
        persistSession { session ->
            session.copy(
                currentSet = TrainingDefaults.currentSet,
                completed = false,
                timerIsRunning = false,
                timerEndEpochMillis = null,
                timerRemainingSeconds = 0,
                timerDidFinish = false,
                completedAtEpochMillis = null,
            )
        }
    }

    fun onDismissDailyLimitDialog() {
        showDailyLimitDialog.value = false
    }

    fun onApplyRoutine(routineId: String) {
        viewModelScope.launch {
            val snapshot = routinesRepository.getRoutineSelectionSnapshot(routineId) ?: return@launch
            trainingSessionCoordinator.applyRoutine(
                snapshot = snapshot,
                maxSets = uiState.value.settings.maxSetsPreference.maxSets,
            )
        }
    }

    fun onClearAppliedRoutine() {
        viewModelScope.launch {
            trainingSessionCoordinator.clearAppliedRoutine()
        }
    }

    private fun persistSession(transform: (TrainingSessionState) -> TrainingSessionState) {
        viewModelScope.launch {
            trainingSessionRepository.updateSession(transform)
        }
    }

    private suspend fun completeWorkout(session: TrainingSessionState) {
        restNotificationCoordinator.clearAll()
        val completedAtEpochMillis = clock.millis()
        workoutCompletionRepository.insertCompletion(
            WorkoutCompletion(
                id = UUID.randomUUID().toString(),
                completedAtEpochMillis = completedAtEpochMillis,
                routineId = session.appliedRoutineId,
                routineNameSnapshot = session.appliedRoutineName
                    ?.takeIf { value -> value.isNotBlank() }
                    ?: quickWorkoutLabel,
                classificationId = session.appliedClassificationId,
                classificationNameSnapshot = session.appliedClassificationName,
                durationSeconds = null,
                notes = null,
            )
        )
        trainingSessionRepository.updateSession { current ->
            current.copy(
                completed = true,
                timerIsRunning = false,
                timerEndEpochMillis = null,
                timerRemainingSeconds = 0,
                timerDidFinish = false,
                completedAtEpochMillis = completedAtEpochMillis,
            )
        }
    }

    private suspend fun enforceTrainingConstraints(
        settings: AppSettings,
        session: TrainingSessionState,
    ) {
        val clampedTotalSets = session.totalSets
            .coerceAtLeast(TrainingDefaults.minSets)
            .coerceAtMost(settings.maxSetsPreference.maxSets)
        val clampedCurrentSet = session.currentSet.coerceAtMost(clampedTotalSets)
        val clampedRestSeconds = session.restSeconds
            .coerceIn(TrainingDefaults.minRestSeconds, TrainingDefaults.maxRestSeconds)

        if (
            clampedTotalSets == session.totalSets &&
            clampedCurrentSet == session.currentSet &&
            clampedRestSeconds == session.restSeconds
        ) {
            return
        }

        trainingSessionRepository.updateSession { current ->
            current.copy(
                totalSets = clampedTotalSets,
                currentSet = clampedCurrentSet,
                restSeconds = clampedRestSeconds,
            )
        }
    }

    private fun synchronizeTimer(
        settings: AppSettings,
        session: TrainingSessionState,
    ) {
        val endEpochMillis = session.timerEndEpochMillis
        if (!session.timerIsRunning || endEpochMillis == null) {
            restNotificationCoordinator.syncRestState(session)
            timerJob?.cancel()
            timerJob = null
            activeTimerEndEpochMillis = null
            return
        }

        if (activeTimerEndEpochMillis == endEpochMillis && timerJob?.isActive == true) {
            return
        }

        restNotificationCoordinator.syncRestState(session)
        timerJob?.cancel()
        activeTimerEndEpochMillis = endEpochMillis
        timerJob = viewModelScope.launch {
            var lastPersistedRemainingSeconds: Int? = null
            while (true) {
                val remainingSeconds = computeRemainingSeconds(endEpochMillis)
                if (remainingSeconds <= 0) {
                    restNotificationCoordinator.notifyRestFinished(
                        currentSet = session.currentSet,
                        totalSets = session.totalSets,
                    )
                    if (AppForegroundState.isForeground()) {
                        restFinishedSoundPlayer.play(settings.energySavingMode)
                    }
                    trainingSessionRepository.updateSession { current ->
                        current.copy(
                            timerIsRunning = false,
                            timerEndEpochMillis = null,
                            timerRemainingSeconds = 0,
                            timerDidFinish = true,
                        )
                    }
                    break
                }

                if (lastPersistedRemainingSeconds != remainingSeconds) {
                    lastPersistedRemainingSeconds = remainingSeconds
                    trainingSessionRepository.updateSession { current ->
                        current.copy(
                            timerRemainingSeconds = remainingSeconds,
                            timerDidFinish = false,
                        )
                    }
                }

                delay(250)
            }
        }
    }

    private fun synchronizeCompletionReset(session: TrainingSessionState) {
        val completedAtEpochMillis = session.completedAtEpochMillis
        if (!session.completed || completedAtEpochMillis == null) {
            completionResetJob?.cancel()
            completionResetJob = null
            scheduledCompletionEpochMillis = null
            return
        }

        if (scheduledCompletionEpochMillis == completedAtEpochMillis && completionResetJob?.isActive == true) {
            return
        }

        completionResetJob?.cancel()
        scheduledCompletionEpochMillis = completedAtEpochMillis
        completionResetJob = viewModelScope.launch {
            val resetDelayMillis = (completedAtEpochMillis + TrainingDefaults.completionResetDelayMillis) -
                clock.millis()
            delay(resetDelayMillis.coerceAtLeast(0L))
            trainingSessionRepository.updateSession { current ->
                if (!current.completed || current.completedAtEpochMillis != completedAtEpochMillis) {
                    current
                } else {
                    current.copy(
                        currentSet = TrainingDefaults.currentSet,
                        completed = false,
                        timerIsRunning = false,
                        timerEndEpochMillis = null,
                        timerRemainingSeconds = 0,
                        timerDidFinish = false,
                        completedAtEpochMillis = null,
                    )
                }
            }
        }
    }

    private suspend fun normalizeDailyUsageIfNeeded(current: DailyUsageState) {
        val normalized = normalizedDailyUsage(current)
        if (normalized != current) {
            trainingSessionRepository.updateDailyUsage { normalized }
        }
    }

    private fun normalizedDailyUsage(current: DailyUsageState): DailyUsageState {
        val todayStartEpochMillis = Instant.now(clock)
            .atZone(clock.zone)
            .truncatedTo(ChronoUnit.DAYS)
            .toInstant()
            .toEpochMilli()
        return if (current.dayStartEpochMillis == todayStartEpochMillis) {
            current
        } else {
            DailyUsageState(
                dayStartEpochMillis = todayStartEpochMillis,
                consumedCount = 0,
            )
        }
    }

    private fun computeRemainingSeconds(endEpochMillis: Long): Int {
        val diffMillis = endEpochMillis - clock.millis()
        if (diffMillis <= 0L) return 0
        return ((diffMillis + 999L) / 1_000L).toInt()
    }

    override fun onCleared() {
        restFinishedSoundPlayer.release()
        super.onCleared()
    }

    companion object {
        fun factory(
            appSettingsRepository: AppSettingsRepository,
            premiumStateRepository: PremiumStateRepository,
            trainingSessionRepository: TrainingSessionRepository,
            routinesRepository: RoutinesRepository,
            trainingSessionCoordinator: TrainingSessionCoordinator,
            workoutCompletionRepository: WorkoutCompletionRepository,
            restNotificationCoordinator: RestNotificationCoordinator,
            restFinishedSoundPlayer: RestFinishedSoundPlayer,
            quickWorkoutLabel: String,
            clock: Clock = Clock.systemDefaultZone(),
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TrainingViewModel(
                    appSettingsRepository = appSettingsRepository,
                    premiumStateRepository = premiumStateRepository,
                    trainingSessionRepository = trainingSessionRepository,
                    routinesRepository = routinesRepository,
                    trainingSessionCoordinator = trainingSessionCoordinator,
                    workoutCompletionRepository = workoutCompletionRepository,
                    restNotificationCoordinator = restNotificationCoordinator,
                    restFinishedSoundPlayer = restFinishedSoundPlayer,
                    clock = clock,
                    quickWorkoutLabel = quickWorkoutLabel,
                ) as T
            }
        }
    }
}
