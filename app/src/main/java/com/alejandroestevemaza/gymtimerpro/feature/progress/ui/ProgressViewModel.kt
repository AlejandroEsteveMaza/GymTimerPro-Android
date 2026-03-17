package com.alejandroestevemaza.gymtimerpro.feature.progress.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alejandroestevemaza.gymtimerpro.core.model.ProgressDerivedState
import com.alejandroestevemaza.gymtimerpro.core.model.ProgressPeriod
import com.alejandroestevemaza.gymtimerpro.data.repository.WorkoutCompletionRepository
import java.time.Clock
import java.time.LocalDate
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProgressViewModel(
    private val workoutCompletionRepository: WorkoutCompletionRepository,
    private val quickWorkoutLabel: String,
    private val clock: Clock,
) : ViewModel() {
    private val selectedPeriod = MutableStateFlow(ProgressPeriod.Week)
    private val selectedDay = MutableStateFlow<LocalDate?>(null)
    private val derivedState = MutableStateFlow(
        progressCalculator().calculate(
            completions = emptyList(),
            selectedPeriod = selectedPeriod.value,
        )
    )
    private var lastSignature: ProgressSignature? = null

    val uiState: StateFlow<ProgressUiState> = combine(
        selectedPeriod,
        selectedDay,
        derivedState,
    ) { selectedPeriod, selectedDay, derivedState ->
        val calculator = progressCalculator()
        ProgressUiState(
            selectedPeriod = selectedPeriod,
            derivedState = derivedState,
            selectedDay = selectedDay,
            selectedDayCompletions = if (selectedDay != null) {
                calculator.dayCompletionStates(
                    date = selectedDay,
                    completions = derivedState.dayCompletions,
                )
            } else {
                emptyList()
            },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProgressUiState(
            selectedPeriod = selectedPeriod.value,
            derivedState = derivedState.value,
        ),
    )

    init {
        viewModelScope.launch {
            combine(
                workoutCompletionRepository.completions,
                selectedPeriod,
            ) { completions, selectedPeriod ->
                completions to selectedPeriod
            }.collect { (completions, selectedPeriod) ->
                val signature = ProgressSignature(
                    totalCount = completions.size,
                    firstId = completions.firstOrNull()?.id,
                    firstTimestamp = completions.firstOrNull()?.completedAtEpochMillis,
                    lastId = completions.lastOrNull()?.id,
                    lastTimestamp = completions.lastOrNull()?.completedAtEpochMillis,
                    period = selectedPeriod,
                    localeTag = Locale.getDefault().toLanguageTag(),
                    today = LocalDate.now(clock),
                )
                if (signature == lastSignature) return@collect

                val computedState = withContext(Dispatchers.Default) {
                    progressCalculator().calculate(
                        completions = completions,
                        selectedPeriod = selectedPeriod,
                    )
                }
                lastSignature = signature
                derivedState.value = computedState
                val currentSelectedDay = selectedDay.value
                if (currentSelectedDay != null) {
                    val isValidSelectedDay = computedState.dayCompletions[currentSelectedDay].orEmpty().isNotEmpty() &&
                        currentSelectedDay.month == computedState.monthStart.month &&
                        currentSelectedDay.year == computedState.monthStart.year
                    if (!isValidSelectedDay) {
                        selectedDay.value = null
                    }
                }
            }
        }
    }

    fun onSelectPeriod(period: ProgressPeriod) {
        selectedPeriod.value = period
    }

    fun onSelectDay(date: LocalDate) {
        val currentState = derivedState.value ?: return
        val hasWorkouts = currentState.dayCompletions[date].orEmpty().isNotEmpty()
        val isCurrentMonth = date.month == currentState.monthStart.month &&
            date.year == currentState.monthStart.year
        if (hasWorkouts && isCurrentMonth) {
            selectedDay.value = date
        }
    }

    fun onDismissDayDetail() {
        selectedDay.value = null
    }

    private fun progressCalculator(
        locale: Locale = Locale.getDefault(),
    ): ProgressCalculator = ProgressCalculator(
        clock = clock,
        locale = locale,
        zoneId = clock.zone,
        quickWorkoutLabel = quickWorkoutLabel,
    )

    companion object {
        fun factory(
            workoutCompletionRepository: WorkoutCompletionRepository,
            quickWorkoutLabel: String,
            clock: Clock = Clock.systemDefaultZone(),
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProgressViewModel(
                    workoutCompletionRepository = workoutCompletionRepository,
                    quickWorkoutLabel = quickWorkoutLabel,
                    clock = clock,
                ) as T
            }
        }
    }
}

private data class ProgressSignature(
    val totalCount: Int,
    val firstId: String?,
    val firstTimestamp: Long?,
    val lastId: String?,
    val lastTimestamp: Long?,
    val period: ProgressPeriod,
    val localeTag: String,
    val today: LocalDate,
)
