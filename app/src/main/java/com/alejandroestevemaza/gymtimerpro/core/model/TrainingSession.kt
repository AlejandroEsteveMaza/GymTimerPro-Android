package com.alejandroestevemaza.gymtimerpro.core.model

object TrainingDefaults {
    // The specs do not state explicit Training defaults; these align with the iOS routine draft defaults
    // until the referenced iOS source files are added to the workspace for direct verification.
    const val totalSets = 4
    const val restSeconds = 90
    const val currentSet = 1
    const val minSets = 1
    const val minRestSeconds = 15
    const val maxRestSeconds = 300
    const val dailyFreeUsageLimit = 16
    const val completionResetDelayMillis = 2_000L
}

data class TrainingSessionState(
    val totalSets: Int = TrainingDefaults.totalSets,
    val restSeconds: Int = TrainingDefaults.restSeconds,
    val currentSet: Int = TrainingDefaults.currentSet,
    val completed: Boolean = false,
    val appliedRoutineId: String? = null,
    val appliedRoutineName: String? = null,
    val appliedRoutineReps: Int? = null,
    val appliedClassificationId: String? = null,
    val appliedClassificationName: String? = null,
    val timerIsRunning: Boolean = false,
    val timerEndEpochMillis: Long? = null,
    val timerRemainingSeconds: Int = 0,
    val timerDidFinish: Boolean = false,
    val completedAtEpochMillis: Long? = null,
)

data class DailyUsageState(
    val dayStartEpochMillis: Long? = null,
    val consumedCount: Int = 0,
)
