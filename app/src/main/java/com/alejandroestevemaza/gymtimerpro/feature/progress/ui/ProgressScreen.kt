package com.alejandroestevemaza.gymtimerpro.feature.progress.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alejandroestevemaza.gymtimerpro.R
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.ProgressCalendarDayCell
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTheme
import com.alejandroestevemaza.gymtimerpro.core.model.CalendarDayState
import com.alejandroestevemaza.gymtimerpro.core.model.CalendarWeekState
import com.alejandroestevemaza.gymtimerpro.core.model.DayCompletionState
import com.alejandroestevemaza.gymtimerpro.core.model.PeriodSummary
import com.alejandroestevemaza.gymtimerpro.core.model.ProgressBadgeId
import com.alejandroestevemaza.gymtimerpro.core.model.ProgressBadgeState
import com.alejandroestevemaza.gymtimerpro.core.model.ProgressBucket
import com.alejandroestevemaza.gymtimerpro.core.model.ProgressDerivedState
import com.alejandroestevemaza.gymtimerpro.core.model.ProgressPeriod
import com.alejandroestevemaza.gymtimerpro.core.model.RecentActivityCardState
import com.alejandroestevemaza.gymtimerpro.core.model.RecentActivityType
import com.alejandroestevemaza.gymtimerpro.data.preferences.AppContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun ProgressRoute(
    appContainer: AppContainer,
) {
    val progressViewModel: ProgressViewModel = viewModel(
        factory = ProgressViewModel.factory(
            workoutCompletionRepository = appContainer.workoutCompletionRepository,
            quickWorkoutLabel = stringResource(R.string.training_quick_workout),
        )
    )
    val uiState = progressViewModel.uiState.collectAsStateWithLifecycle()

    ProgressScreen(
        uiState = uiState.value,
        onSelectPeriod = progressViewModel::onSelectPeriod,
        onSelectDay = progressViewModel::onSelectDay,
        onDismissDayDetail = progressViewModel::onDismissDayDetail,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProgressScreen(
    uiState: ProgressUiState,
    onSelectPeriod: (ProgressPeriod) -> Unit,
    onSelectDay: (LocalDate) -> Unit,
    onDismissDayDetail: () -> Unit,
    previewShowInlineDayDetail: Boolean = false,
) {
    val derivedState = uiState.derivedState ?: return
    val locale = remember { Locale.getDefault() }
    val monthFormatter = remember(locale) {
        DateTimeFormatter.ofPattern("LLLL yyyy", locale)
    }
    val dayFormatter = remember(locale) {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = GymTheme.spacing.s16, vertical = GymTheme.spacing.s12),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s20),
        ) {
            Text(
                text = stringResource(R.string.app_navigation_progress),
                style = GymTheme.type.title2Bold,
                color = GymTheme.colors.textPrimary,
            )
            ChartsCard(
                selectedPeriod = uiState.selectedPeriod,
                periodSummary = derivedState.periodSummary,
                onSelectPeriod = onSelectPeriod,
            )
            CalendarCard(
                derivedState = derivedState,
                monthLabel = derivedState.monthStart.format(monthFormatter),
                locale = locale,
                onSelectDay = onSelectDay,
            )
            ActivityCard(recentActivities = derivedState.recentActivities)
            BadgesCard(badges = derivedState.badges)
        }

        if (uiState.selectedDay != null && uiState.selectedDayCompletions.isNotEmpty()) {
            if (previewShowInlineDayDetail) {
                DayDetailInlineOverlay(
                    day = uiState.selectedDay,
                    completions = uiState.selectedDayCompletions,
                    dayFormatter = dayFormatter,
                )
            } else {
                DayDetailSheet(
                    day = uiState.selectedDay,
                    completions = uiState.selectedDayCompletions,
                    dayFormatter = dayFormatter,
                    onDismiss = onDismissDayDetail,
                )
            }
        }
    }
}

