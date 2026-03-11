package com.alejandroestevemaza.gymtimerpro.feature.routines.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alejandroestevemaza.gymtimerpro.R
import com.alejandroestevemaza.gymtimerpro.core.format.formatRoutineSummary
import com.alejandroestevemaza.gymtimerpro.data.preferences.AppContainer

@Composable
fun RoutinesRoute(
    appContainer: AppContainer,
) {
    val routinesViewModel: RoutinesViewModel = viewModel(
        factory = RoutinesViewModel.factory(
            appSettingsRepository = appContainer.appSettingsRepository,
            routinesRepository = appContainer.routinesRepository,
            trainingSessionRepository = appContainer.trainingSessionRepository,
            trainingSessionCoordinator = appContainer.trainingSessionCoordinator,
        )
    )
    val uiState by routinesViewModel.uiState.collectAsStateWithLifecycle()

    RoutinesScreen(
        uiState = uiState,
        onSearchQueryChanged = routinesViewModel::onSearchQueryChanged,
        onToggleSection = routinesViewModel::onToggleSection,
        onAddRoutine = routinesViewModel::onAddRoutine,
        onEditRoutine = routinesViewModel::onEditRoutine,
        onApplyRoutine = routinesViewModel::onApplyRoutineFromList,
        onOpenClassificationManager = routinesViewModel::onOpenClassificationManager,
        onCloseClassificationManager = routinesViewModel::onCloseClassificationManager,
        onClassificationSearchQueryChanged = routinesViewModel::onClassificationSearchQueryChanged,
        onStartCreateClassification = routinesViewModel::onStartCreateClassification,
        onStartRenameClassification = routinesViewModel::onStartRenameClassification,
        onClassificationDraftChanged = routinesViewModel::onClassificationDraftChanged,
        onCancelClassificationDraft = routinesViewModel::onCancelClassificationDraft,
        onSaveClassificationDraft = routinesViewModel::onSaveClassificationDraft,
        onDeleteClassification = routinesViewModel::onDeleteClassification,
        onDismissEditor = routinesViewModel::onDismissEditor,
        onEditorNameChanged = routinesViewModel::onEditorNameChanged,
        onEditorIncreaseSets = routinesViewModel::onEditorIncreaseSets,
        onEditorDecreaseSets = routinesViewModel::onEditorDecreaseSets,
        onEditorIncreaseReps = routinesViewModel::onEditorIncreaseReps,
        onEditorDecreaseReps = routinesViewModel::onEditorDecreaseReps,
        onEditorIncreaseRest = routinesViewModel::onEditorIncreaseRest,
        onEditorDecreaseRest = routinesViewModel::onEditorDecreaseRest,
        onEditorWeightChanged = routinesViewModel::onEditorWeightChanged,
        onToggleClassification = routinesViewModel::onToggleClassification,
        onSaveEditor = routinesViewModel::onSaveEditor,
        onDeleteRoutine = routinesViewModel::onDeleteRoutine,
        onApplyOrRemoveFromTraining = routinesViewModel::onApplyOrRemoveFromTraining,
    )
}

@Composable
fun RoutinesScreen(
    uiState: RoutinesUiState,
    onSearchQueryChanged: (String) -> Unit,
    onToggleSection: (String) -> Unit,
    onAddRoutine: () -> Unit,
    onEditRoutine: (String) -> Unit,
    onApplyRoutine: (String) -> Unit,
    onOpenClassificationManager: () -> Unit,
    onCloseClassificationManager: () -> Unit,
    onClassificationSearchQueryChanged: (String) -> Unit,
    onStartCreateClassification: () -> Unit,
    onStartRenameClassification: (String) -> Unit,
    onClassificationDraftChanged: (String) -> Unit,
    onCancelClassificationDraft: () -> Unit,
    onSaveClassificationDraft: () -> Unit,
    onDeleteClassification: (String) -> Unit,
    onDismissEditor: () -> Unit,
    onEditorNameChanged: (String) -> Unit,
    onEditorIncreaseSets: () -> Unit,
    onEditorDecreaseSets: () -> Unit,
    onEditorIncreaseReps: () -> Unit,
    onEditorDecreaseReps: () -> Unit,
    onEditorIncreaseRest: () -> Unit,
    onEditorDecreaseRest: () -> Unit,
    onEditorWeightChanged: (String) -> Unit,
    onToggleClassification: (String) -> Unit,
    onSaveEditor: () -> Unit,
    onDeleteRoutine: () -> Unit,
    onApplyOrRemoveFromTraining: () -> Unit,
) {
    if (uiState.classificationManagerOpen) {
        ClassificationManagerDialog(
            uiState = uiState,
            onClose = onCloseClassificationManager,
            onSearchChanged = onClassificationSearchQueryChanged,
            onStartCreate = onStartCreateClassification,
            onStartRename = onStartRenameClassification,
            onDraftChanged = onClassificationDraftChanged,
            onCancelDraft = onCancelClassificationDraft,
            onSaveDraft = onSaveClassificationDraft,
            onDelete = onDeleteClassification,
        )
    }

    uiState.editorState?.let { editorState ->
        RoutineEditorDialog(
            editorState = editorState,
            uiState = uiState,
            onDismiss = onDismissEditor,
            onNameChanged = onEditorNameChanged,
            onIncreaseSets = onEditorIncreaseSets,
            onDecreaseSets = onEditorDecreaseSets,
            onIncreaseReps = onEditorIncreaseReps,
            onDecreaseReps = onEditorDecreaseReps,
            onIncreaseRest = onEditorIncreaseRest,
            onDecreaseRest = onEditorDecreaseRest,
            onWeightChanged = onEditorWeightChanged,
            onToggleClassification = onToggleClassification,
            onSave = onSaveEditor,
            onDeleteRoutine = onDeleteRoutine,
            onApplyOrRemoveFromTraining = onApplyOrRemoveFromTraining,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.routines_search_hint)) },
            singleLine = true,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FilledIconButton(onClick = onAddRoutine) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
            }
            OutlinedButton(onClick = onOpenClassificationManager) {
                Text(text = stringResource(R.string.routines_manage_classifications))
            }
        }

        if (uiState.isEmptyState) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.routines_empty_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(R.string.routines_empty_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.groupedSections, key = { section -> section.id }) { section ->
                    RoutineSectionCard(
                        section = section,
                        uiState = uiState,
                        onToggleSection = onToggleSection,
                        onEditRoutine = onEditRoutine,
                        onApplyRoutine = onApplyRoutine,
                    )
                }
            }
        }
    }
}

