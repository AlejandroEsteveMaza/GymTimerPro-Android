package com.alejandroestevemaza.gymtimerpro.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "routine_classification_cross_refs",
    primaryKeys = ["routine_id", "classification_id"],
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["id"],
            childColumns = ["routine_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = RoutineClassificationEntity::class,
            parentColumns = ["id"],
            childColumns = ["classification_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["routine_id"]),
        Index(value = ["classification_id"]),
    ],
)
data class RoutineClassificationCrossRefEntity(
    @ColumnInfo(name = "routine_id")
    val routineId: String,
    @ColumnInfo(name = "classification_id")
    val classificationId: String,
)