@Composable
private fun ChartsCard(
    selectedPeriod: ProgressPeriod,
    periodSummary: PeriodSummary,
    onSelectPeriod: (ProgressPeriod) -> Unit,
) {
    val noneText = stringResource(R.string.progress_summary_none)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(GymTheme.radii.r14),
        color = GymTheme.colors.cardBackground,
        border = BorderStroke(
            width = GymTheme.borders.quaternary,
            color = GymTheme.colors.divider,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GymTheme.spacing.s14),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s14),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.progress_workouts_over_time_title),
                    style = GymTheme.type.headlineSemibold,
                    color = GymTheme.colors.textPrimary,
                )
                Row(
                    modifier = Modifier.clickable {
                        onSelectPeriod(selectedPeriod.next())
                    },
                    horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s2),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(selectedPeriod.labelRes()),
                        style = GymTheme.type.subheadlineRegular,
                        color = GymTheme.colors.iconTint,
                    )
                    Icon(
                        imageVector = Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        tint = GymTheme.colors.iconTint,
                        modifier = Modifier.size(GymTheme.spacing.s16),
                    )
                }
            }

            WorkoutsLineChart(
                buckets = periodSummary.buckets,
                maxY = periodSummary.buckets.maxOfOrNull { it.count } ?: 0,
            )

            HorizontalDivider(color = GymTheme.colors.divider)
            SummaryLine(
                label = stringResource(R.string.progress_summary_top_routine),
                value = periodSummary.mostRepeatedRoutineName ?: noneText,
            )
            SummaryLine(
                label = stringResource(R.string.progress_summary_top_classification),
                value = periodSummary.topClassificationName ?: noneText,
            )
        }
    }
}

@Composable
private fun SummaryLine(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = GymTheme.type.subheadlineRegular,
            color = GymTheme.colors.textPrimary,
        )
        Text(
            text = value,
            style = GymTheme.type.subheadlineRegular,
            color = GymTheme.colors.textSecondary,
        )
    }
}

@Composable
private fun WorkoutsLineChart(
    buckets: List<ProgressBucket>,
    maxY: Int,
) {
    val safeMaxY = maxOf(maxY, 1)
    val spacing = GymTheme.spacing
    val divider = GymTheme.colors.divider
    val accent = GymTheme.colors.iconTint
    val borderWidth = GymTheme.borders.quaternary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(GymTheme.layout.progressChartHeight),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val chartBottom = size.height - spacing.s20.toPx()
            val chartTop = spacing.s8.toPx()
            val chartHeight = chartBottom - chartTop
            val chartWidth = size.width - spacing.s20.toPx()
            val left = 0f
            val right = left + chartWidth

            repeat(4) { index ->
                val y = chartTop + chartHeight * index / 3f
                drawLine(
                    color = divider,
                    start = Offset(left, y),
                    end = Offset(right, y),
                    strokeWidth = borderWidth.toPx(),
                )
            }

            if (buckets.isEmpty()) {
                return@Canvas
            }

            val stepX = if (buckets.size > 1) chartWidth / (buckets.size - 1) else 0f
            val path = Path()
            val points = buckets.mapIndexed { index, bucket ->
                val x = left + stepX * index
                val ratio = bucket.count.toFloat() / safeMaxY.toFloat()
                val y = chartBottom - chartHeight * ratio
                Offset(x, y)
            }
            points.forEachIndexed { index, point ->
                if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
            }

            drawPath(
                path = path,
                color = accent,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = spacing.s2.toPx(),
                    cap = StrokeCap.Round,
                ),
            )
            points.forEach { point ->
                drawCircle(
                    color = accent,
                    radius = spacing.s4.toPx(),
                    center = point,
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = GymTheme.spacing.s2),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            listOf(maxY, (maxY * 2) / 3, maxY / 3, 0).forEach { yLabel ->
                Text(
                    text = yLabel.toString(),
                    style = GymTheme.type.caption2Semibold,
                    color = GymTheme.colors.textSecondary,
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(end = GymTheme.spacing.s20),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            buckets.forEach { bucket ->
                Text(
                    text = bucket.label,
                    style = GymTheme.type.caption2Semibold,
                    color = GymTheme.colors.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun SummaryGrid(
    periodSummary: PeriodSummary,
    noneText: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
        ) {
            SummaryStatCard(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.progress_summary_total_workouts),
                value = periodSummary.totalWorkouts.toString(),
            )
            SummaryStatCard(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.progress_summary_active_days),
                value = periodSummary.activeDays.toString(),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
        ) {
            SummaryStatCard(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.progress_summary_top_routine),
                value = periodSummary.mostRepeatedRoutineName ?: noneText,
            )
            SummaryStatCard(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.progress_summary_top_classification),
                value = periodSummary.topClassificationName ?: noneText,
            )
        }
    }
}

