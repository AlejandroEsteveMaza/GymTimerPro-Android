package com.alejandroestevemaza.gymtimerpro.feature.routines.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alejandroestevemaza.gymtimerpro.core.model.RoutineClassification
import com.alejandroestevemaza.gymtimerpro.data.repository.RoutinesRepository
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClassificationsUiState(
    val isOpen: Boolean = false,
    val classifications: List<RoutineClassification> = emptyList(),
    val searchQuery: String = "",
    val draft: ClassificationDraft? = null,
)

class ClassificationsViewModel(
    private val routinesRepository: RoutinesRepository,
) : ViewModel() {

    private val isOpen = MutableStateFlow(false)
    private val searchQuery = MutableStateFlow("")
    private val draft = MutableStateFlow<ClassificationDraft?>(null)

    val uiState: StateFlow<ClassificationsUiState> = combine(
        isOpen,
        routinesRepository.classifications,
        searchQuery,
        draft,
    ) { open, classifications, query, currentDraft ->
        ClassificationsUiState(
            isOpen = open,
            classifications = classifications,
            searchQuery = query,
            draft = currentDraft,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ClassificationsUiState(),
    )

    fun openManager() {
        isOpen.value = true
        draft.value = null
        searchQuery.value = ""
    }

    fun closeManager() {
        isOpen.value = false
        draft.value = null
        searchQuery.value = ""
    }

    fun onSearchQueryChanged(value: String) {
        searchQuery.value = value
    }

    fun onStartCreate() {
        val prefill = searchQuery.value.trim()
        draft.value = ClassificationDraft(value = prefill)
    }

    fun onStartRename(classificationId: String) {
        val classification = uiState.value.classifications.firstOrNull { it.id == classificationId } ?: return
        draft.value = ClassificationDraft(id = classification.id, value = classification.name)
    }

    fun onDraftChanged(value: String) {
        draft.update { current -> current?.copy(value = value, duplicateError = false) }
    }

    fun onCancelDraft() {
        draft.value = null
    }

    fun onSaveDraft() {
        val current = draft.value ?: return
        val trimmed = current.value.trim()
        if (trimmed.isEmpty()) return
        val normalized = trimmed.lowercase(Locale.getDefault())
        val duplicate = uiState.value.classifications.any { classification ->
            classification.normalizedName == normalized && classification.id != current.id
        }
        if (duplicate) {
            draft.value = current.copy(duplicateError = true)
            return
        }
        viewModelScope.launch {
            routinesRepository.upsertClassification(
                RoutineClassification(
                    id = current.id ?: UUID.randomUUID().toString(),
                    name = trimmed,
                    normalizedName = normalized,
                )
            )
            draft.value = null
        }
    }

    fun onDelete(classificationId: String) {
        viewModelScope.launch {
            routinesRepository.deleteClassification(classificationId)
        }
    }

    companion object {
        fun factory(routinesRepository: RoutinesRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ClassificationsViewModel(routinesRepository = routinesRepository) as T
            }
    }
}
