package com.alejandroestevemaza.gymtimerpro.feature.progress.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alejandroestevemaza.gymtimerpro.R
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

private val RoutineActivityGreen = Color(0xFF4E7D4A)

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
) {
    val derivedState = uiState.derivedState ?: return
    val locale = remember { Locale.getDefault() }
    val monthFormatter = remember(locale) {
        DateTimeFormatter.ofPattern("LLLL yyyy", locale)
    }
    val dayFormatter = remember(locale) {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale)
    }

    if (uiState.selectedDay != null && uiState.selectedDayCompletions.isNotEmpty()) {
        DayDetailSheet(
            day = uiState.selectedDay,
            completions = uiState.selectedDayCompletions,
            dayFormatter = dayFormatter,
            onDismiss = onDismissDayDetail,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
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
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChartsCard(
    selectedPeriod: ProgressPeriod,
    periodSummary: PeriodSummary,
    onSelectPeriod: (ProgressPeriod) -> Unit,
) {
    val noneText = stringResource(R.string.progress_summary_none)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SectionHeader(
                icon = Icons.Rounded.BarChart,
                title = stringResource(R.string.progress_charts_title),
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ProgressPeriod.entries.forEach { period ->
                    FilterChip(
                        selected = period == selectedPeriod,
                        onClick = { onSelectPeriod(period) },
                        label = { Text(text = stringResource(period.labelRes())) },
                    )
                }
            }

            SummaryGrid(
                periodSummary = periodSummary,
                noneText = noneText,
            )

            BucketsChart(buckets = periodSummary.buckets)
        }
    }
}

@Composable
private fun SummaryGrid(
    periodSummary: PeriodSummary,
    noneText: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
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
            horizontalArrangement = Arrangement.spacedBy(12.dp),
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
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
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
            .height(164.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        buckets.forEach { bucket ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = bucket.count.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            .height(2.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                    if (bucket.count > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.72f)
                                .height((96f * bucket.count / maxCount).dp.coerceAtLeast(16.dp))
                                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
                Text(
                    text = bucket.label,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SectionHeader(
                icon = Icons.Rounded.CalendarMonth,
                title = stringResource(R.string.progress_calendar_title),
            )
            Text(
                text = monthLabel.replaceFirstChar { char ->
                    if (char.isLowerCase()) char.titlecase(locale) else char.toString()
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        dayLabels.forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.width(40.dp))
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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
            modifier = Modifier.width(40.dp),
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
    val isWorkoutDay = day.inCurrentMonth && day.workoutCount > 0
    val isInteractive = isWorkoutDay
    val backgroundColor: Color
    val contentColor: Color
    val border: BorderStroke?

    when {
        isWorkoutDay -> {
            backgroundColor = MaterialTheme.colorScheme.primary
            contentColor = MaterialTheme.colorScheme.onPrimary
            border = null
        }

        !day.inCurrentMonth -> {
            backgroundColor = Color.Transparent
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.42f)
            border = null
        }

        day.isToday -> {
            backgroundColor = Color.Transparent
            contentColor = MaterialTheme.colorScheme.primary
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        }

        day.isFuture -> {
            backgroundColor = Color.Transparent
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        }

        else -> {
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
        }
    }

    Box(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(backgroundColor)
                .then(
                    if (border != null) {
                        Modifier.border(border = border, shape = CircleShape)
                    } else {
                        Modifier
                    }
                )
                .then(
                    if (isInteractive) {
                        Modifier.clickable { onSelectDay(day.date) }
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (isWorkoutDay) {
                Icon(
                    imageVector = Icons.Rounded.FitnessCenter,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp),
                )
            } else {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun StreakIndicator(
    streak: Int,
) {
    Surface(
        shape = CircleShape,
        color = if (streak > 0) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
        } else {
            Color.Transparent
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (streak > 0) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            },
        ),
    ) {
        Box(
            modifier = Modifier.size(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (streak > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocalFireDepartment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = streak.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Rounded.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun ActivityCard(
    recentActivities: List<RecentActivityCardState>,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SectionHeader(
                icon = Icons.Rounded.History,
                title = stringResource(R.string.progress_activity_title),
            )

            if (recentActivities.isEmpty()) {
                Text(
                    text = stringResource(R.string.progress_activity_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
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
        MaterialTheme.colorScheme.primary
    } else {
        RoutineActivityGreen
    }
    val icon = if (activity.type == RecentActivityType.Classification) {
        Icons.Rounded.LocalOffer
    } else {
        Icons.Rounded.FitnessCenter
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = activity.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = activity.dateTimeText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun BadgesCard(
    badges: List<ProgressBadgeState>,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SectionHeader(
                icon = Icons.Rounded.MilitaryTech,
                title = stringResource(R.string.progress_badges_title),
            )

            badges.chunked(2).forEach { rowBadges ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
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
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val containerColor = if (badge.unlocked) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = BorderStroke(
            width = 1.dp,
            color = if (badge.unlocked) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
            },
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = stringResource(badge.id.titleRes()),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(badge.id.subtitleRes()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = day.format(dayFormatter),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
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
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
    }
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
