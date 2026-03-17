# GymTimerPro Android Master Prompt

Use this prompt as the single entry point for creating the Android project. It assumes the iOS app already exists and that Android must match its behavior as closely as possible.

## Prompt

You are building the Android version of GymTimerPro.

This is not a redesign, not a simplified MVP, and not a cross-platform rewrite. The iOS app already exists and is the source product reference. Your job is to implement the Android app with strict behavioral parity to the current iOS product while using Android-native architecture and APIs.

Before writing code, read and obey these documents:

- `android-port/specs/master-spec.md`
- `android-port/specs/training.md`
- `android-port/specs/routines.md`
- `android-port/specs/progress.md`
- `android-port/specs/settings.md`
- `android-port/specs/paywall-subscriptions.md`
- `android-port/specs/notifications-widget-parity.md`
- `android-port/specs/localization.md`

## Product Goal

Recreate GymTimerPro on Android so that:

- the app structure matches the iOS product
- free vs premium behavior matches the iOS product
- training logic matches the iOS product
- routine and progress behavior matches the iOS product
- settings semantics match the iOS product
- all currently supported languages are preserved

If Android requires a platform-specific adaptation, keep user-visible behavior as close as possible and document the deviation.

## Required Android Stack

Use:

- Kotlin
- Jetpack Compose
- Navigation Compose
- Room for structured persistence
- DataStore Preferences for app settings and session state
- Google Play Billing for subscriptions
- Android-native notifications for rest completion
- ongoing Android rest-state surface for live timer parity

Do not use:

- Flutter
- React Native
- KMP for this project
- a backend unless explicitly required later

## Architectural Constraints

1. Keep business logic separate from Compose UI.
2. Use typed domain models and feature-level repositories.
3. Use a single premium entitlement source shared across the app.
4. Use a single settings source shared across modules.
5. Persist routine, classification and workout completion data in Room.
6. Persist training session and global preferences in DataStore.
7. Keep date, number and time formatting locale-aware.
8. Do not hardcode week start or measurement rules.

## Module Boundaries

Implement these modules:

1. App shell and navigation
2. Shared settings/preferences layer
3. Premium entitlement and billing layer
4. Training
5. Routines and classifications
6. Progress
7. Notifications/live-rest parity
8. Localization resources

## Functional Contract

Preserve these behaviors exactly:

- Training is free.
- Routines, Progress and Settings are premium-gated.
- Free users have a daily usage limit of 16 successful rest starts.
- Starting rest increments the current set before countdown begins.
- Starting rest on the last set completes the workout instead of starting another countdown.
- Applying a routine resets current training progress and overwrites total sets and rest duration.
- Applied routine total sets are capped by the max-sets preference.
- Progress is computed from workout-completion snapshots, not live routine joins.
- Week start depends on user locale.
- Premium products are yearly and monthly subscriptions.
- Yearly plan is selected by default when available.

## Data Model Requirements

Structured entities:

- Routine
- RoutineClassification
- many-to-many routine/classification relation
- WorkoutCompletion

Preferences/session keys to preserve semantically:

- training total sets
- training rest seconds
- training current set
- training completed flag
- applied routine name
- applied routine reps
- max sets preference
- rest increment preference
- timer display format
- energy saving mode
- cached premium state
- usage limiter counters
- timer persisted state

## UX Requirements

Preserve the current iOS product intent:

- 4 tabs: Training, Routines, Progress, Settings
- locked premium tabs remain visible in free mode
- paywall copy varies by entry point and information level
- routine picker and routines catalog use grouped classification behavior
- progress screen preserves chart, calendar, recent activity and badges

Do not redesign navigation or feature scope unless absolutely necessary for Android usability.

## Notifications and Live Timer Parity

The current iOS product uses local notifications plus Live Activity.

Android parity should be:

- end-of-rest user-visible notification with sound
- one active pending rest-end notification at a time
- ongoing visible rest state outside the app with countdown and set progress

Do not treat a Home Screen widget as mandatory for v1 unless you explicitly decide to add one later.

## Localization Requirements

Support all current locales:

- da
- de
- en
- es
- fr
- hi
- it
- ja
- ko
- nb
- nl
- pt-BR
- pt-PT
- sv
- zh-Hans

Do not postpone localization to the end. Resource wiring should be part of the initial implementation.

## Build Output Requirements

Create a real Android Studio project with:

- Gradle Kotlin DSL
- clear package structure
- compile SDK aligned with current Android stable platform
- target SDK aligned with Play requirements
- a pragmatic minimum SDK for modern timer and notification support

## Implementation Order

Implement in this order:

1. project skeleton, navigation, theme, app entry
2. shared settings and entitlement infrastructure
3. Room schema and repositories
4. training module and timer engine
5. routines and classifications
6. workout completion recording
7. progress module
8. paywall and billing UI
9. notification/live-rest behavior
10. full localization pass

## Coding Standards

- production-grade code only
- no placeholder implementations
- no fake repositories
- no TODOs standing in for core behavior
- no duplicated business logic across screens
- keep logic testable

## Expected Deliverables

Produce:

- Android project structure
- package architecture
- Room entities, DAOs and repositories
- DataStore-based settings/session storage
- billing manager and entitlement model
- Compose screens for all app sections
- navigation setup
- notification/live-rest integration
- localized string resources

## Verification Requirements

Before considering the work complete, verify:

- training state survives app restart
- free daily limit triggers at the right moment
- premium state unlocks gated modules globally
- routine application updates training state correctly
- progress summaries follow the documented period logic
- locale-dependent week start works correctly
- all supported locales build and resolve strings

## Output Format

When executing this prompt:

1. first summarize the Android architecture you will create
2. then create the project skeleton
3. then implement modules in the required order
4. after each major module, state what was implemented and what remains
5. at the end, list any deliberate Android-specific deviations from iOS behavior
