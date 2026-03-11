package com.alejandroestevemaza.gymtimerpro.data.repository

import com.alejandroestevemaza.gymtimerpro.core.model.WorkoutCompletion
import com.alejandroestevemaza.gymtimerpro.data.local.dao.WorkoutCompletionDao
import com.alejandroestevemaza.gymtimerpro.data.local.entity.WorkoutCompletionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface WorkoutCompletionRepository {
    val completions: Flow<List<WorkoutCompletion>>

    suspend fun insertCompletion(completion: WorkoutCompletion)
}

class DefaultWorkoutCompletionRepository(
    private val workoutCompletionDao: WorkoutCompletionDao,
) : WorkoutCompletionRepository {
    override val completions: Flow<List<WorkoutCompletion>> = workoutCompletionDao.observeCompletions()
        .map { completions -> completions.map(WorkoutCompletionEntity::toDomain) }

    override suspend fun insertCompletion(completion: WorkoutCompletion) {
        workoutCompletionDao.insertCompletion(completion.toEntity())
    }
}

private fun WorkoutCompletionEntity.toDomain(): WorkoutCompletion = WorkoutCompletion(
    id = id,
    completedAtEpochMillis = completedAtEpochMillis,
    routineId = routineId,
    routineNameSnapshot = routineNameSnapshot,
    classificationId = classificationId,
    classificationNameSnapshot = classificationNameSnapshot,
    durationSeconds = durationSeconds,
    notes = notes,
)

private fun WorkoutCompletion.toEntity(): WorkoutCompletionEntity = WorkoutCompletionEntity(
    id = id,
    completedAtEpochMillis = completedAtEpochMillis,
    routineId = routineId,
    routineNameSnapshot = routineNameSnapshot,
    classificationId = classificationId,
    classificationNameSnapshot = classificationNameSnapshot,
    durationSeconds = durationSeconds,
    notes = notes,
)
