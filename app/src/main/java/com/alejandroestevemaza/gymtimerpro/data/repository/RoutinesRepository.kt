package com.alejandroestevemaza.gymtimerpro.data.repository

import com.alejandroestevemaza.gymtimerpro.core.model.Routine
import com.alejandroestevemaza.gymtimerpro.core.model.RoutineClassification
import com.alejandroestevemaza.gymtimerpro.core.model.RoutineSelectionSnapshot
import com.alejandroestevemaza.gymtimerpro.data.local.dao.RoutineDao
import com.alejandroestevemaza.gymtimerpro.data.local.entity.RoutineClassificationEntity
import com.alejandroestevemaza.gymtimerpro.data.local.entity.RoutineEntity
import com.alejandroestevemaza.gymtimerpro.data.local.model.RoutineWithClassifications
import java.text.Collator
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface RoutinesRepository {
    val routines: Flow<List<Routine>>
    val classifications: Flow<List<RoutineClassification>>

    suspend fun upsertRoutine(routine: Routine)
    suspend fun deleteRoutine(routineId: String)
    suspend fun upsertClassification(classification: RoutineClassification)
    suspend fun deleteClassification(classificationId: String)
    suspend fun getRoutineSelectionSnapshot(routineId: String): RoutineSelectionSnapshot?
}

class DefaultRoutinesRepository(
    private val routineDao: RoutineDao,
) : RoutinesRepository {
    override val routines: Flow<List<Routine>> = routineDao.observeRoutines()
        .map { routines -> routines.map(RoutineWithClassifications::toDomain) }

    override val classifications: Flow<List<RoutineClassification>> = routineDao.observeClassifications()
        .map { classifications -> classifications.map(RoutineClassificationEntity::toDomain) }

    override suspend fun upsertRoutine(routine: Routine) {
        routineDao.upsertRoutineWithClassifications(
            routine = routine.toEntity(),
            classifications = routine.classifications.map { it.toEntity() },
        )
    }

    override suspend fun deleteRoutine(routineId: String) {
        routineDao.deleteRoutineById(routineId)
    }

    override suspend fun upsertClassification(classification: RoutineClassification) {
        routineDao.upsertClassifications(
            listOf(
                classification.toEntity()
            )
        )
    }

    override suspend fun deleteClassification(classificationId: String) {
        routineDao.deleteClassificationAndAssociations(classificationId)
    }

    override suspend fun getRoutineSelectionSnapshot(routineId: String): RoutineSelectionSnapshot? {
        val routine = routineDao.getRoutineWithClassifications(routineId) ?: return null
        val collator = Collator.getInstance(Locale.getDefault()).apply {
            strength = Collator.PRIMARY
        }
        val primaryClassification = routine.classifications
            .sortedWith { left, right -> collator.compare(left.name, right.name) }
            .firstOrNull()

        return RoutineSelectionSnapshot(
            routineId = routine.routine.id,
            name = routine.routine.name,
            totalSets = routine.routine.totalSets,
            reps = routine.routine.reps,
            restSeconds = routine.routine.restSeconds,
            primaryClassificationId = primaryClassification?.id,
            primaryClassificationName = primaryClassification?.name,
        )
    }
}

private fun RoutineWithClassifications.toDomain(): Routine = Routine(
    id = routine.id,
    name = routine.name,
    totalSets = routine.totalSets,
    reps = routine.reps,
    restSeconds = routine.restSeconds,
    weightKg = routine.weightKg,
    classifications = classifications
        .sortedBy { classification -> classification.name.lowercase(Locale.getDefault()) }
        .map(RoutineClassificationEntity::toDomain),
    createdAtEpochMillis = routine.createdAtEpochMillis,
    updatedAtEpochMillis = routine.updatedAtEpochMillis,
)

private fun Routine.toEntity(): RoutineEntity = RoutineEntity(
    id = id,
    name = name,
    totalSets = totalSets,
    reps = reps,
    restSeconds = restSeconds,
    weightKg = weightKg,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

private fun RoutineClassification.toEntity(): RoutineClassificationEntity = RoutineClassificationEntity(
    id = id,
    name = name.trim(),
    normalizedName = name.trim().lowercase(Locale.getDefault()),
)

private fun RoutineClassificationEntity.toDomain(): RoutineClassification = RoutineClassification(
    id = id,
    name = name,
    normalizedName = normalizedName,
)
