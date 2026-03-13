package com.alejandroestevemaza.gymtimerpro.feature.routines.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alejandroestevemaza.gymtimerpro.R
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.ClassificationInputBar
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.HorizontalWheelStepper
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.RoutineRowItem
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTheme
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
@Suppress("UNUSED_PARAMETER")
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
    previewShowEditorInline: Boolean = false,
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
        if (previewShowEditorInline) {
            RoutineEditorInlinePreview(editorState = editorState)
        } else {
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
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = GymTheme.spacing.s16, vertical = GymTheme.spacing.s12),
        verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s20),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.app_navigation_routines),
                style = GymTheme.type.title2Bold,
                color = GymTheme.colors.textPrimary,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8)) {
                RoutinesTopActionButton(
                    onClick = onOpenClassificationManager,
                    icon = Icons.Rounded.MoreHoriz,
                )
                RoutinesTopActionButton(
                    onClick = onAddRoutine,
                    icon = Icons.Rounded.Add,
                )
            }
        }

        if (uiState.isEmptyState) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = GymTheme.spacing.s32),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.FormatListBulleted,
                    contentDescription = null,
                    tint = GymTheme.colors.textSecondary,
                    modifier = Modifier.size(GymTheme.spacing.s32),
                )
                Column(
                    modifier = Modifier.padding(top = GymTheme.spacing.s12),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
                ) {
                    Text(
                        text = stringResource(R.string.routines_empty_title),
                        style = GymTheme.type.title2Bold,
                        color = GymTheme.colors.textPrimary,
                    )
                    Text(
                        text = stringResource(R.string.routines_empty_body),
                        style = GymTheme.type.subheadlineRegular,
                        color = GymTheme.colors.textSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s20),
            ) {
                val classificationSections = uiState.groupedSections.filter { section ->
                    section.id != RoutinesUiState.UNCLASSIFIED_SECTION_ID &&
                        section.id != RoutinesUiState.MATCHING_ROUTINES_SECTION_ID
                }
                val unclassifiedSection = uiState.groupedSections.firstOrNull { section ->
                    section.id == RoutinesUiState.UNCLASSIFIED_SECTION_ID
                }

                if (classificationSections.isNotEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.routines_classifications_label),
                            style = GymTheme.type.captionRegular,
                            color = GymTheme.colors.textSecondary,
                        )
                    }
                    item {
                        ClassificationSectionsCard(
                            sections = classificationSections,
                            onToggleSection = onToggleSection,
                        )
                    }
                }

                if (unclassifiedSection != null) {
                    item {
                        Text(
                            text = stringResource(R.string.routines_unclassified),
                            style = GymTheme.type.captionRegular,
                            color = GymTheme.colors.textSecondary,
                        )
                    }
                    item {
                        RoutineRowsCard(
                            routines = unclassifiedSection.routines,
                            uiState = uiState,
                            onEditRoutine = onEditRoutine,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClassificationSectionsCard(
    sections: List<RoutineCatalogSection>,
    onToggleSection: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.r20),
    ) {
        Column(
            modifier = Modifier.padding(GymTheme.spacing.s16),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s6),
        ) {
            sections.forEachIndexed { index, section ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = GymTheme.layout.minTapHeight)
                        .clickable { onToggleSection(section.id) },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = section.title,
                        style = GymTheme.type.headlineRegular,
                        color = GymTheme.colors.textPrimary,
                    )
                    Icon(
                        imageVector = if (section.isExpanded) {
                            Icons.Rounded.ExpandMore
                        } else {
                            Icons.Rounded.ChevronRight
                        },
                        contentDescription = null,
                        tint = GymTheme.colors.textSecondary,
                    )
                }
                if (index != sections.lastIndex) {
                    HorizontalDivider(color = GymTheme.colors.divider)
                }
            }
        }
    }
}

@Composable
private fun RoutinesTopActionButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Box(
        modifier = Modifier
            .size(GymTheme.layout.minTapHeight)
            .background(
                color = GymTheme.colors.secondaryButtonFill,
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GymTheme.colors.textPrimary,
        )
    }
}

