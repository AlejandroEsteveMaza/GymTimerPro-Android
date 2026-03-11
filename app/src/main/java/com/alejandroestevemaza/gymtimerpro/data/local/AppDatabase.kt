package com.alejandroestevemaza.gymtimerpro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alejandroestevemaza.gymtimerpro.data.local.dao.RoutineDao
import com.alejandroestevemaza.gymtimerpro.data.local.dao.WorkoutCompletionDao
import com.alejandroestevemaza.gymtimerpro.data.local.entity.RoutineClassificationCrossRefEntity
import com.alejandroestevemaza.gymtimerpro.data.local.entity.RoutineClassificationEntity
import com.alejandroestevemaza.gymtimerpro.data.local.entity.RoutineEntity
import com.alejandroestevemaza.gymtimerpro.data.local.entity.WorkoutCompletionEntity

@Database(
    entities = [
        RoutineEntity::class,
        RoutineClassificationEntity::class,
        RoutineClassificationCrossRefEntity::class,
        WorkoutCompletionEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
    abstract fun workoutCompletionDao(): WorkoutCompletionDao
}
