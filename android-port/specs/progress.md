# GymTimerPro Android Port: Progress Module Spec

## Scope

This module covers:

- progress tab
- chart summaries by period
- monthly calendar view
- recent activity cards
- badge unlocking
- day-detail drilldown

Primary iOS sources:

- `GymTimerPro/Progress/ProgramProgressView.swift`
- `GymTimerPro/Progress/ProgramProgressStore.swift`
- `GymTimerPro/Progress/Models/WorkoutCompletion.swift`

## Product Role

Progress is a premium-only area in the current app.

In free mode:

- the tab is visible
- the content is blurred and blocked by the premium lock overlay from the main tab shell

In premium mode:

- users can inspect workout history summaries
- view charts for different time periods
- inspect a month calendar with workout markers
- open day detail when a day contains workouts
- see recent activity cards
- unlock badges based on historical usage

## Source Data

The module reads only from `WorkoutCompletion` records.

Relevant fields:

- `id`
- `completedAt`
- `routineID`
- `routineNameSnapshot`
- `classificationID`
- `classificationNameSnapshot`
- `durationSeconds`

Important:

- the progress module works from immutable snapshots, not live routine references
- if `routineNameSnapshot` is empty, it falls back to the localized quick-workout label

Android equivalent:

- use stored workout-completion snapshots exactly the same way
- do not join live routine names into historical progress rows

## View Structure

The progress screen is a scrollable page composed of four cards:

1. charts section
2. calendar section
3. activity section
4. badges section

There is also a modal day-detail sheet when the user taps a workout day in the calendar.

## Period Selection

Available periods:

- week
- fortnight
- month
- quarter
- year

The selected period controls:

- chart interval
- bucket granularity
- x-axis labels
- summary stats
- top routine
- top classification

## Core Derived Data

The store derives and exposes:

- `activeWeeklyStreak`
- `monthStart`
- `monthlyDayCounts`
- `dayCompletions`
- `recentCompletions`
- `badges`
- `periodSummaries`

Android should mirror this structure conceptually, even if represented through different classes.

## Locale and Calendar Rules

This is a critical parity area.

The current iOS implementation uses `Calendar.autoupdatingCurrent`.

That means:

- week start depends on device locale
- weekday symbols rotate according to `firstWeekday`
- weekly streaks use locale-aware `weekOfYear`
- calendar month leading cells depend on locale week start

Android requirement:

- use locale-aware week definitions
- do not hardcode Monday or Sunday
- the Android calendar grid and week streak logic must follow the user locale, not a fixed convention

## Chart Summary Logic

Each period produces:

- total workouts
- active days
- most repeated routine name
- top classification name
- chart buckets

### Most Repeated Routine

Computed from filtered completions in the period.

Tie-breaker:

- if counts are equal, sort by localized case-insensitive ascending name

### Top Classification

Computed only from non-empty classification names.

Tie-breaker:

- if counts are equal, sort by localized case-insensitive ascending name

## Period Intervals

### Week

Interval:

- from `todayStart - 7 days`
- to `tomorrow`

Bucket granularity:

- daily

### Fortnight

Interval:

- from `todayStart - 14 days`
- to `tomorrow`

Bucket granularity:

- daily

### Month

Interval:

- current calendar month only

Bucket granularity:

- weekly buckets using locale-aware `weekOfYear`

### Quarter

Interval:

- 12 weeks ending at the end of the current week window
- start = current week start minus 11 weeks
- end = one week after current week start

Bucket granularity:

- weekly, fixed 12 buckets

### Year

Interval:

- last 1 year up to tomorrow

Bucket granularity:

- monthly

## Bucket Start Rules

Bucket starts depend on period:

- week, fortnight: day start
- month, quarter: week start
- year: month start

This must remain exact in Android because charts and counts depend on this alignment.

## Empty Summary Behavior

If a period has no completions:

- workouts = 0
- activeDays = 0
- mostRepeatedRoutineName = null
- topClassificationName = null
- chart still shows empty buckets for the period

Android should not collapse the chart into “no data” if the iOS version still displays zero buckets.

