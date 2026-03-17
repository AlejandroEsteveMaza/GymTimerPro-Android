# GymTimerPro Android Port: Settings Module Spec

## Scope

This module covers:

- settings tab
- all global user preferences exposed by the app
- the navigation entry to classification management
- the cross-module effects of these preferences

Primary iOS sources:

- `GymTimerPro/Settings/SettingsRootView.swift`
- `Shared/GymTimerLiveActivityAttributes.swift`
- `GymTimerPro/ContentView.swift`
- `GymTimerPro/Routines/RoutineEditorView.swift`
- `GymTimerPro/Routines/RoutineFormatting.swift`

## Product Role

Settings is a premium-only area in the current app.

In free mode:

- the tab is visible
- the content is blurred and blocked by the premium overlay from the main tab shell

In premium mode:

- users can modify global preferences that affect training, routines formatting and energy behavior
- users can open classification management from here

## Preferences Exposed in Settings

The app currently exposes five global preferences:

1. weight unit
2. timer display format
3. max sets
4. rest increment
5. energy-saving mode

These are not screen-local values. They are app-wide behavior controls.

Android requirement:

- persist them in DataStore
- expose them as reactive state to any module that depends on them

## Preference Keys

Current keys in iOS:

- `weight.unit_preference`
- `timer.display_format`
- `training.max_sets`
- `training.rest_increment`
- `energy_saving.mode`

Android should preserve equivalent semantic keys even if the storage implementation differs.

## Settings Screen Structure

The settings screen is a grouped list with sections in this order:

1. weight unit
2. timer display
3. max sets
4. rest increment
5. energy-saving mode
6. classifications management entry

Navigation title:

- settings tab title

## Weight Unit Preference

Enum values:

- automatic = `0`
- kilograms = `1`
- pounds = `2`

Behavior:

- affects how routine weights are displayed and edited
- stored routine weights remain normalized in kilograms

### Automatic Resolution Rule

When set to automatic:

- if locale measurement system is US or UK, use pounds
- otherwise use kilograms

Android parity:

- preserve this exact semantic rule
- do not simplify automatic mode into “follow device language” without checking the measurement system

## Timer Display Format Preference

Enum values:

- seconds = `0`
- minutes and seconds = `1`

Behavior:

- affects countdown display in Training
- affects accessibility strings for rest time
- affects routine rest summary formatting
- affects routine editor rest display formatting

Formatting rules:

- `seconds` -> plain integer seconds
- `minutesAndSeconds` -> `m:ss`

The formatter clamps negative input to zero before formatting.

## Max Sets Preference

Enum values:

- 10
- 15
- 20
- 30

Behavior:

- defines the upper bound for total sets in Training
- defines the upper bound for total sets in Routine Editor
- clamps already-stored or already-edited values when lowered
- caps applied routine total sets when a routine is pushed into Training

This is a control preference, not just a picker label.

Android requirement:

- changing it must immediately affect dependent UI state, not only future sessions

## Rest Increment Preference

Enum values:

- 5 seconds
- 10 seconds
- 15 seconds

Behavior:

- defines the stepping interval for rest time controls in Training
- defines the stepping interval for rest time controls in Routine Editor

Allowed rest range remains:

- minimum 15
- maximum 300

Only the step changes, not the valid bounds.

## Energy-Saving Mode Preference

Enum values:

- off = `0`
- automatic = `1`
- on = `2`

Resolution function:

- `off` -> disabled
- `automatic` -> enabled only when system low-power mode is enabled
- `on` -> enabled always

Current iOS effects:

- affects timer loop tolerance
- affects whether extra haptic feedback is played on rest completion
- affects idle-timer behavior

Android parity intent:

- preserve the user-facing meaning of the mode
- when enabled, reduce optional feedback or energy-heavy behavior
- exact Android implementation may differ, but the semantic state machine must remain the same

## Cross-Module Impact Matrix

### Weight Unit

Impacts:

- routine weight input text
- routine weight summary text
- routine list row summaries

Does not change stored weight unit in persistence.

### Timer Display Format

Impacts:

- active rest timer display
- rest accessibility text
- routine editor rest formatting
- routine summary formatting
- preview/live-state formatting where applicable

### Max Sets

Impacts:

- training total-sets control range
- routine editor total-sets control range
- applied routine capping in training

### Rest Increment

Impacts:

- training rest-time control step
- routine editor rest-time control step

### Energy-Saving Mode

Impacts:

- training timer behavior
- finish feedback behavior
- power-related runtime decisions

## UI Control Types in Current iOS App

Current picker styles:

- weight unit: menu
- timer display: segmented
- max sets: menu
- rest increment: segmented
- energy-saving mode: menu

Android does not need identical widgets, but should preserve:

- same choices
- same labels and meaning
- same update immediacy

Reasonable Android mappings:

- dropdown/menu or exposed dropdown for menu-like choices
- segmented/toggle row for short binary/ternary option groups where it still reads clearly

## Classifications Management Entry

The settings screen includes a navigation link to classification management.

Important:

- classification management is part of the routines domain
- settings only provides an entry point

Android equivalent:

- include a settings row that navigates into the shared classifications management screen
- do not duplicate a second independent classifications implementation

## Persistence and Default Values

Default values in iOS:

- weight unit: automatic
- timer display: seconds
- max sets: 10
- rest increment: 15
- energy-saving mode: off

Android should use the same defaults.

If persisted values are invalid or missing:

- fall back to the same defaults

## Implementation Constraints

- settings state must be shareable across modules
- formatting helpers must consume settings state consistently
- avoid duplicating preference-parsing logic in each screen

Recommended Android shape:

- a single settings repository backed by DataStore
- typed domain enums for each preference
- shared formatter helpers depending on these enums

## Non-Negotiable Parity Rules

- weight automatic mode must use measurement-system logic
- timer display format must affect both countdown and routine summaries
- max sets must actively clamp dependent values
- rest increment must change stepping, not valid range
- energy mode must preserve off/automatic/on semantics

## Acceptance Checklist

The Android settings module is correct only if:

- all five preferences persist and restore correctly
- defaults match iOS
- changing a setting immediately affects dependent modules
- routine formatting reflects weight-unit and timer-format settings
- training and routine editor honor max-sets and rest-increment settings
- classification management is reachable from settings without duplicating logic
