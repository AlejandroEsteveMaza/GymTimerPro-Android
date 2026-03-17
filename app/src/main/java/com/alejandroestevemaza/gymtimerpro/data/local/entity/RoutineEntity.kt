package com.alejandroestevemaza.gymtimerpro.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "routines",
    indices = [
        Index(value = ["name"]),
    ],
)
data class RoutineEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo(name = "total_sets")
    val totalSets: Int,
    val reps: Int,
    @ColumnInfo(name = "rest_seconds")
    val restSeconds: Int,
    @ColumnInfo(name = "weight_kg")
    val weightKg: Double?,
    @ColumnInfo(name = "created_at_epoch_millis")
    val createdAtEpochMillis: Long,
    @ColumnInfo(name = "updated_at_epoch_millis")
    val updatedAtEpochMillis: Long,
)
