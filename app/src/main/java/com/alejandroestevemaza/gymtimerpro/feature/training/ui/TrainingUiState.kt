package com.alejandroestevemaza.gymtimerpro.feature.training.ui

import com.alejandroestevemaza.gymtimerpro.core.model.AppSettings
import com.alejandroestevemaza.gymtimerpro.core.model.DailyUsageState
import com.alejandroestevemaza.gymtimerpro.core.model.TrainingDefaults
import com.alejandroestevemaza.gymtimerpro.core.model.TrainingSessionState

data class TrainingUiState(
    val settings: AppSettings = AppSettings(),
    val isPro: Boolean = false,
    val session: TrainingSessionState = TrainingSessionState(),
    val dailyUsage: DailyUsageState = DailyUsageState(),
    val showDailyLimitDialog: Boolean = false,
) {
    val canEditConfiguration: Boolean
        get() = !session.timerIsRunning && !session.completed

    val canReset: Boolean
        get() = session.completed || session.timerIsRunning || session.currentSet > TrainingDefaults.currentSet

    val startRestEnabled: Boolean
        get() = !session.timerIsRunning && !session.completed

    val remainingFreeUsage: Int
        get() = (TrainingDefaults.dailyFreeUsageLimit - dailyUsage.consumedCount).coerceAtLeast(0)
}
