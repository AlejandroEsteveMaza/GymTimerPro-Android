# GymTimerPro Android Port: Training Module Spec

## Scope

This module covers the main Training tab and its immediate dependencies:

- session configuration
- active rest timer behavior
- routine application into current session
- free-tier usage limiting
- workout completion recording
- notification and live-state parity requirements

Primary iOS sources:

- `GymTimerPro/ContentView.swift`
- `GymTimerPro/LiveActivityManager.swift`
- `GymTimerPro/DailyUsageLimiter.swift`
- `Shared/GymTimerLiveActivityAttributes.swift`
- `GymTimerPro/Routines/RoutineSelectionStore.swift`

## User-Facing Structure

The Training screen has two main cards plus a bottom action area:

1. Configuration section
2. Progress section
3. Bottom primary button to start rest

### Configuration Section

Contains:

- routine selector row, premium only
- total sets control
- rest time control
- free-tier pro upsell row when user is not premium

The whole configuration section becomes disabled and visually dimmed while:

- the timer is active
- or the workout is marked completed

### Progress Section

Shows either:

- live progress state, or
- workout completed state

Live state includes:

- current set over total sets
- reps metric if a routine is applied and user is premium
- current state badge: training or resting
- large countdown view while resting

### Bottom Action

Single primary button:

- label: start rest
- disabled while already resting
- disabled while workout completed

## Training Session State

Persistent user/session state currently stored in iOS preferences:

- `training.total_sets`
- `training.rest_seconds`
- `training.current_set`
- `training.completed`
- `training.applied_routine_name`
- `training.applied_routine_reps`
- `training.max_sets`
- `training.rest_increment`
- `timer.display_format`
- `energy_saving.mode`

Android requirement:

- persist all of these in DataStore so the session survives process death and app restarts

## Settings Affecting Training

### Max Sets Preference

Defined values:

- 10
- 15
- 20
- 30

Behavior:

- total sets cannot exceed selected maximum
- if max is lowered below current total sets, clamp current total sets
- if current set is above clamped total sets, clamp current set too

### Rest Increment Preference

Defined values:

- 5 seconds
- 10 seconds
- 15 seconds

Behavior:

- rest time control must step using the chosen interval
- allowed range remains `15...300` seconds

### Timer Display Format

Defined values:

- raw seconds
- minutes and seconds

Behavior:

- affects visual countdown display
- affects accessibility-formatted rest value

### Power Saving Mode

Defined values:

- off
- automatic
- on

Behavior:

- `off`: energy-saving behavior disabled
- `automatic`: follow system low-power state
- `on`: always use energy-saving behavior

Current iOS effects:

- disables some extra feedback like haptics and animation smoothness
- changes timer loop tolerance
- allows screen idle behavior to return to system default

Android equivalent intent:

- preserve the semantic meaning, not the exact Apple implementation
- while active, reduce non-essential animation or wake behavior when energy saving is enabled

## Timer Engine Contract

The timer is modeled from `endDate`, not from a decremented integer.

State:

- `isRunning`
- `remaining`
- `endDate`
- `didFinish`

Rules:

- `start(duration)` sets `remaining`, computes `endDate`, marks running
- displayed remaining time is derived from `endDate - now`
- remaining seconds are rounded up
- timer can be paused and resumed
- timer resets to zero on completion or manual reset
- state is persisted
- on app lifecycle changes, timer is reconciled against current time

This is critical: Android must keep the same time model. Do not implement a naive decrement-only counter.

## Start Rest Behavior

When user taps Start Rest:

1. If already resting or workout completed, do nothing.
2. If `currentSet >= totalSets`, complete workout instead.
3. Check daily free usage limit unless user is premium.
4. If limit reached:
   - do not consume usage
   - present paywall for daily-limit entry point
5. If allowed:
   - consume one usage for free users
   - increment current set with animation in iOS, equivalent state transition in Android
   - start rest timer with current rest duration
   - update live state / notification representation

