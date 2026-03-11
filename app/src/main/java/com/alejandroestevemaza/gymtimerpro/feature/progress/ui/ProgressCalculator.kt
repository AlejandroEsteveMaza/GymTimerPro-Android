package com.alejandroestevemaza.gymtimerpro.feature.progress.ui

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
import com.alejandroestevemaza.gymtimerpro.core.model.WorkoutCompletion
import java.text.Collator
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

class ProgressCalculator(
    private val clock: Clock,
    private val locale: Locale,
    private val zoneId: ZoneId,
    private val quickWorkoutLabel: String,
) {
    private val weekFields = WeekFields.of(locale)
    private val collator = Collator.getInstance(locale).apply { strength = Collator.PRIMARY }
    private val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)
    private val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
        .withLocale(locale)

    fun calculate(
        completions: List<WorkoutCompletion>,
        selectedPeriod: ProgressPeriod,
    ): ProgressDerivedState {
        val today = LocalDate.now(clock)
        val normalizedCompletions = completions
            .sortedByDescending { it.completedAtEpochMillis }

        val dayCompletions = normalizedCompletions.groupBy { completion ->
            completion.completedAtEpochMillis.toLocalDate()
        }

        val monthStart = today.withDayOfMonth(1)
        val monthlyDayCounts = dayCompletions
            .filterKeys { date -> date.year == monthStart.year && date.month == monthStart.month }
            .mapValues { (_, completionsForDay) -> completionsForDay.size }

        val activeWeeklyStreak = computeActiveWeeklyStreak(dayCompletions, today)
        val periodSummary = calculatePeriodSummary(
            completions = normalizedCompletions,
            selectedPeriod = selectedPeriod,
            today = today,
        )
        val recentActivities = calculateRecentActivities(normalizedCompletions)
        val badges = calculateBadges(normalizedCompletions, dayCompletions, activeWeeklyStreak)
        val calendarWeeks = buildCalendarWeeks(
            monthStart = monthStart,
            monthlyDayCounts = monthlyDayCounts,
            today = today,
        )

        return ProgressDerivedState(
            activeWeeklyStreak = activeWeeklyStreak,
            monthStart = monthStart,
            calendarWeeks = calendarWeeks,
            monthlyDayCounts = monthlyDayCounts,
            dayCompletions = dayCompletions,
            recentActivities = recentActivities,
            badges = badges,
            periodSummary = periodSummary,
        )
    }

    fun dayCompletionStates(
        date: LocalDate,
        completions: Map<LocalDate, List<WorkoutCompletion>>,
    ): List<DayCompletionState> = completions[date].orEmpty()
        .sortedByDescending { it.completedAtEpochMillis }
        .map { completion ->
            DayCompletionState(
                id = completion.id,
                routineName = completion.displayRoutineName(),
                completionTimeText = completion.completedAtEpochMillis
                    .toLocalDateTime()
                    .format(timeFormatter),
            )
        }

    private fun calculatePeriodSummary(
        completions: List<WorkoutCompletion>,
        selectedPeriod: ProgressPeriod,
        today: LocalDate,
    ): PeriodSummary {
        val range = selectedPeriod.toRange(today)
        val filteredCompletions = completions.filter { completion ->
            val completionInstant = Instant.ofEpochMilli(completion.completedAtEpochMillis)
            !completionInstant.isBefore(range.startInclusive) && completionInstant.isBefore(range.endExclusive)
        }
        val activeDays = filteredCompletions
            .map { completion -> completion.completedAtEpochMillis.toLocalDate() }
            .toSet()
            .size
        val buckets = buildBuckets(
            period = selectedPeriod,
            filteredCompletions = filteredCompletions,
            range = range,
            today = today,
        )

        return PeriodSummary(
            period = selectedPeriod,
            totalWorkouts = filteredCompletions.size,
            activeDays = activeDays,
            mostRepeatedRoutineName = topRoutineName(filteredCompletions),
            topClassificationName = topClassificationName(filteredCompletions),
            buckets = buckets,
        )
    }

    private fun buildBuckets(
        period: ProgressPeriod,
        filteredCompletions: List<WorkoutCompletion>,
        range: DateRange,
        today: LocalDate,
    ): List<ProgressBucket> {
        val bucketStarts = when (period) {
            ProgressPeriod.Week,
            ProgressPeriod.Fortnight -> {
                generateSequence(range.startInclusive.atZone(zoneId).toLocalDate()) { date -> date.plusDays(1) }
                    .takeWhile { date -> date.atStartOfDay(zoneId).toInstant() < range.endExclusive }
                    .toList()
            }

            ProgressPeriod.Month -> {
                val monthStart = today.withDayOfMonth(1)
                val lastDay = monthStart.with(TemporalAdjusters.lastDayOfMonth())
                generateSequence(startOfWeek(monthStart)) { date -> date.plusWeeks(1) }
                    .takeWhile { date -> !date.isAfter(startOfWeek(lastDay)) }
                    .toList()
            }

            ProgressPeriod.Quarter -> {
                val currentWeekStart = startOfWeek(today)
                (11 downTo 0).map { offset -> currentWeekStart.minusWeeks(offset.toLong()) }
            }

            ProgressPeriod.Year -> {
                val startMonth = range.startInclusive.atZone(zoneId).toLocalDate().withDayOfMonth(1)
                val endMonth = range.endExclusive.minusMillis(1).atZone(zoneId).toLocalDate().withDayOfMonth(1)
                generateSequence(startMonth) { date -> date.plusMonths(1) }
                    .takeWhile { date -> !date.isAfter(endMonth) }
                    .toList()
            }
        }

        return bucketStarts.map { bucketStart ->
            val count = filteredCompletions.count { completion ->
                completion.belongsToBucket(period, bucketStart)
            }
            ProgressBucket(
                id = "${period.name}-${bucketStart}",
                startDate = bucketStart,
                label = bucketLabel(period, bucketStart, today),
                count = count,
            )
        }
    }

    private fun bucketLabel(
        period: ProgressPeriod,
        bucketStart: LocalDate,
        today: LocalDate,
    ): String = when (period) {
        ProgressPeriod.Week,
        ProgressPeriod.Fortnight -> bucketStart.dayOfMonth.toString()
        ProgressPeriod.Month -> "W${bucketStart.get(weekFields.weekOfYear())}"
        ProgressPeriod.Quarter -> quarterBucketLabel(bucketStart, today)
        ProgressPeriod.Year -> bucketStart.month.getDisplayName(java.time.format.TextStyle.SHORT, locale)
    }

    private fun quarterBucketLabel(
        bucketStart: LocalDate,
        today: LocalDate,
    ): String {
        val preferredMonths = setOf(
            today.withDayOfMonth(1),
            today.withDayOfMonth(1).minusMonths(1),
            today.withDayOfMonth(1).minusMonths(2),
        )
        val bucketEndExclusive = bucketStart.plusWeeks(1)
        val monthStartsInsideBucket = generateSequence(bucketStart.withDayOfMonth(1)) { date -> date.plusMonths(1) }
            .takeWhile { date -> date < bucketEndExclusive }
            .filter { monthStart -> !monthStart.isBefore(bucketStart) }
            .toList()
        val monthStart = monthStartsInsideBucket.firstOrNull()
            ?: bucketStart.withDayOfMonth(1)
        return if (monthStart in preferredMonths || bucketStart.dayOfMonth <= 7) {
            monthStart.month.getDisplayName(java.time.format.TextStyle.SHORT, locale)
        } else {
            ""
        }
    }

    private fun topRoutineName(filteredCompletions: List<WorkoutCompletion>): String? = filteredCompletions
        .groupingBy { completion -> completion.displayRoutineName() }
        .eachCount()
        .entries
        .sortedWith(
            compareByDescending<Map.Entry<String, Int>> { it.value }
                .thenComparator { left, right -> collator.compare(left.key, right.key) }
        )
        .firstOrNull()
        ?.key

    private fun topClassificationName(filteredCompletions: List<WorkoutCompletion>): String? = filteredCompletions
        .mapNotNull { completion -> completion.classificationNameSnapshot?.takeIf { it.isNotBlank() } }
        .groupingBy { it }
        .eachCount()
        .entries
        .sortedWith(
            compareByDescending<Map.Entry<String, Int>> { it.value }
                .thenComparator { left, right -> collator.compare(left.key, right.key) }
        )
        .firstOrNull()
        ?.key

    private fun calculateRecentActivities(
        completions: List<WorkoutCompletion>,
    ): List<RecentActivityCardState> {
        val latestClassification = completions.firstOrNull { completion ->
            !completion.classificationNameSnapshot.isNullOrBlank()
        }?.let { completion ->
            RecentActivityCardState(
                id = "classification-${completion.id}",
                type = RecentActivityType.Classification,
                title = completion.classificationNameSnapshot.orEmpty(),
                dateTimeText = completion.completedAtEpochMillis.toLocalDateTime().format(dateTimeFormatter),
            )
        }

        val latestRoutine = completions.firstOrNull()?.let { completion ->
            RecentActivityCardState(
                id = "routine-${completion.id}",
                type = RecentActivityType.Routine,
                title = completion.displayRoutineName(),
                dateTimeText = completion.completedAtEpochMillis.toLocalDateTime().format(dateTimeFormatter),
            )
        }

        return listOfNotNull(latestClassification, latestRoutine).take(2)
    }

    private fun calculateBadges(
        completions: List<WorkoutCompletion>,
        dayCompletions: Map<LocalDate, List<WorkoutCompletion>>,
        activeWeeklyStreak: Int,
    ): List<ProgressBadgeState> {
        val totalCount = completions.size
        val weeklyCounts = dayCompletions.entries
            .groupBy { (date, _) -> startOfWeek(date) }
            .mapValues { (_, entries) -> entries.sumOf { (_, dailyCompletions) -> dailyCompletions.size } }

        return listOf(
            ProgressBadgeState(ProgressBadgeId.FirstWorkout, totalCount >= 1),
            ProgressBadgeState(ProgressBadgeId.Workouts5, totalCount >= 5),
            ProgressBadgeState(ProgressBadgeId.Workouts10, totalCount >= 10),
            ProgressBadgeState(ProgressBadgeId.Workouts25, totalCount >= 25),
            ProgressBadgeState(ProgressBadgeId.ThreeWeek, weeklyCounts.values.any { count -> count >= 3 }),
            ProgressBadgeState(ProgressBadgeId.Streak4, activeWeeklyStreak >= 4),
        )
    }

    private fun computeActiveWeeklyStreak(
        dayCompletions: Map<LocalDate, List<WorkoutCompletion>>,
        today: LocalDate,
    ): Int {
        var streak = 0
        var weekStart = startOfWeek(today)
        while (dayCompletions.keys.any { date -> startOfWeek(date) == weekStart }) {
            streak += 1
            weekStart = weekStart.minusWeeks(1)
        }
        return streak
    }

    private fun buildCalendarWeeks(
        monthStart: LocalDate,
        monthlyDayCounts: Map<LocalDate, Int>,
        today: LocalDate,
    ): List<CalendarWeekState> {
        val monthEnd = monthStart.with(TemporalAdjusters.lastDayOfMonth())
        val firstCellDate = startOfWeek(monthStart)
        var lastCellDate = endOfWeek(monthEnd)
        while (daysBetweenInclusive(firstCellDate, lastCellDate) < 35) {
            lastCellDate = lastCellDate.plusWeeks(1)
        }

        val days = generateSequence(firstCellDate) { date -> date.plusDays(1) }
            .takeWhile { date -> !date.isAfter(lastCellDate) }
            .map { date ->
                CalendarDayState(
                    date = date,
                    inCurrentMonth = date.month == monthStart.month,
                    workoutCount = monthlyDayCounts[date] ?: 0,
                    isToday = date == today,
                    isFuture = date.isAfter(today),
                )
            }
            .toList()

        return days.chunked(7).map { weekDays ->
            CalendarWeekState(
                days = weekDays,
                showStreakIndicator = weekDays.any { day -> startOfWeek(day.date) == startOfWeek(today) },
            )
        }
    }

    private fun ProgressPeriod.toRange(today: LocalDate): DateRange = when (this) {
        ProgressPeriod.Week -> {
            val start = today.minusDays(7).atStartOfDay(zoneId).toInstant()
            val end = today.plusDays(1).atStartOfDay(zoneId).toInstant()
            DateRange(start, end)
        }

        ProgressPeriod.Fortnight -> {
            val start = today.minusDays(14).atStartOfDay(zoneId).toInstant()
            val end = today.plusDays(1).atStartOfDay(zoneId).toInstant()
            DateRange(start, end)
        }

        ProgressPeriod.Month -> {
            val startDate = today.withDayOfMonth(1)
            DateRange(
                startInclusive = startDate.atStartOfDay(zoneId).toInstant(),
                endExclusive = startDate.plusMonths(1).atStartOfDay(zoneId).toInstant(),
            )
        }

        ProgressPeriod.Quarter -> {
            val currentWeekStart = startOfWeek(today)
            DateRange(
                startInclusive = currentWeekStart.minusWeeks(11).atStartOfDay(zoneId).toInstant(),
                endExclusive = currentWeekStart.plusWeeks(1).atStartOfDay(zoneId).toInstant(),
            )
        }

        ProgressPeriod.Year -> {
            DateRange(
                startInclusive = today.minusYears(1).atStartOfDay(zoneId).toInstant(),
                endExclusive = today.plusDays(1).atStartOfDay(zoneId).toInstant(),
            )
        }
    }

    private fun WorkoutCompletion.belongsToBucket(
        period: ProgressPeriod,
        bucketStart: LocalDate,
    ): Boolean {
        val completionDate = completedAtEpochMillis.toLocalDate()
        return when (period) {
            ProgressPeriod.Week,
            ProgressPeriod.Fortnight -> completionDate == bucketStart
            ProgressPeriod.Month,
            ProgressPeriod.Quarter -> startOfWeek(completionDate) == bucketStart
            ProgressPeriod.Year -> completionDate.withDayOfMonth(1) == bucketStart
        }
    }

    private fun WorkoutCompletion.displayRoutineName(): String =
        routineNameSnapshot.takeIf { it.isNotBlank() } ?: quickWorkoutLabel

    private fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(zoneId).toLocalDate()

    private fun Long.toLocalDateTime(): LocalDateTime = Instant.ofEpochMilli(this).atZone(zoneId).toLocalDateTime()

    private fun startOfWeek(date: LocalDate): LocalDate =
        date.with(TemporalAdjusters.previousOrSame(weekFields.firstDayOfWeek))

    private fun endOfWeek(date: LocalDate): LocalDate =
        date.with(TemporalAdjusters.nextOrSame(weekFields.firstDayOfWeek.minus(1)))

    private fun daysBetweenInclusive(start: LocalDate, end: LocalDate): Int =
        java.time.temporal.ChronoUnit.DAYS.between(start, end).toInt() + 1

    private data class DateRange(
        val startInclusive: Instant,
        val endExclusive: Instant,
    )
}