## X-Axis Label Rules

### Week / Fortnight

- label = day of month

### Month

- label = `W` + week number

### Quarter

- label = abbreviated month label
- axis values prefer the current month and the two prior months when in range
- otherwise fall back to month starts derived from bucket dates

### Year

- label = abbreviated month label

## Calendar Card

The calendar card shows the current month only.

Inputs:

- `monthStart`
- `monthlyDayCounts`
- `activeWeeklyStreak`

### Grid Construction

Rules:

- grid is based on the current month
- include leading days from the previous month to align the first row
- row count is at least 5
- total cells are padded to full weeks

Current month cells:

- show day number when there is no workout
- show dumbbell icon when there is at least one workout

Non-current-month cells:

- show muted day numbers only

## Day Interaction

Only days that satisfy both conditions are tappable:

- belongs to the current month
- has at least one workout

Tapped day opens a detail modal showing:

- routine name
- completion time

Android parity:

- keep this drilldown behavior
- do not open empty days

## Day Cell Styling Semantics

### Workout day

- filled blue circle
- white dumbbell icon

### Today with no workout

- clear fill
- orange border

### Past day with no workout

- light gray fill
- darker gray border

### Future day with no workout

- clear fill
- light gray border

The exact colors can be adapted to Android theming, but the semantic distinctions must stay the same.

## Weekly Streak Indicator

Each week row has an extra column at the end.

Behavior:

- only the current week row can show the active streak value
- if current week streak is greater than 0, show flame icon plus streak count
- otherwise show an empty outlined circle

Streak definition:

- count consecutive weeks, starting from the current week and moving backward
- a week counts if it has at least 1 workout
- stop at the first week that does not meet the goal

Current goal:

- 1 workout per week

## Recent Activity Section

The activity card section is not a full activity feed.

It builds at most two curated cards:

1. latest completion that has a non-empty classification
2. latest completion overall, shown as routine activity

Rules:

- maximum two cards
- if only one card exists, the layout still visually fills the row

Card content:

- icon
- title
- date/time

Current icon semantics:

- classification card: orange tag icon
- routine card: green strength-training icon

## Badges

Current badges:

- first workout
- 5 workouts
- 10 workouts
- 25 workouts
- at least 3 workouts in any single week
- 4-week streak

Unlock rules:

- `first_workout`: total completions >= 1
- `workouts_5`: total completions >= 5
- `workouts_10`: total completions >= 10
- `workouts_25`: total completions >= 25
- `three_week`: any week has at least 3 workouts
- `streak_4`: active weekly streak >= 4

Badge cards show:

- medal icon when unlocked
- lock icon when locked
- localized title and subtitle

## Recompute / Reload Rules

The current iOS store computes a signature from:

- total count
- first completion id and timestamp
- last completion id and timestamp

If the signature does not change:

- skip recomputation

Android equivalent:

- use an efficient invalidation strategy
- avoid unnecessary recomputation for unchanged datasets

The exact signature implementation can differ, but the optimization intent should stay.

## Concurrency / Computation Model

The current iOS code:

- maps persistence entities to immutable snapshots
- computes derived progress data off the main actor
- then publishes the result

Android recommendation:

- map DB models to immutable domain snapshots
- compute summaries off the main thread
- expose state through a `ViewModel`

## Empty and Fallback Behavior

If there are no completions:

- charts still render zero buckets
- activity section shows empty-state message
- badges remain locked
- day-detail modal never opens

## Android Implementation Notes

- Use locale-aware week logic from Java Time / ICU APIs, not hardcoded assumptions.
- Keep chart bucket logic separate from UI code.
- Keep calendar grid generation deterministic and testable.
- The progress feature should depend only on workout-completion snapshots and not on live routine entities.

## Acceptance Checklist

The Android progress module is correct only if:

- locale-dependent week start is respected everywhere
- chart summaries match iOS period semantics
- empty periods still generate empty buckets
- monthly calendar shows workout markers and only opens valid days
- weekly streak matches the current-week backward logic
- curated recent activity reproduces the same two-card selection rules
- badge unlocking matches the current thresholds exactly
