package com.alejandroestevemaza.gymtimerpro.feature.progress.ui

import com.alejandroestevemaza.gymtimerpro.core.model.DayCompletionState
import com.alejandroestevemaza.gymtimerpro.core.model.ProgressBadgeState
import com.alejandroestevemaza.gymtimerpro.core.model.ProgressDerivedState
import com.alejandroestevemaza.gymtimerpro.core.model.ProgressPeriod
import java.time.LocalDate

data class ProgressUiState(
    val selectedPeriod: ProgressPeriod = ProgressPeriod.Week,
    val derivedState: ProgressDerivedState? = null,
    val selectedDay: LocalDate? = null,
    val selectedDayCompletions: List<DayCompletionState> = emptyList(),
) {
    val badges: List<ProgressBadgeState>
        get() = derivedState?.badges.orEmpty()
}