@Composable
private fun SummaryStatCard(
    modifier: Modifier,
    label: String,
    value: String,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(GymTheme.radii.r12),
        color = GymTheme.colors.metricBackground,
        border = BorderStroke(
            width = GymTheme.borders.quaternary,
            color = GymTheme.colors.divider,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GymTheme.spacing.s12),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s6),
        ) {
            Text(
                text = label,
                style = GymTheme.type.captionSemibold,
                color = GymTheme.colors.textSecondary,
            )
            Text(
                text = value,
                style = GymTheme.type.subheadlineSemibold,
                color = GymTheme.colors.textPrimary,
            )
        }
    }
}

@Composable
private fun BucketsChart(
    buckets: List<ProgressBucket>,
) {
    val maxCount = maxOf(buckets.maxOfOrNull { it.count } ?: 0, 1)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(GymTheme.layout.progressChartHeight),
        horizontalArrangement = Arrangement.spacedBy(GymTheme.layout.progressCalendarGridGap),
        verticalAlignment = Alignment.Bottom,
    ) {
        buckets.forEach { bucket ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
            ) {
                Text(
                    text = bucket.count.toString(),
                    style = GymTheme.type.caption2Semibold,
                    color = GymTheme.colors.textSecondary,
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(GymTheme.spacing.s2)
                            .background(GymTheme.colors.secondaryButtonFill)
                    )
                    if (bucket.count > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.72f)
                                .height((96f * bucket.count / maxCount).dp.coerceAtLeast(GymTheme.spacing.s16))
                                .clip(RoundedCornerShape(topStart = GymTheme.radii.r10, topEnd = GymTheme.radii.r10))
                                .background(GymTheme.colors.iconTint)
                        )
                    }
                }
                Text(
                    text = bucket.label,
                    style = GymTheme.type.caption2Semibold,
                    textAlign = TextAlign.Center,
                    color = GymTheme.colors.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun CalendarCard(
    derivedState: ProgressDerivedState,
    monthLabel: String,
    locale: Locale,
    onSelectDay: (LocalDate) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(GymTheme.radii.r14),
        color = GymTheme.colors.cardBackground,
        border = BorderStroke(
            width = GymTheme.borders.quaternary,
            color = GymTheme.colors.divider,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GymTheme.spacing.s14),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
        ) {
            Text(
                text = monthLabel.replaceFirstChar { char ->
                    if (char.isLowerCase()) char.titlecase(locale) else char.toString()
                },
                style = GymTheme.type.title2Bold,
                color = GymTheme.colors.textPrimary,
            )
            WeekdayHeader(locale = locale)
            derivedState.calendarWeeks.forEach { week ->
                CalendarWeekRow(
                    week = week,
                    activeWeeklyStreak = derivedState.activeWeeklyStreak,
                    onSelectDay = onSelectDay,
                )
            }
        }
    }
}

