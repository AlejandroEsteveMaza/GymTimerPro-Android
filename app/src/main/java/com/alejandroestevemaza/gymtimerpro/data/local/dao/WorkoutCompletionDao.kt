package com.alejandroestevemaza.gymtimerpro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alejandroestevemaza.gymtimerpro.data.local.entity.WorkoutCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutCompletionDao {
    @Query("SELECT * FROM workout_completions ORDER BY completed_at_epoch_millis DESC")
    fun observeCompletions(): Flow<List<WorkoutCompletionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(entity: WorkoutCompletionEntity)
}