@Composable
private fun RoutineRowsCard(
    routines: List<com.alejandroestevemaza.gymtimerpro.core.model.Routine>,
    uiState: RoutinesUiState,
    onEditRoutine: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.r20),
    ) {
        Column(
            modifier = Modifier.padding(GymTheme.spacing.s16),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
        ) {
            if (routines.isEmpty()) {
                Text(
                    text = stringResource(R.string.routines_empty_body),
                    style = GymTheme.type.subheadlineRegular,
                    color = GymTheme.colors.textSecondary,
                )
            } else {
                routines.forEachIndexed { index, routine ->
                    RoutineListRow(
                        routine = routine,
                        uiState = uiState,
                        onEditRoutine = onEditRoutine,
                    )
                    if (index != routines.lastIndex) {
                        HorizontalDivider(color = GymTheme.colors.divider)
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineListRow(
    routine: com.alejandroestevemaza.gymtimerpro.core.model.Routine,
    uiState: RoutinesUiState,
    onEditRoutine: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = GymTheme.layout.minTapHeight)
            .clickable { onEditRoutine(routine.id) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoutineRowItem(
            modifier = Modifier.weight(1f),
            name = routine.name,
            summary = formatRoutineSummary(
                totalSets = routine.totalSets,
                reps = routine.reps,
                restSeconds = routine.restSeconds,
                weightKg = routine.weightKg,
                timerDisplayFormat = uiState.settings.timerDisplayFormat,
                weightUnitPreference = uiState.settings.weightUnitPreference,
            ),
        )
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = GymTheme.colors.textSecondary,
            modifier = Modifier.padding(start = GymTheme.spacing.s8),
        )
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
            .heightIn(min = GymTheme.layout.minTapHeight)
            .clickable { onEditRoutine(routine.id) }
    ) {
        Column(
            modifier = Modifier.padding(GymTheme.spacing.s16),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s10),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s4),
                ) {
                    RoutineRowItem(
                        name = routine.name,
                        summary = formatRoutineSummary(
                            totalSets = routine.totalSets,
                            reps = routine.reps,
                            restSeconds = routine.restSeconds,
                            weightKg = routine.weightKg,
                            timerDisplayFormat = uiState.settings.timerDisplayFormat,
                            weightUnitPreference = uiState.settings.weightUnitPreference,
                        ),
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
            Row(horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8)) {
                OutlinedButton(onClick = { onEditRoutine(routine.id) }) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(GymTheme.layout.icon18),
                    )
                    Spacer(modifier = Modifier.size(GymTheme.spacing.s8))
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
                    .padding(GymTheme.spacing.s20),
                verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s16),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.classifications_title),
                        style = GymTheme.type.title2Bold,
                        color = GymTheme.colors.textPrimary,
                    )
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                    }
                }

                ClassificationInputBar(
                    text = uiState.classificationSearchQuery,
                    onTextChange = onSearchChanged,
                    onCreate = onStartCreate,
                    canCreate = uiState.classificationSearchQuery.isNotBlank(),
                    showDuplicateError = uiState.classificationDraft?.duplicateError == true,
                    duplicateMessage = stringResource(R.string.classifications_duplicate),
                )

                uiState.classificationDraft?.let { draft ->
                    Column(verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8)) {
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
                                style = GymTheme.type.footnoteRegular,
                                color = GymTheme.colors.error,
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8)) {
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
                        style = GymTheme.type.subheadlineRegular,
                        color = GymTheme.colors.textSecondary,
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.height(GymTheme.layout.classificationListHeight),
                        verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
                    ) {
                        items(filteredClassifications, key = { classification -> classification.id }) { classification ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = GymTheme.spacing.s16,
                                            vertical = GymTheme.spacing.s12,
                                        ),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(text = classification.name)
                                    Row(horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s4)) {
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
        RoutineEditorContent(
            editorState = editorState,
            uiState = uiState,
            onDismiss = onDismiss,
            onNameChanged = onNameChanged,
            onIncreaseSets = onIncreaseSets,
            onDecreaseSets = onDecreaseSets,
            onIncreaseReps = onIncreaseReps,
            onDecreaseReps = onDecreaseReps,
            onIncreaseRest = onIncreaseRest,
            onDecreaseRest = onDecreaseRest,
            onWeightChanged = onWeightChanged,
            onToggleClassification = onToggleClassification,
            onSave = onSave,
            onDeleteRoutine = { showDeleteDialog = true },
            onApplyOrRemoveFromTraining = onApplyOrRemoveFromTraining,
        )
    }
}

