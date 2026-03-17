# GymTimerPro Android Port: Master Spec

## Objective

Create an Android version of GymTimerPro with behavior matching the current iOS app as closely as possible.

This is not a redesign and not a product rethink. The Android app must preserve the same product contract unless Android platform constraints require a documented deviation.

## Product Scope

The current app has four primary areas:

1. Training
2. Routines
3. Progress
4. Settings

Access model:

- Training is available in free mode.
- Routines, Progress, and Settings are visually present but premium-gated for free users.
- Free mode also has a daily training-use limit.

## Source of Truth in iOS

Global app structure and container:

- `GymTimerPro/GymTimerProApp.swift`
- `GymTimerPro/MainTabView.swift`

Training:

- `GymTimerPro/ContentView.swift`
- `GymTimerPro/LiveActivityManager.swift`
- `GymTimerPro/DailyUsageLimiter.swift`
- `Shared/GymTimerLiveActivityAttributes.swift`

Routines:

- `GymTimerPro/Routines/`

Progress:

- `GymTimerPro/Progress/`

Premium and paywall:

- `GymTimerPro/PurchaseManager.swift`
- `GymTimerPro/PaywallContent.swift`
- `GymTimerPro/PaywallView.swift`

Localization:

- `Shared/*.lproj/Localizable.strings`
- `Shared/L10n.swift`

## Android Architecture Target

### Stack

- Language: Kotlin
- UI: Jetpack Compose
- Navigation: Navigation Compose
- Structured persistence: Room
- Preferences persistence: DataStore Preferences
- Dependency management: standard Gradle Kotlin DSL
- Billing: Google Play Billing
- Notifications: NotificationManager
- Background timer behavior: foreground service when required by platform behavior and user experience

### Data Strategy

The iOS app uses SwiftData and attempts CloudKit private storage first, then local fallback.

For Android v1:

- local-first
- no cross-platform sync
- no backend required

This is the most pragmatic equivalent because there is no direct Android equivalent to private CloudKit integration.

## Core Parity Rules

The Android port must preserve:

- same tab structure
- same premium gating rules
- same routine application behavior
- same training flow
- same rest timer semantics
- same daily free-limit semantics
- same settings and preference meanings
- same routine and progress data concepts
- same localization set where feasible

The Android port may adapt:

- Live Activity behavior into Android-native ongoing notification behavior
- widget implementation details
- some visual controls where Android has different interaction norms

## Functional Modules

### 1. Training

Responsibilities:

- configure total sets
- configure rest duration
- apply a premium routine into current training state
- start rest between sets
- complete workout automatically when last set action is triggered
- persist timer state
- restore timer state after lifecycle changes
- play completion feedback
- record workout completion

### 2. Routines

Responsibilities:

- create, edit, delete routines
- assign classifications
- choose a routine
- apply selected routine to training

### 3. Progress

Responsibilities:

- read workout completion history
- summarize usage over time periods
- show calendar and chart views
- respect locale-based week start

### 4. Settings

Responsibilities:

- choose weight unit
- choose timer display format
- choose max sets cap
- choose rest increment step
- choose energy-saving mode
- manage classifications

### 5. Premium / Paywall

Responsibilities:

- load products
- show plan comparison
- purchase monthly or yearly
- restore purchases
- unlock premium sections and unlimited usage

## Persistence Contract

The current iOS app stores two kinds of state:

1. Structured models
   - `Routine`
   - `RoutineClassification`
   - `WorkoutCompletion`

2. Preferences and transient training state
   - current total sets
   - max sets preference
   - current rest seconds
   - rest increment preference
   - timer display format
   - power saving mode
   - current set number
   - completion flag
   - currently applied routine name and reps
   - timer end date, running state, remaining duration
   - purchase entitlement cache
   - daily usage limiter counters

On Android, this should map to:

- Room for structured entities
- DataStore for preferences and training session state

## Non-Negotiable Behavioral Details

- Free users can access Training but not full Routines, Progress, or Settings flows.
- Free users have a daily usage limit of 16 rest-start consumptions.
- Starting rest increments the current set before the countdown starts.
- If the user triggers start rest while already on the last set, the workout completes instead of starting another rest.
- Rest duration options are constrained by a configurable step preference.
- Total sets are constrained by a configurable max-sets preference.
- Applying a routine resets current workout progress and overwrites total sets and rest seconds.
- Applied routine total sets are capped by the global max-sets preference.

## Android Implementation Principles

- Do not try to mirror SwiftUI component-for-component.
- Mirror behavior and state transitions, not Apple-specific APIs.
- Do not introduce sync or backend complexity in v1.
- Do not redesign premium gating or product structure.
- Keep module boundaries explicit so each screen can be verified against iOS.

## Acceptance Criteria

The Android port is acceptable only if:

- a user can perform the same training session flow as on iOS
- the same settings produce the same training behavior
- premium and free experiences match the current product contract
- routine application changes training state identically
- progress is computed from equivalent completion records
- major user-facing strings and flows match current iOS intent

## Extraction Order

Recommended port order:

1. Training and timer engine
2. Preferences and settings required by training
3. Routine model and routine selection
4. Workout completion persistence
5. Progress
6. Premium and paywall
7. Notifications and widget parity
8. Full localization pass
