package com.alejandroestevemaza.gymtimerpro.core.model

import java.time.LocalDate

enum class ProgressPeriod {
    Week,
    Fortnight,
    Month,
    Quarter,
    Year,
}

data class ProgressBucket(
    val id: String,
    val startDate: LocalDate,
    val label: String,
    val count: Int,
)

data class PeriodSummary(
    val period: ProgressPeriod,
    val totalWorkouts: Int,
    val activeDays: Int,
    val mostRepeatedRoutineName: String?,
    val topClassificationName: String?,
    val buckets: List<ProgressBucket>,
)

data class CalendarDayState(
    val date: LocalDate,
    val inCurrentMonth: Boolean,
    val workoutCount: Int,
    val isToday: Boolean,
    val isFuture: Boolean,
)

data class CalendarWeekState(
    val days: List<CalendarDayState>,
    val showStreakIndicator: Boolean,
)

enum class RecentActivityType {
    Classification,
    Routine,
}

data class RecentActivityCardState(
    val id: String,
    val type: RecentActivityType,
    val title: String,
    val dateTimeText: String,
)

enum class ProgressBadgeId {
    FirstWorkout,
    Workouts5,
    Workouts10,
    Workouts25,
    ThreeWeek,
    Streak4,
}

data class ProgressBadgeState(
    val id: ProgressBadgeId,
    val unlocked: Boolean,
)

data class DayCompletionState(
    val id: String,
    val routineName: String,
    val completionTimeText: String,
)

data class ProgressDerivedState(
    val activeWeeklyStreak: Int,
    val monthStart: LocalDate,
    val calendarWeeks: List<CalendarWeekState>,
    val monthlyDayCounts: Map<LocalDate, Int>,
    val dayCompletions: Map<LocalDate, List<WorkoutCompletion>>,
    val recentActivities: List<RecentActivityCardState>,
    val badges: List<ProgressBadgeState>,
    val periodSummary: PeriodSummary,
)