@Composable
private fun RoutineEditorInlinePreview(
    editorState: RoutineEditorState,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = GymTheme.spacing.s2),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(
                topStart = GymTheme.radii.r20,
                topEnd = GymTheme.radii.r20,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = GymTheme.spacing.s16, vertical = GymTheme.spacing.s16),
                verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s16),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.routines_cancel),
                        style = GymTheme.type.subheadlineRegular,
                        color = GymTheme.colors.textPrimary,
                        modifier = Modifier
                            .background(
                                color = GymTheme.colors.secondaryButtonFill,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.capsule),
                            )
                            .padding(horizontal = GymTheme.spacing.s12, vertical = GymTheme.spacing.s8),
                    )
                    Text(
                        text = stringResource(R.string.routines_editor_create_title),
                        style = GymTheme.type.headlineSemibold,
                        color = GymTheme.colors.textPrimary,
                    )
                    Text(
                        text = stringResource(R.string.routines_save),
                        style = GymTheme.type.subheadlineSemibold,
                        color = GymTheme.colors.textSecondary,
                        modifier = Modifier
                            .background(
                                color = GymTheme.colors.secondaryButtonFill,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.capsule),
                            )
                            .padding(horizontal = GymTheme.spacing.s12, vertical = GymTheme.spacing.s8),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8)) {
                    Text(
                        text = stringResource(R.string.routines_name_label),
                        style = GymTheme.type.captionRegular,
                        color = GymTheme.colors.textSecondary,
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.r16),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = GymTheme.spacing.s16, vertical = GymTheme.spacing.s12),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = editorState.name.ifBlank { stringResource(R.string.routines_name_label) },
                                style = GymTheme.type.subheadlineRegular,
                                color = GymTheme.colors.textSecondary,
                            )
                            Text(
                                text = stringResource(R.string.routines_name_counter, editorState.nameCount),
                                style = GymTheme.type.captionSemibold,
                                color = GymTheme.colors.textSecondary,
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8)) {
                    Text(
                        text = stringResource(R.string.routines_classifications_label),
                        style = GymTheme.type.captionRegular,
                        color = GymTheme.colors.textSecondary,
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.r16),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = GymTheme.spacing.s16, vertical = GymTheme.spacing.s12),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.routines_classifications_label),
                                style = GymTheme.type.subheadlineRegular,
                                color = GymTheme.colors.textPrimary,
                            )
                            Text(
                                text = stringResource(R.string.progress_summary_none),
                                style = GymTheme.type.subheadlineRegular,
                                color = GymTheme.colors.textSecondary,
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8)) {
                    Text(
                        text = stringResource(R.string.training_config_title),
                        style = GymTheme.type.captionRegular,
                        color = GymTheme.colors.textSecondary,
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.r20),
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            EditorPreviewParameterRow(
                                label = stringResource(R.string.routines_sets_label),
                                value = editorState.totalSets.toString(),
                                showWheel = true,
                            )
                            HorizontalDivider(color = GymTheme.colors.divider)
                            EditorPreviewParameterRow(
                                label = stringResource(R.string.routines_reps_label),
                                value = editorState.reps.toString(),
                                showWheel = true,
                            )
                            HorizontalDivider(color = GymTheme.colors.divider)
                            EditorPreviewParameterRow(
                                label = stringResource(R.string.routines_rest_label),
                                value = editorState.restSeconds.toString(),
                                showWheel = true,
                            )
                            HorizontalDivider(color = GymTheme.colors.divider)
                            EditorPreviewParameterRow(
                                label = stringResource(R.string.routines_weight_label),
                                value = editorState.weightInput.ifBlank { "0" },
                                showWheel = false,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditorPreviewParameterRow(
    label: String,
    value: String,
    showWheel: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = GymTheme.spacing.s16, vertical = GymTheme.spacing.s12),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = GymTheme.type.subheadlineSemibold,
            color = GymTheme.colors.textPrimary,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s10),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value,
                style = GymTheme.type.numericSecondary,
                color = GymTheme.colors.textPrimary,
            )
            if (showWheel) {
                HorizontalWheelStepper(
                    onDecrement = {},
                    onIncrement = {},
                    state = com.alejandroestevemaza.gymtimerpro.core.designsystem.component.GymComponentState.Disabled,
                )
            }
        }
    }
}

@Composable
private fun RoutineEditorContent(
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
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(GymTheme.spacing.s20),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s16),
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
                    style = GymTheme.type.title2Bold,
                    color = GymTheme.colors.textPrimary,
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
                    style = GymTheme.type.footnoteRegular,
                    color = GymTheme.colors.error,
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
                    style = GymTheme.type.footnoteRegular,
                    color = GymTheme.colors.error,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8)) {
                Text(
                    text = stringResource(R.string.routines_classifications_label),
                    style = GymTheme.type.headlineSemibold,
                    color = GymTheme.colors.textPrimary,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
                    verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
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

            Row(horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8)) {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.routines_cancel))
                }
                OutlinedButton(onClick = onSave) {
                    Text(text = stringResource(R.string.routines_save))
                }
            }

            if (editorState.isEditMode) {
                Row(horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8)) {
                    OutlinedButton(onClick = onApplyOrRemoveFromTraining) {
                        Icon(
                            imageVector = if (editorState.isAppliedToTraining) {
                                Icons.Rounded.Close
                            } else {
                                Icons.Rounded.PlayArrow
                            },
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.size(GymTheme.spacing.s8))
                        Text(
                            text = if (editorState.isAppliedToTraining) {
                                stringResource(R.string.routines_remove_from_training)
                            } else {
                                stringResource(R.string.routines_apply_to_training)
                            }
                        )
                    }
                    OutlinedButton(onClick = onDeleteRoutine) {
                        Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.size(GymTheme.spacing.s8))
                        Text(text = stringResource(R.string.routines_delete))
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
                .padding(horizontal = GymTheme.spacing.s16, vertical = GymTheme.spacing.s14),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = label,
                    style = GymTheme.type.captionSemibold,
                    color = GymTheme.colors.textSecondary,
                )
                Text(
                    text = value,
                    style = GymTheme.type.numericSecondary,
                    color = GymTheme.colors.textPrimary,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s4)) {
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
