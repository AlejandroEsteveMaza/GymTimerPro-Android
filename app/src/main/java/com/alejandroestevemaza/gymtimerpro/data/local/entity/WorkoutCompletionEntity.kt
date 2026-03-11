package com.alejandroestevemaza.gymtimerpro.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_completions",
    indices = [
        Index(value = ["completed_at_epoch_millis"]),
        Index(value = ["routine_id"]),
        Index(value = ["classification_id"]),
    ],
)
data class WorkoutCompletionEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "completed_at_epoch_millis")
    val completedAtEpochMillis: Long,
    @ColumnInfo(name = "routine_id")
    val routineId: String?,
    @ColumnInfo(name = "routine_name_snapshot")
    val routineNameSnapshot: String,
    @ColumnInfo(name = "classification_id")
    val classificationId: String?,
    @ColumnInfo(name = "classification_name_snapshot")
    val classificationNameSnapshot: String?,
    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Int?,
    val notes: String?,
)
