package com.alejandroestevemaza.gymtimerpro.data.repository

import com.alejandroestevemaza.gymtimerpro.core.model.RoutineSelectionSnapshot
import com.alejandroestevemaza.gymtimerpro.core.model.TrainingDefaults
import com.alejandroestevemaza.gymtimerpro.data.preferences.TrainingSessionRepository

interface TrainingSessionCoordinator {
    suspend fun applyRoutine(
        snapshot: RoutineSelectionSnapshot,
        maxSets: Int,
    )

    suspend fun clearAppliedRoutine()
}

class DefaultTrainingSessionCoordinator(
    private val trainingSessionRepository: TrainingSessionRepository,
) : TrainingSessionCoordinator {
    override suspend fun applyRoutine(
        snapshot: RoutineSelectionSnapshot,
        maxSets: Int,
    ) {
        val effectiveTotalSets = snapshot.totalSets.coerceIn(TrainingDefaults.minSets, maxSets)
        trainingSessionRepository.updateSession { session ->
            val alreadyApplied = session.appliedRoutineId == snapshot.routineId &&
                session.totalSets == effectiveTotalSets &&
                session.restSeconds == snapshot.restSeconds &&
                session.appliedRoutineName == snapshot.name &&
                session.appliedRoutineReps == snapshot.reps &&
                session.appliedClassificationId == snapshot.primaryClassificationId &&
                session.appliedClassificationName == snapshot.primaryClassificationName

            if (alreadyApplied) {
                session
            } else {
                session.copy(
                    totalSets = effectiveTotalSets,
                    restSeconds = snapshot.restSeconds.coerceIn(
                        TrainingDefaults.minRestSeconds,
                        TrainingDefaults.maxRestSeconds,
                    ),
                    currentSet = TrainingDefaults.currentSet,
                    completed = false,
                    appliedRoutineId = snapshot.routineId,
                    appliedRoutineName = snapshot.name,
                    appliedRoutineReps = snapshot.reps,
                    appliedClassificationId = snapshot.primaryClassificationId,
                    appliedClassificationName = snapshot.primaryClassificationName,
                    timerIsRunning = false,
                    timerEndEpochMillis = null,
                    timerRemainingSeconds = 0,
                    timerDidFinish = false,
                    completedAtEpochMillis = null,
                )
            }
        }
    }

    override suspend fun clearAppliedRoutine() {
        trainingSessionRepository.updateSession { session ->
            session.copy(
                appliedRoutineId = null,
                appliedRoutineName = null,
                appliedRoutineReps = null,
                appliedClassificationId = null,
                appliedClassificationName = null,
            )
        }
    }
}
