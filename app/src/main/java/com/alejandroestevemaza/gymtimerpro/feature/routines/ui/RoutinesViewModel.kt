package com.alejandroestevemaza.gymtimerpro.feature.routines.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alejandroestevemaza.gymtimerpro.core.format.parseWeightInputToKilograms
import com.alejandroestevemaza.gymtimerpro.core.format.sanitizeWeightInput
import com.alejandroestevemaza.gymtimerpro.core.model.AppSettings
import com.alejandroestevemaza.gymtimerpro.core.model.Routine
import com.alejandroestevemaza.gymtimerpro.core.model.RoutineClassification
import com.alejandroestevemaza.gymtimerpro.data.preferences.AppSettingsRepository
import com.alejandroestevemaza.gymtimerpro.data.preferences.TrainingSessionRepository
import com.alejandroestevemaza.gymtimerpro.data.repository.RoutinesRepository
import com.alejandroestevemaza.gymtimerpro.data.repository.TrainingSessionCoordinator
import java.time.Clock
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RoutinesViewModel(
    private val appSettingsRepository: AppSettingsRepository,
    private val routinesRepository: RoutinesRepository,
    private val trainingSessionRepository: TrainingSessionRepository,
    private val trainingSessionCoordinator: TrainingSessionCoordinator,
    private val clock: Clock,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val expandedSectionId = MutableStateFlow<String?>(null)
    private val editorState = MutableStateFlow<RoutineEditorState?>(null)
    private val classificationManagerOpen = MutableStateFlow(false)
    private val classificationSearchQuery = MutableStateFlow("")
    private val classificationDraft = MutableStateFlow<ClassificationDraft?>(null)

    private var editorBaseline: RoutineEditorState? = null

    private val baseState = combine(
        appSettingsRepository.settings,
        routinesRepository.routines,
        routinesRepository.classifications,
        trainingSessionRepository.sessionState,
    ) { settings, routines, classifications, session ->
        BaseRoutinesState(
            settings = settings,
            routines = routines,
            classifications = classifications,
            appliedRoutineId = session.appliedRoutineId,
        )
    }

    private val interactiveState = combine(
        searchQuery,
        expandedSectionId,
        editorState,
    ) { searchQuery, expandedSectionId, editorState ->
        EditorInteractionState(
            searchQuery = searchQuery,
            expandedSectionId = expandedSectionId,
            editorState = editorState,
        )
    }

    private val classificationManagerState = combine(
        classificationManagerOpen,
        classificationSearchQuery,
        classificationDraft,
    ) { classificationManagerOpen, classificationSearchQuery, classificationDraft ->
        ClassificationManagerState(
            classificationManagerOpen = classificationManagerOpen,
            classificationSearchQuery = classificationSearchQuery,
            classificationDraft = classificationDraft,
        )
    }

    val uiState: StateFlow<RoutinesUiState> = combine(
        baseState,
        interactiveState,
        classificationManagerState,
    ) { baseState, interactiveState, classificationManagerState ->
        RoutinesUiState(
            settings = baseState.settings,
            routines = baseState.routines,
            classifications = baseState.classifications,
            appliedRoutineId = baseState.appliedRoutineId,
            searchQuery = interactiveState.searchQuery,
            expandedSectionId = interactiveState.expandedSectionId,
            editorState = interactiveState.editorState,
            classificationDraft = classificationManagerState.classificationDraft,
            classificationManagerOpen = classificationManagerState.classificationManagerOpen,
            classificationSearchQuery = classificationManagerState.classificationSearchQuery,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RoutinesUiState(),
    )

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun onToggleSection(sectionId: String) {
        if (searchQuery.value.isNotBlank()) return
        expandedSectionId.update { current ->
            if (current == sectionId) null else sectionId
        }
    }

    fun onAddRoutine() {
        val draft = RoutineEditorState()
        editorBaseline = draft
        editorState.value = draft.copy(hasUnsavedChanges = false)
    }

    fun onEditRoutine(routineId: String) {
        val uiState = uiState.value
        val routine = uiState.routines.firstOrNull { it.id == routineId } ?: return
        val draft = routine.toEditorState(
            settings = uiState.settings,
            isAppliedToTraining = routine.id == uiState.appliedRoutineId,
        )
        editorBaseline = draft
        editorState.value = draft.copy(hasUnsavedChanges = false)
    }

    fun onDismissEditor() {
        editorBaseline = null
        editorState.value = null
    }

    fun onEditorNameChanged(value: String) {
        updateEditor { current ->
            current.copy(
                name = value.take(50),
                nameCount = value.take(50).length,
            )
        }
    }

    fun onEditorIncreaseSets() {
        val maxSets = uiState.value.settings.maxSetsPreference.maxSets
        updateEditor { current ->
            current.copy(totalSets = (current.totalSets + 1).coerceAtMost(maxSets))
        }
    }

    fun onEditorDecreaseSets() {
        updateEditor { current ->
            current.copy(totalSets = (current.totalSets - 1).coerceAtLeast(1))
        }
    }

    fun onEditorIncreaseReps() {
        updateEditor { current ->
            current.copy(reps = (current.reps + 1).coerceAtMost(30))
        }
    }

    fun onEditorDecreaseReps() {
        updateEditor { current ->
            current.copy(reps = (current.reps - 1).coerceAtLeast(1))
        }
    }

    fun onEditorIncreaseRest() {
        val step = uiState.value.settings.restIncrementPreference.seconds
        updateEditor { current ->
            current.copy(restSeconds = (current.restSeconds + step).coerceAtMost(300))
        }
    }

    fun onEditorDecreaseRest() {
        val step = uiState.value.settings.restIncrementPreference.seconds
        updateEditor { current ->
            current.copy(restSeconds = (current.restSeconds - step).coerceAtLeast(15))
        }
    }

    fun onEditorWeightChanged(value: String) {
        val sanitized = sanitizeWeightInput(value)
        updateEditor { current ->
            current.copy(
                weightInput = sanitized,
                isWeightValid = isWeightInputValid(sanitized, uiState.value.settings),
            )
        }
    }

    fun onToggleClassification(classificationId: String) {
        updateEditor { current ->
            val updatedSelection = current.selectedClassificationIds.toMutableSet().apply {
                if (!add(classificationId)) {
                    remove(classificationId)
                }
            }
            current.copy(selectedClassificationIds = updatedSelection)
        }
    }

    fun onOpenClassificationManager() {
        classificationManagerOpen.value = true
        classificationDraft.value = null
        classificationSearchQuery.value = ""
    }

    fun onCloseClassificationManager() {
        classificationManagerOpen.value = false
        classificationDraft.value = null
        classificationSearchQuery.value = ""
    }

    fun onClassificationSearchQueryChanged(value: String) {
        classificationSearchQuery.value = value
    }

    fun onStartCreateClassification() {
        val prefill = classificationSearchQuery.value.trim()
        classificationDraft.value = ClassificationDraft(value = prefill)
    }

    fun onStartRenameClassification(classificationId: String) {
        val classification = uiState.value.classifications.firstOrNull { it.id == classificationId } ?: return
        classificationDraft.value = ClassificationDraft(
            id = classification.id,
            value = classification.name,
        )
    }

    fun onClassificationDraftChanged(value: String) {
        classificationDraft.update { current ->
            current?.copy(value = value, duplicateError = false)
        }
    }

    fun onCancelClassificationDraft() {
        classificationDraft.value = null
    }

    fun onSaveClassificationDraft() {
        val draft = classificationDraft.value ?: return
        val trimmed = draft.value.trim()
        if (trimmed.isEmpty()) return
        val normalized = trimmed.lowercase(Locale.getDefault())
        val duplicate = uiState.value.classifications.any { classification ->
            classification.normalizedName == normalized && classification.id != draft.id
        }
        if (duplicate) {
            classificationDraft.value = draft.copy(duplicateError = true)
            return
        }
        viewModelScope.launch {
            routinesRepository.upsertClassification(
                RoutineClassification(
                    id = draft.id ?: UUID.randomUUID().toString(),
                    name = trimmed,
                    normalizedName = normalized,
                )
            )
            classificationDraft.value = null
        }
    }

    fun onDeleteClassification(classificationId: String) {
        viewModelScope.launch {
            routinesRepository.deleteClassification(classificationId)
        }
    }

    fun onSaveEditor() {
        val draft = editorState.value ?: return
        if (!canSaveDraft(draft, uiState.value.settings)) return

        viewModelScope.launch {
            persistEditorDraft(draft)
            onDismissEditor()
        }
    }

    fun onDeleteRoutine() {
        val draft = editorState.value ?: return
        val routineId = draft.routineId ?: return
        viewModelScope.launch {
            if (uiState.value.appliedRoutineId == routineId) {
                trainingSessionCoordinator.clearAppliedRoutine()
            }
            routinesRepository.deleteRoutine(routineId)
            onDismissEditor()
        }
    }

    fun onApplyOrRemoveFromTraining() {
        val draft = editorState.value ?: return
        val routineId = draft.routineId ?: return
        viewModelScope.launch {
            if (draft.isAppliedToTraining) {
                trainingSessionCoordinator.clearAppliedRoutine()
                updateEditorImmediately { current ->
                    current.copy(isAppliedToTraining = false)
                }
                return@launch
            }

            if (!canSaveDraft(draft, uiState.value.settings)) return@launch
            val routineIdToApply = if (draft.hasUnsavedChanges) {
                persistEditorDraft(draft)
            } else {
                routineId
            }
            val snapshot = routinesRepository.getRoutineSelectionSnapshot(routineIdToApply) ?: return@launch
            trainingSessionCoordinator.applyRoutine(
                snapshot = snapshot,
                maxSets = uiState.value.settings.maxSetsPreference.maxSets,
            )
            onDismissEditor()
        }
    }

    fun onApplyRoutineFromList(routineId: String) {
        viewModelScope.launch {
            val snapshot = routinesRepository.getRoutineSelectionSnapshot(routineId) ?: return@launch
            trainingSessionCoordinator.applyRoutine(
                snapshot = snapshot,
                maxSets = uiState.value.settings.maxSetsPreference.maxSets,
            )
        }
    }

    private fun updateEditor(transform: (RoutineEditorState) -> RoutineEditorState) {
        updateEditorImmediately { current ->
            val updated = transform(current)
            val baseline = editorBaseline
            updated.copy(
                nameCount = updated.name.length,
                hasUnsavedChanges = baseline == null || updated.asComparable() != baseline.asComparable(),
                isWeightValid = isWeightInputValid(updated.weightInput, uiState.value.settings),
            )
        }
    }

    private fun updateEditorImmediately(transform: (RoutineEditorState) -> RoutineEditorState) {
        editorState.update { current -> current?.let(transform) }
    }

    private fun canSaveDraft(
        draft: RoutineEditorState,
        settings: AppSettings,
    ): Boolean {
        val trimmedName = draft.name.trim()
        if (trimmedName.isEmpty()) return false
        if (!draft.isWeightValid) return false
        if (draft.totalSets !in 1..settings.maxSetsPreference.maxSets) return false
        if (draft.reps !in 1..30) return false
        if (draft.restSeconds !in 15..300) return false
        return true
    }

    private fun isWeightInputValid(
        weightInput: String,
        settings: AppSettings,
    ): Boolean = parseWeightInputToKilograms(
        input = weightInput,
        preference = settings.weightUnitPreference,
    )?.isNaN() != true

    private suspend fun persistEditorDraft(draft: RoutineEditorState): String {
        val now = clock.millis()
        val existingRoutine = draft.routineId?.let { routineId ->
            uiState.value.routines.firstOrNull { it.id == routineId }
        }
        val savedRoutine = Routine(
            id = existingRoutine?.id ?: UUID.randomUUID().toString(),
            name = draft.name.trim(),
            totalSets = draft.totalSets.coerceAtMost(uiState.value.settings.maxSetsPreference.maxSets),
            reps = draft.reps,
            restSeconds = draft.restSeconds,
            weightKg = parseWeightInputToKilograms(
                input = draft.weightInput,
                preference = uiState.value.settings.weightUnitPreference,
            ),
            classifications = uiState.value.classifications.filter { classification ->
                classification.id in draft.selectedClassificationIds
            },
            createdAtEpochMillis = existingRoutine?.createdAtEpochMillis ?: now,
            updatedAtEpochMillis = now,
        )
        routinesRepository.upsertRoutine(savedRoutine)
        editorBaseline = savedRoutine.toEditorState(
            settings = uiState.value.settings,
            isAppliedToTraining = savedRoutine.id == uiState.value.appliedRoutineId,
        )
        editorState.value = editorBaseline?.copy(hasUnsavedChanges = false)
        return savedRoutine.id
    }

    companion object {
        fun factory(
            appSettingsRepository: AppSettingsRepository,
            routinesRepository: RoutinesRepository,
            trainingSessionRepository: TrainingSessionRepository,
            trainingSessionCoordinator: TrainingSessionCoordinator,
            clock: Clock = Clock.systemDefaultZone(),
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RoutinesViewModel(
                    appSettingsRepository = appSettingsRepository,
                    routinesRepository = routinesRepository,
                    trainingSessionRepository = trainingSessionRepository,
                    trainingSessionCoordinator = trainingSessionCoordinator,
                    clock = clock,
                ) as T
            }
        }

    }
}

