package com.alejandroestevemaza.gymtimerpro.core.model

data class RoutineClassification(
    val id: String,
    val name: String,
    val normalizedName: String,
)

data class Routine(
    val id: String,
    val name: String,
    val totalSets: Int,
    val reps: Int,
    val restSeconds: Int,
    val weightKg: Double?,
    val classifications: List<RoutineClassification>,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

data class RoutineSelectionSnapshot(
    val routineId: String,
    val name: String,
    val totalSets: Int,
    val reps: Int,
    val restSeconds: Int,
    val primaryClassificationId: String?,
    val primaryClassificationName: String?,
)
