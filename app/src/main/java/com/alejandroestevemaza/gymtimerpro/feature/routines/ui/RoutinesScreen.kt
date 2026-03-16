package com.alejandroestevemaza.gymtimerpro.feature.routines.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.MonitorWeight
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alejandroestevemaza.gymtimerpro.R
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.HorizontalWheelStepper
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.NumericConfigRow
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
        onEditorSetsChanged = routinesViewModel::onEditorSetsChanged,
        onEditorRepsChanged = routinesViewModel::onEditorRepsChanged,
        onEditorRestChanged = routinesViewModel::onEditorRestChanged,
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
    onEditorSetsChanged: (Int) -> Unit,
    onEditorRepsChanged: (Int) -> Unit,
    onEditorRestChanged: (Int) -> Unit,
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
                onSetsChanged = onEditorSetsChanged,
                onRepsChanged = onEditorRepsChanged,
                onRestChanged = onEditorRestChanged,
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
                            uiState = uiState,
                            onToggleSection = onToggleSection,
                            onEditRoutine = onEditRoutine,
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
    uiState: RoutinesUiState,
    onToggleSection: (String) -> Unit,
    onEditRoutine: (String) -> Unit,
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
                // Header de la sección (clickable para expandir/colapsar)
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

                // Rutinas de la sección (visibles solo cuando está expandida)
                if (section.isExpanded) {
                    if (section.routines.isEmpty()) {
                        Text(
                            text = stringResource(R.string.routines_empty_body),
                            style = GymTheme.type.subheadlineRegular,
                            color = GymTheme.colors.textSecondary,
                            modifier = Modifier.padding(
                                start = GymTheme.spacing.s8,
                                bottom = GymTheme.spacing.s8,
                            ),
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = GymTheme.spacing.s8),
                            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s4),
                        ) {
                            section.routines.forEachIndexed { routineIndex, routine ->
                                HorizontalDivider(color = GymTheme.colors.divider)
                                RoutineListRow(
                                    routine = routine,
                                    uiState = uiState,
                                    onEditRoutine = onEditRoutine,
                                )
                            }
                        }
                    }
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
    var pendingDeleteClassificationId by remember { mutableStateOf<String?>(null) }
    val pendingDeleteClassification = uiState.classifications.firstOrNull { classification ->
        classification.id == pendingDeleteClassificationId
    }
    if (pendingDeleteClassification != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteClassificationId = null },
            title = {
                Text(text = stringResource(R.string.classifications_delete))
            },
            text = {
                Text(text = pendingDeleteClassification.name)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(pendingDeleteClassification.id)
                        pendingDeleteClassificationId = null
                    },
                ) {
                    Text(text = stringResource(R.string.routines_delete_confirm_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteClassificationId = null }) {
                    Text(text = stringResource(R.string.routines_cancel))
                }
            },
        )
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                (view.parent as? DialogWindowProvider)?.window?.apply {
                    setBackgroundDrawableResource(android.R.color.transparent)
                    setLayout(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = GymTheme.spacing.s20),
                shape = RoundedCornerShape(28.dp),
                color = GymTheme.colors.cardBackground,
                shadowElevation = 20.dp,
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    // ── Header estilo Apple ──────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = GymTheme.spacing.s16,
                                end = GymTheme.spacing.s4,
                                top = GymTheme.spacing.s16,
                                bottom = GymTheme.spacing.s16,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.classifications_title),
                            style = GymTheme.type.subheadlineSemibold,
                            color = GymTheme.colors.textPrimary,
                        )
                        TextButton(
                            onClick = onClose,
                            modifier = Modifier.align(Alignment.CenterEnd),
                        ) {
                            Text(
                                text = stringResource(R.string.routines_done),
                                style = GymTheme.type.subheadlineSemibold,
                                color = GymTheme.colors.iconTint,
                            )
                        }
                    }

                    HorizontalDivider(color = GymTheme.colors.divider)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(GymTheme.spacing.s16),
                        verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
                    ) {

                        // ── Buscador estilo iOS pill ──────────────────────
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = GymTheme.colors.secondaryButtonFill,
                                    shape = RoundedCornerShape(10.dp),
                                )
                                .padding(
                                    start = GymTheme.spacing.s10,
                                    end = GymTheme.spacing.s4,
                                    top = GymTheme.spacing.s4,
                                    bottom = GymTheme.spacing.s4,
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s6),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null,
                                tint = GymTheme.colors.textSecondary,
                                modifier = Modifier.size(GymTheme.spacing.s20),
                            )
                            TextField(
                                value = uiState.classificationSearchQuery,
                                onValueChange = onSearchChanged,
                                modifier = Modifier.weight(1f),
                                textStyle = GymTheme.type.subheadlineRegular,
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.routines_search_hint),
                                        style = GymTheme.type.subheadlineRegular,
                                        color = GymTheme.colors.textSecondary,
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                ),
                                singleLine = true,
                            )
                            AnimatedVisibility(visible = uiState.classificationSearchQuery.isNotBlank()) {
                                IconButton(
                                    onClick = { onSearchChanged("") },
                                    modifier = Modifier.size(40.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = stringResource(R.string.routines_cancel),
                                        tint = GymTheme.colors.textSecondary,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }
                            AnimatedVisibility(visible = uiState.classificationDraft == null) {
                                IconButton(onClick = onStartCreate) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(
                                                color = GymTheme.colors.iconTint,
                                                shape = CircleShape,
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Add,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp),
                                        )
                                    }
                                }
                            }
                        }

                        // ── Sección de creación / renombrado ─────────────
                        AnimatedVisibility(visible = uiState.classificationDraft != null) {
                            uiState.classificationDraft?.let { draft ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = GymTheme.colors.secondaryButtonFill,
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                horizontal = GymTheme.spacing.s16,
                                                vertical = GymTheme.spacing.s12,
                                            ),
                                        verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
                                    ) {
                                        Text(
                                            text = if (draft.isCreating) {
                                                stringResource(R.string.classifications_new_name)
                                            } else {
                                                stringResource(R.string.classifications_rename)
                                            },
                                            style = GymTheme.type.captionSemibold,
                                            color = GymTheme.colors.iconTint,
                                        )
                                        TextField(
                                            value = draft.value,
                                            onValueChange = onDraftChanged,
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = Color.Transparent,
                                                unfocusedContainerColor = Color.Transparent,
                                                focusedIndicatorColor = GymTheme.colors.iconTint,
                                                unfocusedIndicatorColor = GymTheme.colors.divider,
                                                disabledIndicatorColor = Color.Transparent,
                                            ),
                                            textStyle = GymTheme.type.subheadlineRegular,
                                            placeholder = {
                                                Text(
                                                    text = stringResource(R.string.classifications_new_name),
                                                    style = GymTheme.type.subheadlineRegular,
                                                    color = GymTheme.colors.textSecondary,
                                                )
                                            },
                                        )
                                        if (draft.duplicateError) {
                                            Text(
                                                text = stringResource(R.string.classifications_duplicate),
                                                style = GymTheme.type.captionRegular,
                                                color = GymTheme.colors.error,
                                            )
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            TextButton(onClick = onCancelDraft) {
                                                Text(
                                                    text = stringResource(R.string.routines_cancel),
                                                    style = GymTheme.type.subheadlineRegular,
                                                    color = GymTheme.colors.textSecondary,
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(GymTheme.spacing.s8))
                                            Button(
                                                onClick = onSaveDraft,
                                                enabled = draft.value.isNotBlank(),
                                            ) {
                                                Text(
                                                    text = if (draft.isCreating) {
                                                        stringResource(R.string.classifications_create)
                                                    } else {
                                                        stringResource(R.string.routines_save)
                                                    },
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ── Lista agrupada estilo iOS ─────────────────────
                        val filteredClassifications = uiState.classifications.filter { classification ->
                            uiState.classificationSearchQuery.isBlank() || classification.name.contains(
                                uiState.classificationSearchQuery,
                                ignoreCase = true,
                            )
                        }

                        if (filteredClassifications.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = GymTheme.spacing.s20),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = stringResource(R.string.classifications_empty),
                                    style = GymTheme.type.subheadlineRegular,
                                    color = GymTheme.colors.textSecondary,
                                )
                            }
                        } else {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = GymTheme.colors.secondaryButtonFill,
                            ) {
                                LazyColumn(
                                    modifier = Modifier.heightIn(max = GymTheme.layout.classificationListHeight),
                                ) {
                                    itemsIndexed(
                                        filteredClassifications,
                                        key = { _, classification -> classification.id },
                                    ) { index, classification ->
                                        Column {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        start = GymTheme.spacing.s16,
                                                        end = GymTheme.spacing.s4,
                                                        top = GymTheme.spacing.s4,
                                                        bottom = GymTheme.spacing.s4,
                                                    ),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Text(
                                                    text = classification.name,
                                                    style = GymTheme.type.subheadlineRegular,
                                                    color = GymTheme.colors.textPrimary,
                                                    modifier = Modifier.weight(1f),
                                                )
                                                Row {
                                                    IconButton(
                                                        onClick = { onStartRename(classification.id) },
                                                        modifier = Modifier.size(40.dp),
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Rounded.Edit,
                                                            contentDescription = null,
                                                            tint = GymTheme.colors.iconTint,
                                                            modifier = Modifier.size(18.dp),
                                                        )
                                                    }
                                                    IconButton(
                                                        onClick = {
                                                            pendingDeleteClassificationId = classification.id
                                                        },
                                                        modifier = Modifier.size(40.dp),
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Rounded.Delete,
                                                            contentDescription = null,
                                                            tint = GymTheme.colors.error,
                                                            modifier = Modifier.size(18.dp),
                                                        )
                                                    }
                                                }
                                            }
                                            if (index < filteredClassifications.lastIndex) {
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(start = GymTheme.spacing.s16),
                                                    color = GymTheme.colors.divider,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(GymTheme.spacing.s8))
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
    onSetsChanged: (Int) -> Unit,
    onRepsChanged: (Int) -> Unit,
    onRestChanged: (Int) -> Unit,
    onWeightChanged: (String) -> Unit,
    onToggleClassification: (String) -> Unit,
    onSave: () -> Unit,
    onDeleteRoutine: () -> Unit,
    onApplyOrRemoveFromTraining: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showClassificationPicker by remember { mutableStateOf(false) }
    var classificationPickerQuery by remember { mutableStateOf("") }

    if (showClassificationPicker) {
        RoutineClassificationPickerDialog(
            uiState = uiState,
            selectedClassificationIds = editorState.selectedClassificationIds,
            searchQuery = classificationPickerQuery,
            onSearchQueryChanged = { classificationPickerQuery = it },
            onToggleClassification = onToggleClassification,
            onDismiss = { showClassificationPicker = false },
        )
    }

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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                (view.parent as? DialogWindowProvider)?.window?.apply {
                    setBackgroundDrawableResource(android.R.color.transparent)
                    setLayout(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
        RoutineEditorContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GymTheme.spacing.s16),
            editorState = editorState,
            uiState = uiState,
            onDismiss = onDismiss,
            onNameChanged = onNameChanged,
            onSetsChanged = onSetsChanged,
            onRepsChanged = onRepsChanged,
            onRestChanged = onRestChanged,
            onWeightChanged = onWeightChanged,
            onToggleClassification = onToggleClassification,
            onOpenClassificationPicker = { showClassificationPicker = true },
            onSave = onSave,
            onDeleteRoutine = { showDeleteDialog = true },
            onApplyOrRemoveFromTraining = onApplyOrRemoveFromTraining,
        )
        } // Box
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
                    value = 50,
                    valueRange = 0..100,
                    step = 10,
                    onValueChange = {},
                    state = com.alejandroestevemaza.gymtimerpro.core.designsystem.component.GymComponentState.Disabled,
                )
            }
        }
    }
}

