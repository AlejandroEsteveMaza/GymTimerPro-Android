# GymTimerPro Android Port: Routines Module Spec

## Scope

This module covers:

- routines list
- routine creation and editing
- routine deletion
- routine application to the training module
- routine picker used from Training
- routine classifications
- grouped search and disclosure behavior

Primary iOS sources:

- `GymTimerPro/Routines/RoutinesRootView.swift`
- `GymTimerPro/Routines/RoutinesListView.swift`
- `GymTimerPro/Routines/RoutinesStore.swift`
- `GymTimerPro/Routines/RoutineEditorView.swift`
- `GymTimerPro/Routines/RoutinePickerView.swift`
- `GymTimerPro/Routines/RoutineCatalogListView.swift`
- `GymTimerPro/Routines/RoutineRowView.swift`
- `GymTimerPro/Routines/RoutineFormatting.swift`
- `GymTimerPro/Routines/Classifications/RoutineClassification.swift`
- `GymTimerPro/Routines/Classifications/RoutineClassificationManagerView.swift`
- `GymTimerPro/Routines/RoutineSelectionStore.swift`

## Product Role

Routines is a premium-only area in the current app.

In free mode:

- the tab is visible
- the content is visually blurred and locked by an overlay
- the user is pushed to the paywall

In premium mode:

- users can create routines
- edit and delete routines
- manage classifications
- apply a routine to Training

## Data Model

### Routine

Structured fields:

- unique `id: UUID`
- `name: String`
- `totalSets: Int`
- `reps: Int`
- `restSeconds: Int`
- `weightKg: Double?`
- `classifications: [RoutineClassification]`
- `createdAt: Date`
- `updatedAt: Date`

Important:

- stored weight is always normalized in kilograms
- displayed weight unit depends on the global weight preference

### RoutineClassification

Structured fields:

- unique `id: UUID`
- `name: String`
- `normalizedName: String`
- relation to many routines

Normalization rule:

- trim whitespace
- lowercase

The normalized value is used to prevent duplicates.

## Persistence Target on Android

Use Room entities for:

- `Routine`
- `RoutineClassification`
- routine-classification join relation

Requirements:

- sort routines by name ascending
- sort classifications by name ascending
- preserve many-to-many relation semantics
- deleting a classification must remove its association from routines, not delete routines

## Routines Root Behavior

The root view owns a store object that is configured once with persistence access.

Android equivalent:

- a dedicated feature-level repository and state holder should initialize once per routines graph or screen scope
- avoid duplicate initialization

## List Screen Behavior

The routines list screen has two possible states:

1. Empty state when there are no routines and no classifications
2. Catalog list grouped by classification plus unclassified section

Toolbar actions:

- overflow menu with classification manager entry
- add routine action

Per-row actions:

- open editor
- swipe/apply action
- swipe/delete action

## Empty State

Shown only when:

- routines are empty
- and classifications are empty

This matters because classifications can exist without routines.

## Catalog Grouping Behavior

The catalog list groups routines by classification.

Rules:

- a routine may appear in multiple classification groups if it has multiple classifications
- routines with no classifications appear in an "unclassified" section
- unclassified placement is configurable
- in current routines list and picker, unclassified is placed at the bottom

Disclosure behavior:

- outside search mode, only one classification section can be expanded at a time
- in search mode, all matching classification groups auto-expand

## Search Behavior

Search applies to both:

- routine names
- classification names

Current iOS implementation:

- uses persistence-layer queries with `localizedStandardContains`
- returns matching routines and matching classifications separately

Rendering rules during search:

- if a classification matches, show that classification section and all routines under it
- if a routine matches but is already shown under a matched classification, do not duplicate it in the standalone routines search section
- unmatched classification sections are not shown in search mode

Android parity:

- preserve grouped search semantics, not just a flat filtered list

## Routine Row Summary

Each routine row shows:

- routine name
- summary text with sets, reps, rest and weight

Summary formatting depends on:

- timer display format preference
- weight unit preference

This means the routines module depends on global settings when formatting labels.

## Routine Editor

The editor supports both:

- create
- edit

Fields:

- name
- classifications
- total sets
- reps
- rest time
- weight

Extra actions in edit mode:

- apply/remove from training
- delete routine

Toolbar actions:

- cancel
- save

## Default Draft Values

When creating a new routine:

- name: empty
- total sets: 4
- reps: 10
- rest seconds: 90
- weight: nil
- classifications: empty

## Routine Field Rules

### Name

Rules:

- max length: 50 characters
- whitespace-trimmed when saving
- cannot save if trimmed value is empty

Current behavior:

- text is clamped live to max length
- counter is shown as `current/max`