private data class BaseRoutinesState(
    val settings: AppSettings,
    val routines: List<Routine>,
    val classifications: List<RoutineClassification>,
    val appliedRoutineId: String?,
)

private data class EditorInteractionState(
    val searchQuery: String,
    val expandedSectionId: String?,
    val editorState: RoutineEditorState?,
)

private data class ClassificationManagerState(
    val classificationManagerOpen: Boolean,
    val classificationSearchQuery: String,
    val classificationDraft: ClassificationDraft?,
)

private fun Routine.toEditorState(
    settings: AppSettings,
    isAppliedToTraining: Boolean,
): RoutineEditorState = RoutineEditorState(
    routineId = id,
    name = name,
    nameCount = name.length,
    totalSets = totalSets.coerceAtMost(settings.maxSetsPreference.maxSets),
    reps = reps,
    restSeconds = restSeconds,
    weightInput = com.alejandroestevemaza.gymtimerpro.core.format.formatWeight(
        weightKg = weightKg,
        preference = settings.weightUnitPreference,
    )?.split(" ")
        ?.firstOrNull()
        .orEmpty(),
    selectedClassificationIds = classifications.map { it.id }.toSet(),
    isAppliedToTraining = isAppliedToTraining,
    hasUnsavedChanges = false,
    isWeightValid = true,
)

private fun RoutineEditorState.asComparable(): List<Any?> = listOf(
    routineId,
    name.trim(),
    totalSets,
    reps,
    restSeconds,
    weightInput,
    selectedClassificationIds.sorted(),
    isAppliedToTraining,
)
