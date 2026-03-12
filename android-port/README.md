# Android Port Workspace

This folder is the source of truth for rebuilding GymTimerPro on Android with strict parity to the current iOS app.

## Goal

Rebuild the existing iOS app on Android without reusing Swift code, but preserving:

- product behavior
- navigation and UX intent
- business rules
- premium gating
- data model
- localization scope
- visual hierarchy

The Android project should not be generated from a single general prompt. The port is split into:

1. `specs/master-spec.md`
   Defines the global contract of the app.
2. `specs/<module>.md`
   Defines exact behavior per module.
3. `prompts/<module>-prompt.md`
   Uses the specs to produce implementation tasks or coding prompts.

## Working Method

For each module:

1. Inspect the current iOS source files.
2. Extract business rules and UI states into a module spec.
3. Record all persistence keys, limits, feature flags, and edge cases.
4. Define the Android implementation target.
5. Only then generate coding prompts or write Android code.

## Android Baseline

Current baseline decisions for the future Android app:

- Language: Kotlin
- UI: Jetpack Compose
- Structured data: Room
- Preferences: DataStore
- Billing: Google Play Billing
- Rest timer background behavior: Foreground service plus NotificationManager, with AlarmManager only where exact scheduling is required
- Widgets: App Widgets only if parity is required
- Initial data scope: local-first, no cross-platform sync

## Status

Currently extracted:

- master architecture and parity rules
- training and rest timer module
- routines and classifications module
- progress module
- settings module
- paywall and subscriptions module
- paywall copy and localization source map
- notifications and widget parity
- localization

Pending extraction:

- none