@Composable
private fun RoutineEditorContent(
    modifier: Modifier = Modifier,
    editorState: RoutineEditorState,
    uiState: RoutinesUiState,
    onDismiss: () -> Unit,
    onNameChanged: (String) -> Unit,
    onSetsChanged: (Int) -> Unit,
    onRepsChanged: (Int) -> Unit,
    onRestChanged: (Int) -> Unit,
    onWeightChanged: (String) -> Unit,
    onToggleClassification: (String) -> Unit,
    onOpenClassificationPicker: () -> Unit,
    onSave: () -> Unit,
    onDeleteRoutine: () -> Unit,
    onApplyOrRemoveFromTraining: () -> Unit,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GymTheme.colors.cardBackground),
    ) {
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
                trailingIcon = {
                    Text(
                        text = stringResource(R.string.routines_name_counter, editorState.nameCount),
                        style = GymTheme.type.captionRegular,
                        color = GymTheme.colors.textSecondary,
                        modifier = Modifier.padding(end = GymTheme.spacing.s8),
                    )
                },
                isError = editorState.shouldShowNameError,
                singleLine = true,
            )
            if (editorState.shouldShowNameError) {
                Text(
                    text = stringResource(R.string.routines_invalid_name),
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenClassificationPicker),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = GymTheme.spacing.s16, vertical = GymTheme.spacing.s12),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s4),
                        ) {
                            Text(
                                text = stringResource(R.string.routines_classifications_label),
                                style = GymTheme.type.captionSemibold,
                                color = GymTheme.colors.textSecondary,
                            )
                            val selectedNames = uiState.classifications
                                .filter { it.id in editorState.selectedClassificationIds }
                                .map { it.name }
                            Text(
                                text = selectedNames
                                    .takeIf { it.isNotEmpty() }
                                    ?.joinToString(separator = ", ")
                                    ?: stringResource(R.string.progress_summary_none),
                                style = GymTheme.type.subheadlineRegular,
                                color = if (selectedNames.isEmpty()) {
                                    GymTheme.colors.textSecondary
                                } else {
                                    GymTheme.colors.textPrimary
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            tint = GymTheme.colors.textSecondary,
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8)) {
                Text(
                    text = stringResource(R.string.routines_parameters_label),
                    style = GymTheme.type.headlineSemibold,
                    color = GymTheme.colors.textPrimary,
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(GymTheme.radii.r20),
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        StepperEditorRow(
                            icon = Icons.Rounded.Layers,
                            label = stringResource(R.string.routines_sets_label),
                            valueText = editorState.totalSets.toString(),
                            value = editorState.totalSets,
                            valueRange = 1..uiState.settings.maxSetsPreference.maxSets,
                            onValueChanged = onSetsChanged,
                        )
                        HorizontalDivider(color = GymTheme.colors.divider)
                        StepperEditorRow(
                            icon = Icons.Rounded.FitnessCenter,
                            label = stringResource(R.string.routines_reps_label),
                            valueText = editorState.reps.toString(),
                            value = editorState.reps,
                            valueRange = 1..30,
                            onValueChanged = onRepsChanged,
                        )
                        HorizontalDivider(color = GymTheme.colors.divider)
                        StepperEditorRow(
                            icon = Icons.Rounded.Timer,
                            label = stringResource(R.string.routines_rest_label),
                            valueText = com.alejandroestevemaza.gymtimerpro.core.format.formatDuration(
                                editorState.restSeconds,
                                uiState.settings.timerDisplayFormat,
                            ),
                            value = editorState.restSeconds,
                            valueRange = 15..300,
                            valueStep = uiState.settings.restIncrementPreference.seconds,
                            onValueChanged = onRestChanged,
                        )
                        HorizontalDivider(color = GymTheme.colors.divider)
                        WeightEditorRow(
                            value = editorState.weightInput,
                            isWeightValid = editorState.isWeightValid,
                            onWeightChanged = onWeightChanged,
                        )
                    }
                }
                if (!editorState.isWeightValid) {
                    Text(
                        text = stringResource(R.string.routines_invalid_weight),
                        style = GymTheme.type.footnoteRegular,
                        color = GymTheme.colors.error,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(R.string.routines_cancel))
                }
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                ) {
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
private fun RoutineClassificationPickerDialog(
    uiState: RoutinesUiState,
    selectedClassificationIds: Set<String>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onToggleClassification: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                (view.parent as? DialogWindowProvider)?.window?.apply {
                    setBackgroundDrawableResource(android.R.color.transparent)
                    setLayout(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = GymTheme.spacing.s20),
                shape = RoundedCornerShape(28.dp),
                color = GymTheme.colors.cardBackground,
                shadowElevation = 20.dp,
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = GymTheme.spacing.s16,
                                end = GymTheme.spacing.s4,
                                top = GymTheme.spacing.s16,
                                bottom = GymTheme.spacing.s16,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.routines_classifications_label),
                            style = GymTheme.type.subheadlineSemibold,
                            color = GymTheme.colors.textPrimary,
                        )
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.align(Alignment.CenterEnd),
                        ) {
                            Text(
                                text = stringResource(R.string.routines_done),
                                style = GymTheme.type.subheadlineSemibold,
                                color = GymTheme.colors.iconTint,
                            )
                        }
                    }

                    HorizontalDivider(color = GymTheme.colors.divider)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(GymTheme.spacing.s16),
                        verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = GymTheme.colors.secondaryButtonFill,
                                    shape = RoundedCornerShape(10.dp),
                                )
                                .padding(
                                    start = GymTheme.spacing.s10,
                                    end = GymTheme.spacing.s4,
                                    top = GymTheme.spacing.s4,
                                    bottom = GymTheme.spacing.s4,
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s6),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null,
                                tint = GymTheme.colors.textSecondary,
                                modifier = Modifier.size(GymTheme.spacing.s20),
                            )
                            TextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChanged,
                                modifier = Modifier.weight(1f),
                                textStyle = GymTheme.type.subheadlineRegular,
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.routines_search_hint),
                                        style = GymTheme.type.subheadlineRegular,
                                        color = GymTheme.colors.textSecondary,
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                ),
                                singleLine = true,
                            )
                            AnimatedVisibility(visible = searchQuery.isNotBlank()) {
                                IconButton(
                                    onClick = { onSearchQueryChanged("") },
                                    modifier = Modifier.size(40.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = stringResource(R.string.routines_cancel),
                                        tint = GymTheme.colors.textSecondary,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }
                        }

                        val filteredClassifications = uiState.classifications.filter { classification ->
                            searchQuery.isBlank() || classification.name.contains(
                                searchQuery,
                                ignoreCase = true,
                            )
                        }

                        if (filteredClassifications.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = GymTheme.spacing.s20),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = stringResource(R.string.classifications_empty),
                                    style = GymTheme.type.subheadlineRegular,
                                    color = GymTheme.colors.textSecondary,
                                )
                            }
                        } else {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = GymTheme.colors.secondaryButtonFill,
                            ) {
                                LazyColumn(
                                    modifier = Modifier.heightIn(max = GymTheme.layout.classificationListHeight),
                                ) {
                                    itemsIndexed(
                                        filteredClassifications,
                                        key = { _, classification -> classification.id },
                                    ) { index, classification ->
                                        val isSelected = classification.id in selectedClassificationIds
                                        Column {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { onToggleClassification(classification.id) }
                                                    .padding(
                                                        horizontal = GymTheme.spacing.s16,
                                                        vertical = GymTheme.spacing.s12,
                                                    ),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Text(
                                                    text = classification.name,
                                                    style = GymTheme.type.subheadlineRegular,
                                                    color = GymTheme.colors.textPrimary,
                                                    modifier = Modifier.weight(1f),
                                                )
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Rounded.Check,
                                                        contentDescription = null,
                                                        tint = GymTheme.colors.iconTint,
                                                        modifier = Modifier.size(GymTheme.layout.icon18),
                                                    )
                                                }
                                            }
                                            if (index < filteredClassifications.lastIndex) {
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(start = GymTheme.spacing.s16),
                                                    color = GymTheme.colors.divider,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(GymTheme.spacing.s8))
                }
            }
        }
    }
}