Important detail:

- the app increments the set before rest starts
- the visible progress during rest therefore shows the next set index

## Workout Completion Behavior

If the user presses Start Rest while already on the last set:

- do not start another countdown
- mark workout as completed
- record a `WorkoutCompletion`
- show completed state
- after about 2 seconds, auto-reset workout if still completed

Recorded completion fields:

- `completedAt = now`
- `routineID = selected routine id or null`
- `routineNameSnapshot = selected routine name or localized quick workout label`
- `classificationID = selected routine primary classification id or null`
- `classificationNameSnapshot = selected routine primary classification name or null`
- `durationSeconds = null`
- `notes = null`

## Reset Behavior

Reset action is available when:

- workout completed
- timer running
- or current set > 1

Reset behavior:

- stop timer
- clear live state and scheduled notification
- set current set back to 1
- set completed to false

Reset does not clear:

- total sets
- rest duration
- applied routine name/reps

## Routine Application Behavior

Applying a routine from the routine picker:

- if selection is null, clear applied routine display data only
- if a routine is selected:
  - cap routine total sets by global max-sets preference
  - compare against current applied routine and session config
  - if already applied with same effective values, do nothing
  - otherwise:
    - reset workout
    - overwrite total sets
    - overwrite rest duration
    - persist applied routine name
    - persist applied routine reps

Derived routine metadata:

- primary classification is the alphabetically first classification

## Free Tier Usage Limiter

Current rule:

- free users may consume `16` training starts per local day
- premium users are unlimited

Consumption model:

- one unit consumed per successful Start Rest action
- no consumption if workout is completed instead of starting rest
- no consumption if paywall is shown due to reaching limit

Storage:

- stored day start
- stored consumed count

Reset rule:

- reset counter when local start-of-day changes

Android implementation:

- store both values in DataStore
- use device-local calendar day start

## Notifications and Live State

Current iOS behavior:

- requests notification permission when needed
- if live activities are supported, starts or updates one
- otherwise schedules end-of-rest local notification
- always cancels pending end notification on finish or reset

Android parity intent:

- exact API match is not required
- user must still get clear ongoing rest visibility and end-of-rest feedback

Recommended Android behavior:

- active rest can be represented by an ongoing notification
- end-of-rest should produce a normal user-visible notification if app is not foregrounded
- any pending timer completion notification must be cancelled on reset or finish acknowledgement

## Feedback on Rest Finish

Current iOS behavior on rest end:

- clear timer finished flag
- end live activity
- cancel pending end notification
- play sound
- if not in energy-saving mode, also trigger success haptic

Android parity intent:

- play clear end feedback
- keep haptics conditional if battery-saving mode is active

## UI Test and Debug Hooks

Current iOS code includes debug and UI test launch arguments and environment overrides.

These are not product behavior and should not be part of Android product parity.

They may be recreated later for instrumentation testing, but they are out of scope for functional parity.

## Edge Cases

Android implementation must preserve these cases:

- if stored current set is above total sets, clamp it
- if total sets becomes lower than 1, clamp to 1
- if timer is restored and end date is already in the past, finish immediately
- if routine total sets exceeds selected max sets, cap it on apply
- if app returns to foreground after timer ended, finish flow must trigger immediately

## Android Data Model Required by This Module

No new Room entity is required to run the timer itself.

Required dependencies:

- DataStore keys for session and preferences
- Room insertion of `WorkoutCompletion`
- access to selected routine snapshot data
- billing state `isPro`

## Acceptance Checklist

The Android Training module is correct only if:

- it preserves current session after app restart
- the timer remains accurate across lifecycle changes
- free users hit the daily limit at the same logical moment as iOS
- starting rest on last set completes the workout instead of starting a countdown
- applying a routine resets the workout and overwrites total sets and rest duration
- total sets and rest duration obey the same preference-driven constraints
- completion records contain equivalent snapshots to iOS
