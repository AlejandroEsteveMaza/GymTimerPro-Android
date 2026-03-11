package com.alejandroestevemaza.gymtimerpro.feature.routines.ui

import com.alejandroestevemaza.gymtimerpro.core.model.AppSettings
import com.alejandroestevemaza.gymtimerpro.core.model.Routine
import com.alejandroestevemaza.gymtimerpro.core.model.RoutineClassification
import java.util.Locale

data class RoutineCatalogSection(
    val id: String,
    val title: String,
    val routines: List<Routine>,
    val isExpanded: Boolean,
)

data class ClassificationDraft(
    val id: String? = null,
    val value: String = "",
    val duplicateError: Boolean = false,
) {
    val isCreating: Boolean
        get() = id == null
}

data class RoutineEditorState(
    val routineId: String? = null,
    val name: String = "",
    val nameCount: Int = 0,
    val totalSets: Int = 4,
    val reps: Int = 10,
    val restSeconds: Int = 90,
    val weightInput: String = "",
    val selectedClassificationIds: Set<String> = emptySet(),
    val isAppliedToTraining: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val isWeightValid: Boolean = true,
) {
    val isEditMode: Boolean
        get() = routineId != null
}

data class RoutinesUiState(
    val settings: AppSettings = AppSettings(),
    val routines: List<Routine> = emptyList(),
    val classifications: List<RoutineClassification> = emptyList(),
    val appliedRoutineId: String? = null,
    val searchQuery: String = "",
    val expandedSectionId: String? = null,
    val editorState: RoutineEditorState? = null,
    val classificationDraft: ClassificationDraft? = null,
    val classificationManagerOpen: Boolean = false,
    val classificationSearchQuery: String = "",
) {
    val isEmptyState: Boolean
        get() = routines.isEmpty() && classifications.isEmpty()

    val isSearchMode: Boolean
        get() = searchQuery.isNotBlank()

    val groupedSections: List<RoutineCatalogSection>
        get() {
            val sortedClassifications = classifications.sortedBy { it.name.lowercase(Locale.getDefault()) }
            val sortedRoutines = routines.sortedBy { it.name.lowercase(Locale.getDefault()) }

            if (!isSearchMode) {
                val classificationSections = sortedClassifications.map { classification ->
                    RoutineCatalogSection(
                        id = classification.id,
                        title = classification.name,
                        routines = sortedRoutines.filter { routine ->
                            routine.classifications.any { it.id == classification.id }
                        },
                        isExpanded = expandedSectionId == classification.id,
                    )
                }
                val unclassifiedRoutines = sortedRoutines.filter { it.classifications.isEmpty() }
                val sections = if (unclassifiedRoutines.isNotEmpty()) {
                    classificationSections + RoutineCatalogSection(
                        id = UNCLASSIFIED_SECTION_ID,
                        title = "Unclassified",
                        routines = unclassifiedRoutines,
                        isExpanded = expandedSectionId == UNCLASSIFIED_SECTION_ID,
                    )
                } else {
                    classificationSections
                }
                return sections
            }

            val query = searchQuery.trim()
            val matchingClassifications = sortedClassifications.filter { classification ->
                classification.name.contains(query, ignoreCase = true)
            }
            val matchingClassificationIds = matchingClassifications.map { it.id }.toSet()
            val classificationSections = matchingClassifications.map { classification ->
                RoutineCatalogSection(
                    id = classification.id,
                    title = classification.name,
                    routines = sortedRoutines.filter { routine ->
                        routine.classifications.any { it.id == classification.id }
                    },
                    isExpanded = true,
                )
            }
            val alreadyRenderedRoutineIds = classificationSections
                .flatMap { section -> section.routines }
                .map { routine -> routine.id }
                .toSet()
            val matchingRoutines = sortedRoutines.filter { routine ->
                routine.name.contains(query, ignoreCase = true) &&
                    routine.id !in alreadyRenderedRoutineIds &&
                    routine.classifications.none { it.id in matchingClassificationIds }
            }
            return if (matchingRoutines.isNotEmpty()) {
                classificationSections + RoutineCatalogSection(
                    id = MATCHING_ROUTINES_SECTION_ID,
                    title = "Matching routines",
                    routines = matchingRoutines,
                    isExpanded = true,
                )
            } else {
                classificationSections
            }
        }

    companion object {
        const val UNCLASSIFIED_SECTION_ID = "__unclassified__"
        const val MATCHING_ROUTINES_SECTION_ID = "__matching_routines__"
    }
}
