package com.alejandroestevemaza.gymtimerpro.core.model

data class WorkoutCompletion(
    val id: String,
    val completedAtEpochMillis: Long,
    val routineId: String?,
    val routineNameSnapshot: String,
    val classificationId: String?,
    val classificationNameSnapshot: String?,
    val durationSeconds: Int?,
    val notes: String?,
)