### Total Sets

Rules:

- minimum 1
- maximum comes from global max-sets preference

If the global max-sets preference changes while editor is open:

- clamp current draft total sets immediately

### Reps

Rules:

- allowed range `1...30`

### Rest Seconds

Rules:

- allowed range `15...300`
- UI stepping follows the global rest-increment preference

### Weight

Rules:

- optional
- displayed in the selected weight unit
- stored in kilograms
- valid numeric range `0...999`
- up to 2 decimal digits
- if integer part exceeds 3 digits, fraction is dropped
- input is sanitized live

Invalid or empty behavior:

- empty is valid and means no weight
- unparsable value disables save/apply

## Save Behavior

Save is allowed only when:

- trimmed name is not empty
- name length is within limit
- total sets is valid
- reps is valid
- rest seconds is valid
- weight is valid

On save:

- create mode inserts new routine
- edit mode updates existing routine

On update:

- `updatedAt` is refreshed
- classifications relation is synchronized

## Apply to Training Behavior From Editor

Only available in edit mode.

Button behavior:

- if the routine is already applied, pressing the action removes it from training
- if not applied:
  - if there are unsaved changes, the routine is updated first
  - then the routine is applied to training
  - editor dismisses

Important:

- apply action is disabled when there are invalid unsaved changes
- apply action is allowed when there are no changes, or when there are valid savable changes

## Delete Behavior

Delete is available only in edit mode.

Rules:

- requires confirmation
- if the routine is currently applied to training, clear current training selection first
- then delete routine
- dismiss editor

Deleting a routine must not delete classifications.

## Routine Selection / Application Contract

When a routine is applied to training, the selection snapshot contains:

- routine id
- name
- total sets
- reps
- rest seconds
- primary classification id
- primary classification name

Primary classification rule:

- sort classifications by localized case-insensitive name ascending
- use the first one

Android must preserve this exact rule because Training records classification snapshot from the selected routine.

## Routine Picker Behavior

The picker is opened from Training and is only meaningful for premium users.

Screen behavior:

- same grouped catalog behavior as the main routines screen
- tapping a routine applies it and closes the picker
- if a routine is already applied, show a top section action to remove it from training and close

Toolbar:

- cancel action closes picker

## Classification Management

There are two modes:

1. `manage`
2. `select`

### Manage Mode

Capabilities:

- list classifications
- search classifications
- create classification inline
- rename classification inline
- delete classification

Rules:

- duplicate names are forbidden by `normalizedName`
- create and rename both trim whitespace
- empty names are invalid
- deleting a classification removes it from every routine first, then deletes classification

Inline editing behavior:

- only one edit/create interaction is active at a time
- starting create cancels rename
- starting rename cancels create
- losing focus cancels the current inline interaction

### Select Mode

Capabilities:

- tap to toggle selection
- selected classifications show a checkmark state

No delete or rename UI in select mode.

## Classification Search/Create Input Behavior

In the standalone picker-style classification flow, the bottom input bar:

- filters existing classifications while user types
- if trimmed text does not already exist, allows creation
- creating a new classification immediately selects it

Duplicate handling:

- if the normalized name already exists, show duplicate error and do not create

## Relation Synchronization Rules

When saving a routine:

- newly assigned classifications must contain the routine in their routine list
- removed classifications must remove the routine from their routine list

Android equivalent:

- maintain the join table accurately
- do not leave stale links after update or delete

## Formatting Rules

### Weight

Displayed with:

- locale-aware decimal formatting
- short unit formatting
- unit resolved by weight preference:
  - automatic
  - kilograms
  - pounds

### Rest in Summary

Uses global timer display format:

- seconds
- minutes:seconds

This dependency must be preserved in Android summary rendering.

## Debug Seed Data

The iOS store includes debug-only sample routines seeding via launch arguments.

This is not product behavior and should not be part of Android production parity.

It can be recreated later for dev builds if useful, but it is out of scope for the Android product spec.

## Android Implementation Notes

- Use Room plus a join table for many-to-many relation.
- Keep formatting logic isolated in a shared formatter layer so routines list, picker and editor all render consistently.
- Keep classification management as part of the routines feature, even if later split into subpackages.
- Reuse the same catalog grouping/search behavior in both the main routines screen and the training picker.

## Acceptance Checklist

The Android routines module is correct only if:

- creating and editing routines preserves all field rules
- invalid names or weights block save/apply
- grouped catalog search behaves like iOS
- routines can belong to multiple classifications
- unclassified routines are rendered separately
- applying or removing a routine updates training selection consistently
- deleting a classification removes only the classification relation, not routines
- summary formatting reflects current weight unit and timer display settings