@Composable
private fun WeekdayHeader(
    locale: Locale,
) {
    val dayLabels = remember(locale) {
        orderedDaysOfWeek(locale).map { dayOfWeek ->
            dayOfWeek.getDisplayName(TextStyle.NARROW, locale)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(GymTheme.layout.progressCalendarGridGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        dayLabels.forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = GymTheme.type.caption2Semibold,
                textAlign = TextAlign.Center,
                color = GymTheme.colors.textSecondary,
            )
        }
        Spacer(modifier = Modifier.width(GymTheme.layout.progressStreakColumnWidth))
    }
}

@Composable
private fun CalendarWeekRow(
    week: CalendarWeekState,
    activeWeeklyStreak: Int,
    onSelectDay: (LocalDate) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(GymTheme.layout.progressCalendarGridGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        week.days.forEach { day ->
            CalendarDayCell(
                modifier = Modifier.weight(1f),
                day = day,
                onSelectDay = onSelectDay,
            )
        }
        Box(
            modifier = Modifier.width(GymTheme.layout.progressStreakColumnWidth),
            contentAlignment = Alignment.Center,
        ) {
            if (week.showStreakIndicator) {
                StreakIndicator(streak = activeWeeklyStreak)
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    modifier: Modifier,
    day: CalendarDayState,
    onSelectDay: (LocalDate) -> Unit,
) {
    ProgressCalendarDayCell(
        modifier = modifier.aspectRatio(1f),
        dayLabel = day.date.dayOfMonth.toString(),
        hasWorkout = day.inCurrentMonth && day.workoutCount > 0,
        isCurrentMonth = day.inCurrentMonth,
        isToday = day.isToday,
        isPast = !day.isFuture && !day.isToday,
        isFuture = day.isFuture,
        onClick = if (day.inCurrentMonth && day.workoutCount > 0) {
            { onSelectDay(day.date) }
        } else {
            null
        },
    )
}

@Composable
private fun StreakIndicator(
    streak: Int,
) {
    Surface(
        shape = CircleShape,
        color = if (streak > 0) {
            GymTheme.colors.calendarStreak.copy(alpha = 0.14f)
        } else {
            Color.Transparent
        },
        border = BorderStroke(
            width = GymTheme.borders.quaternary,
            color = if (streak > 0) {
                GymTheme.colors.calendarStreak.copy(alpha = 0.45f)
            } else {
                GymTheme.colors.textSecondary.copy(alpha = 0.4f)
            },
        ),
    ) {
        Box(
            modifier = Modifier.size(GymTheme.layout.progressStreakIndicatorSize),
            contentAlignment = Alignment.Center,
        ) {
            if (streak > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s2),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocalFireDepartment,
                        contentDescription = null,
                        tint = GymTheme.colors.calendarStreak,
                        modifier = Modifier.size(GymTheme.spacing.s14),
                    )
                    Text(
                        text = streak.toString(),
                        style = GymTheme.type.tinyFlame,
                        color = GymTheme.colors.calendarStreak,
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Rounded.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = GymTheme.colors.textSecondary.copy(alpha = 0.7f),
                    modifier = Modifier.size(GymTheme.spacing.s16),
                )
            }
        }
    }
}

@Composable
private fun ActivityCard(
    recentActivities: List<RecentActivityCardState>,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(GymTheme.radii.r14),
        color = GymTheme.colors.cardBackground,
        border = BorderStroke(
            width = GymTheme.borders.quaternary,
            color = GymTheme.colors.divider,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GymTheme.spacing.s14),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
        ) {
            Text(
                text = stringResource(R.string.progress_activity_title),
                style = GymTheme.type.headlineSemibold,
                color = GymTheme.colors.textPrimary,
            )

            if (recentActivities.isEmpty()) {
                Text(
                    text = stringResource(R.string.progress_activity_empty),
                    style = GymTheme.type.subheadlineRegular,
                    color = GymTheme.colors.textSecondary,
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
                ) {
                    recentActivities.forEach { activity ->
                        ActivityItem(
                            modifier = Modifier.weight(1f),
                            activity = activity,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityItem(
    modifier: Modifier,
    activity: RecentActivityCardState,
) {
    val accentColor = if (activity.type == RecentActivityType.Classification) {
        GymTheme.colors.iconTint
    } else {
        GymTheme.colors.completed
    }
    val icon = if (activity.type == RecentActivityType.Classification) {
        Icons.Rounded.LocalOffer
    } else {
        Icons.Rounded.FitnessCenter
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(GymTheme.radii.r12),
        color = GymTheme.colors.metricBackground,
        border = BorderStroke(
            width = GymTheme.borders.quaternary,
            color = GymTheme.colors.divider,
        ),
    ) {
        Column(
            modifier = Modifier
                .heightIn(min = GymTheme.layout.progressActivityMinHeight)
                .fillMaxWidth()
                .padding(GymTheme.spacing.s10),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s10),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(GymTheme.spacing.s20),
            )
            Text(
                text = activity.title,
                style = GymTheme.type.subheadlineSemibold,
                color = GymTheme.colors.textPrimary,
            )
            Text(
                text = activity.dateTimeText,
                style = GymTheme.type.footnoteRegular,
                color = GymTheme.colors.textSecondary,
            )
        }
    }
}

@Composable
private fun BadgesCard(
    badges: List<ProgressBadgeState>,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(GymTheme.radii.r14),
        color = GymTheme.colors.cardBackground,
        border = BorderStroke(
            width = GymTheme.borders.quaternary,
            color = GymTheme.colors.divider,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GymTheme.spacing.s14),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
        ) {
            Text(
                text = stringResource(R.string.progress_badges_title),
                style = GymTheme.type.headlineSemibold,
                color = GymTheme.colors.textPrimary,
            )

            badges.chunked(2).forEach { rowBadges ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s10),
                ) {
                    rowBadges.forEach { badge ->
                        BadgeCard(
                            modifier = Modifier.weight(1f),
                            badge = badge,
                        )
                    }
                    if (rowBadges.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeCard(
    modifier: Modifier,
    badge: ProgressBadgeState,
) {
    val icon = if (badge.unlocked) Icons.Rounded.MilitaryTech else Icons.Rounded.Lock
    val iconTint = if (badge.unlocked) {
        GymTheme.colors.badgeUnlocked
    } else {
        GymTheme.colors.textSecondary
    }
    val containerColor = if (badge.unlocked) {
        GymTheme.colors.badgeUnlocked.copy(alpha = 0.15f)
    } else {
        GymTheme.colors.metricBackground
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(GymTheme.radii.r12),
        color = containerColor,
        border = BorderStroke(
            width = GymTheme.borders.quaternary,
            color = if (badge.unlocked) {
                GymTheme.colors.badgeUnlocked.copy(alpha = 0.45f)
            } else {
                GymTheme.colors.divider
            },
        ),
    ) {
        Column(
            modifier = Modifier
                .heightIn(min = GymTheme.layout.progressBadgeMinHeight)
                .fillMaxWidth()
                .padding(GymTheme.spacing.s10),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s10),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(GymTheme.spacing.s20),
            )
            Text(
                text = stringResource(badge.id.titleRes()),
                style = GymTheme.type.subheadlineSemibold,
                color = GymTheme.colors.textPrimary,
            )
            Text(
                text = stringResource(badge.id.subtitleRes()),
                style = GymTheme.type.footnoteRegular,
                color = GymTheme.colors.textSecondary,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayDetailSheet(
    day: LocalDate,
    completions: List<DayCompletionState>,
    dayFormatter: DateTimeFormatter,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GymTheme.spacing.s20, vertical = GymTheme.spacing.s12),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s4),
        ) {
            Text(
                text = day.format(dayFormatter),
                style = GymTheme.type.title2Bold,
                color = GymTheme.colors.textPrimary,
            )
            completions.forEach { completion ->
                ListItem(
                    headlineContent = { Text(text = completion.routineName) },
                    supportingContent = { Text(text = completion.completionTimeText) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.FitnessCenter,
                            contentDescription = null,
                        )
                    },
                )
            }
            Spacer(modifier = Modifier.height(GymTheme.spacing.s12))
        }
    }
}

@Composable
private fun DayDetailInlineOverlay(
    day: LocalDate,
    completions: List<DayCompletionState>,
    dayFormatter: DateTimeFormatter,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.08f)),
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = GymTheme.spacing.s2, vertical = GymTheme.spacing.s24)
                .fillMaxWidth(),
            shape = RoundedCornerShape(GymTheme.radii.r20),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = GymTheme.spacing.s16, vertical = GymTheme.spacing.s20),
                verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s16),
            ) {
                Text(
                    text = day.format(dayFormatter),
                    modifier = Modifier.fillMaxWidth(),
                    style = GymTheme.type.headlineSemibold,
                    textAlign = TextAlign.Center,
                    color = GymTheme.colors.textPrimary,
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(GymTheme.radii.r16),
                    color = GymTheme.colors.cardBackground,
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        completions.forEachIndexed { index, completion ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = GymTheme.spacing.s16, vertical = GymTheme.spacing.s12),
                                verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s2),
                            ) {
                                Text(
                                    text = completion.routineName,
                                    style = GymTheme.type.headlineSemibold,
                                    color = GymTheme.colors.textPrimary,
                                )
                                Text(
                                    text = completion.completionTimeText,
                                    style = GymTheme.type.footnoteRegular,
                                    color = GymTheme.colors.textSecondary,
                                )
                            }
                            if (index != completions.lastIndex) {
                                HorizontalDivider(color = GymTheme.colors.divider)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun ProgressPeriod.next(): ProgressPeriod {
    val values = ProgressPeriod.entries
    val currentIndex = values.indexOf(this)
    return values[(currentIndex + 1) % values.size]
}

private fun ProgressPeriod.labelRes(): Int = when (this) {
    ProgressPeriod.Week -> R.string.progress_period_week
    ProgressPeriod.Fortnight -> R.string.progress_period_fortnight
    ProgressPeriod.Month -> R.string.progress_period_month
    ProgressPeriod.Quarter -> R.string.progress_period_quarter
    ProgressPeriod.Year -> R.string.progress_period_year
}

private fun ProgressBadgeId.titleRes(): Int = when (this) {
    ProgressBadgeId.FirstWorkout -> R.string.progress_badge_first_workout_title
    ProgressBadgeId.Workouts5 -> R.string.progress_badge_workouts_5_title
    ProgressBadgeId.Workouts10 -> R.string.progress_badge_workouts_10_title
    ProgressBadgeId.Workouts25 -> R.string.progress_badge_workouts_25_title
    ProgressBadgeId.ThreeWeek -> R.string.progress_badge_three_week_title
    ProgressBadgeId.Streak4 -> R.string.progress_badge_streak_4_title
}

private fun ProgressBadgeId.subtitleRes(): Int = when (this) {
    ProgressBadgeId.FirstWorkout -> R.string.progress_badge_first_workout_subtitle
    ProgressBadgeId.Workouts5 -> R.string.progress_badge_workouts_5_subtitle
    ProgressBadgeId.Workouts10 -> R.string.progress_badge_workouts_10_subtitle
    ProgressBadgeId.Workouts25 -> R.string.progress_badge_workouts_25_subtitle
    ProgressBadgeId.ThreeWeek -> R.string.progress_badge_three_week_subtitle
    ProgressBadgeId.Streak4 -> R.string.progress_badge_streak_4_subtitle
}

private fun orderedDaysOfWeek(locale: Locale): List<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(locale).firstDayOfWeek
    return List(7) { offset ->
        DayOfWeek.of(((firstDayOfWeek.value - 1 + offset) % 7) + 1)
    }
}
