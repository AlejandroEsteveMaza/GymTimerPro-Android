package com.alejandroestevemaza.gymtimerpro.data.local.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.alejandroestevemaza.gymtimerpro.data.local.entity.RoutineClassificationCrossRefEntity
import com.alejandroestevemaza.gymtimerpro.data.local.entity.RoutineClassificationEntity
import com.alejandroestevemaza.gymtimerpro.data.local.entity.RoutineEntity

data class RoutineWithClassifications(
    @Embedded
    val routine: RoutineEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = RoutineClassificationCrossRefEntity::class,
            parentColumn = "routine_id",
            entityColumn = "classification_id",
        ),
    )
    val classifications: List<RoutineClassificationEntity>,
)
