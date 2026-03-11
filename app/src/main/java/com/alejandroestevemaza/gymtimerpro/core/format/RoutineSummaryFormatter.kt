package com.alejandroestevemaza.gymtimerpro.core.format

import com.alejandroestevemaza.gymtimerpro.core.model.TimerDisplayFormat
import com.alejandroestevemaza.gymtimerpro.core.model.WeightUnitPreference

fun formatRoutineSummary(
    totalSets: Int,
    reps: Int,
    restSeconds: Int,
    weightKg: Double?,
    timerDisplayFormat: TimerDisplayFormat,
    weightUnitPreference: WeightUnitPreference,
): String {
    val parts = mutableListOf(
        "$totalSets x $reps",
        formatDuration(restSeconds, timerDisplayFormat),
    )
    formatWeight(weightKg, weightUnitPreference)?.let(parts::add)
    return parts.joinToString(separator = " • ")
}
