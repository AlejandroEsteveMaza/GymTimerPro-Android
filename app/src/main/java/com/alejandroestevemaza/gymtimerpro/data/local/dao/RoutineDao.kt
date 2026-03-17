package com.alejandroestevemaza.gymtimerpro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.alejandroestevemaza.gymtimerpro.data.local.entity.RoutineClassificationCrossRefEntity
import com.alejandroestevemaza.gymtimerpro.data.local.entity.RoutineClassificationEntity
import com.alejandroestevemaza.gymtimerpro.data.local.entity.RoutineEntity

import com.alejandroestevemaza.gymtimerpro.data.local.model.RoutineWithClassifications
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Transaction
    @Query("SELECT * FROM routines ORDER BY name COLLATE NOCASE ASC")
    fun observeRoutines(): Flow<List<RoutineWithClassifications>>

    @Query("SELECT * FROM routine_classifications ORDER BY name COLLATE NOCASE ASC")
    fun observeClassifications(): Flow<List<RoutineClassificationEntity>>

    @Transaction
    @Query("SELECT * FROM routines WHERE id = :routineId")
    suspend fun getRoutineWithClassifications(routineId: String): RoutineWithClassifications?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRoutine(entity: RoutineEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertClassifications(entities: List<RoutineClassificationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRoutineClassificationCrossRefs(
        entities: List<RoutineClassificationCrossRefEntity>,
    )

    @Query("DELETE FROM routine_classification_cross_refs WHERE routine_id = :routineId")
    suspend fun deleteCrossRefsForRoutine(routineId: String)

    @Query("DELETE FROM routine_classification_cross_refs WHERE classification_id = :classificationId")
    suspend fun deleteCrossRefsForClassification(classificationId: String)

    @Query("DELETE FROM routines WHERE id = :routineId")
    suspend fun deleteRoutineById(routineId: String)

    @Query("DELETE FROM routine_classifications WHERE id = :classificationId")
    suspend fun deleteClassificationById(classificationId: String)

    @Transaction
    suspend fun upsertRoutineWithClassifications(
        routine: RoutineEntity,
        classificationIds: List<String>,
    ) {
        upsertRoutine(routine)
        deleteCrossRefsForRoutine(routine.id)
        if (classificationIds.isNotEmpty()) {
            upsertRoutineClassificationCrossRefs(
                classificationIds.map { classificationId ->
                    RoutineClassificationCrossRefEntity(
                        routineId = routine.id,
                        classificationId = classificationId,
                    )
                }
            )
        }
    }

    @Transaction
    suspend fun deleteClassificationAndAssociations(classificationId: String) {
        deleteCrossRefsForClassification(classificationId)
        deleteClassificationById(classificationId)
    }
}
