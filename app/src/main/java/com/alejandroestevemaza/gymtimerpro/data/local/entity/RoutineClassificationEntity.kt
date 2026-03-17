package com.alejandroestevemaza.gymtimerpro.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "routine_classifications",
    indices = [
        Index(value = ["normalized_name"], unique = true),
        Index(value = ["name"]),
    ],
)
data class RoutineClassificationEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    @ColumnInfo(name = "normalized_name")
    val normalizedName: String,
)