@Composable
private fun StepperEditorRow(
    icon: ImageVector,
    label: String,
    valueText: String,
    value: Int,
    valueRange: IntRange,
    valueStep: Int = 1,
    onValueChanged: (Int) -> Unit,
) {
    NumericConfigRow(
        icon = icon,
        title = label,
        valueText = valueText,
        value = value,
        valueRange = valueRange,
        valueStep = valueStep,
        onValueChange = onValueChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = GymTheme.spacing.s16),
    )
}

@Composable
private fun WeightEditorRow(
    value: String,
    isWeightValid: Boolean,
    onWeightChanged: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = GymTheme.layout.minTapHeight)
            .padding(horizontal = GymTheme.spacing.s16),
        horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
        ) {
            Box(
                modifier = Modifier
                    .size(GymTheme.layout.configIconFrame)
                    .background(
                        color = GymTheme.colors.iconBackground,
                        shape = RoundedCornerShape(GymTheme.radii.r8),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.MonitorWeight,
                    contentDescription = null,
                    tint = GymTheme.colors.iconTint,
                    modifier = Modifier.size(GymTheme.layout.configIconGlyph),
                )
            }
            Text(
                text = stringResource(R.string.routines_weight_label),
                style = GymTheme.type.valueLabel,
                color = GymTheme.colors.textPrimary,
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onWeightChanged,
            modifier = Modifier.width(130.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = !isWeightValid,
            singleLine = true,
            textStyle = GymTheme.type.numericSecondary,
        )
    }
}