@Composable
private fun RoutineSectionCard(
    section: RoutineCatalogSection,
    uiState: RoutinesUiState,
    onToggleSection: (String) -> Unit,
    onEditRoutine: (String) -> Unit,
    onApplyRoutine: (String) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !uiState.isSearchMode) { onToggleSection(section.id) },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = when (section.id) {
                        RoutinesUiState.UNCLASSIFIED_SECTION_ID -> stringResource(R.string.routines_unclassified)
                        RoutinesUiState.MATCHING_ROUTINES_SECTION_ID -> stringResource(R.string.routines_matching_routines)
                        else -> section.title
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                if (!uiState.isSearchMode) {
                    Icon(
                        imageVector = if (section.isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null,
                    )
                }
            }

            if (uiState.isSearchMode || section.isExpanded) {
                if (section.routines.isEmpty()) {
                    Text(
                        text = stringResource(R.string.classifications_empty),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        section.routines.forEach { routine ->
                            RoutineRow(
                                routine = routine,
                                uiState = uiState,
                                onEditRoutine = onEditRoutine,
                                onApplyRoutine = onApplyRoutine,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineRow(
    routine: com.alejandroestevemaza.gymtimerpro.core.model.Routine,
    uiState: RoutinesUiState,
    onEditRoutine: (String) -> Unit,
    onApplyRoutine: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditRoutine(routine.id) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = routine.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = formatRoutineSummary(
                            totalSets = routine.totalSets,
                            reps = routine.reps,
                            restSeconds = routine.restSeconds,
                            weightKg = routine.weightKg,
                            timerDisplayFormat = uiState.settings.timerDisplayFormat,
                            weightUnitPreference = uiState.settings.weightUnitPreference,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (uiState.appliedRoutineId == routine.id) {
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        label = { Text(text = stringResource(R.string.routines_applied)) },
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { onEditRoutine(routine.id) }) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = stringResource(R.string.routines_edit))
                }
                FilledIconButton(onClick = { onApplyRoutine(routine.id) }) {
                    Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun ClassificationManagerDialog(
    uiState: RoutinesUiState,
    onClose: () -> Unit,
    onSearchChanged: (String) -> Unit,
    onStartCreate: () -> Unit,
    onStartRename: (String) -> Unit,
    onDraftChanged: (String) -> Unit,
    onCancelDraft: () -> Unit,
    onSaveDraft: () -> Unit,
    onDelete: (String) -> Unit,
) {
    Dialog(onDismissRequest = onClose) {
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.classifications_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                    }
                }

                OutlinedTextField(
                    value = uiState.classificationSearchQuery,
                    onValueChange = onSearchChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(R.string.classifications_search_hint)) },
                    singleLine = true,
                )

                OutlinedButton(onClick = onStartCreate) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = stringResource(R.string.classifications_create))
                }

                uiState.classificationDraft?.let { draft ->
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = draft.value,
                            onValueChange = onDraftChanged,
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text(
                                    text = if (draft.isCreating) {
                                        stringResource(R.string.classifications_new_name)
                                    } else {
                                        stringResource(R.string.classifications_rename)
                                    }
                                )
                            },
                            singleLine = true,
                        )
                        if (draft.duplicateError) {
                            Text(
                                text = stringResource(R.string.classifications_duplicate),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = onCancelDraft) {
                                Text(text = stringResource(R.string.routines_cancel))
                            }
                            OutlinedButton(onClick = onSaveDraft) {
                                Text(
                                    text = if (draft.isCreating) {
                                        stringResource(R.string.classifications_create)
                                    } else {
                                        stringResource(R.string.routines_save)
                                    }
                                )
                            }
                        }
                    }
                }

                val filteredClassifications = uiState.classifications.filter { classification ->
                    uiState.classificationSearchQuery.isBlank() || classification.name.contains(
                        uiState.classificationSearchQuery,
                        ignoreCase = true,
                    )
                }

                if (filteredClassifications.isEmpty()) {
                    Text(
                        text = stringResource(R.string.classifications_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(filteredClassifications, key = { classification -> classification.id }) { classification ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(text = classification.name)
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(onClick = { onStartRename(classification.id) }) {
                                            Icon(
                                                imageVector = Icons.Rounded.Edit,
                                                contentDescription = null,
                                            )
                                        }
                                        IconButton(onClick = { onDelete(classification.id) }) {
                                            Icon(
                                                imageVector = Icons.Rounded.Delete,
                                                contentDescription = null,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineEditorDialog(
    editorState: RoutineEditorState,
    uiState: RoutinesUiState,
    onDismiss: () -> Unit,
    onNameChanged: (String) -> Unit,
    onIncreaseSets: () -> Unit,
    onDecreaseSets: () -> Unit,
    onIncreaseReps: () -> Unit,
    onDecreaseReps: () -> Unit,
    onIncreaseRest: () -> Unit,
    onDecreaseRest: () -> Unit,
    onWeightChanged: (String) -> Unit,
    onToggleClassification: (String) -> Unit,
    onSave: () -> Unit,
    onDeleteRoutine: () -> Unit,
    onApplyOrRemoveFromTraining: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = stringResource(R.string.routines_delete_confirm_title)) },
            text = { Text(text = stringResource(R.string.routines_delete_confirm_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteRoutine()
                    }
                ) {
                    Text(text = stringResource(R.string.routines_delete_confirm_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(R.string.routines_cancel))
                }
            },
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (editorState.isEditMode) {
                            stringResource(R.string.routines_editor_edit_title)
                        } else {
                            stringResource(R.string.routines_editor_create_title)
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                    }
                }

                OutlinedTextField(
                    value = editorState.name,
                    onValueChange = onNameChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(R.string.routines_name_label)) },
                    supportingText = {
                        Text(text = stringResource(R.string.routines_name_counter, editorState.nameCount))
                    },
                    isError = editorState.name.trim().isEmpty(),
                    singleLine = true,
                )
                if (editorState.name.trim().isEmpty()) {
                    Text(
                        text = stringResource(R.string.routines_invalid_name),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                StepperEditorRow(
                    label = stringResource(R.string.routines_sets_label),
                    value = editorState.totalSets.toString(),
                    onDecrease = onDecreaseSets,
                    onIncrease = onIncreaseSets,
                )
                StepperEditorRow(
                    label = stringResource(R.string.routines_reps_label),
                    value = editorState.reps.toString(),
                    onDecrease = onDecreaseReps,
                    onIncrease = onIncreaseReps,
                )
                StepperEditorRow(
                    label = stringResource(R.string.routines_rest_label),
                    value = com.alejandroestevemaza.gymtimerpro.core.format.formatDuration(
                        editorState.restSeconds,
                        uiState.settings.timerDisplayFormat,
                    ),
                    onDecrease = onDecreaseRest,
                    onIncrease = onIncreaseRest,
                )

                OutlinedTextField(
                    value = editorState.weightInput,
                    onValueChange = onWeightChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(R.string.routines_weight_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = !editorState.isWeightValid,
                    singleLine = true,
                )
                if (!editorState.isWeightValid) {
                    Text(
                        text = stringResource(R.string.routines_invalid_weight),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.routines_classifications_label),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        uiState.classifications.forEach { classification ->
                            ElevatedFilterChip(
                                selected = classification.id in editorState.selectedClassificationIds,
                                onClick = { onToggleClassification(classification.id) },
                                label = { Text(text = classification.name) },
                                leadingIcon = {
                                    if (classification.id in editorState.selectedClassificationIds) {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = null,
                                        )
                                    }
                                },
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(R.string.routines_cancel))
                    }
                    OutlinedButton(onClick = onSave) {
                        Text(text = stringResource(R.string.routines_save))
                    }
                }

                if (editorState.isEditMode) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onApplyOrRemoveFromTraining) {
                            Icon(
                                imageVector = if (editorState.isAppliedToTraining) {
                                    Icons.Rounded.Close
                                } else {
                                    Icons.Rounded.PlayArrow
                                },
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = if (editorState.isAppliedToTraining) {
                                    stringResource(R.string.routines_remove_from_training)
                                } else {
                                    stringResource(R.string.routines_apply_to_training)
                                }
                            )
                        }
                        OutlinedButton(onClick = { showDeleteDialog = true }) {
                            Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(text = stringResource(R.string.routines_delete))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepperEditorRow(
    label: String,
    value: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onDecrease) {
                    Icon(imageVector = Icons.Rounded.Remove, contentDescription = null)
                }
                IconButton(onClick = onIncrease) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                }
            }
        }
    }
}
