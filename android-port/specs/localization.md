# GymTimerPro Android Port: Localization Spec

## Scope

This module covers:

- localization resource model
- language coverage
- formatting behavior
- key parity expectations between iOS and Android

Primary iOS sources:

- `Shared/L10n.swift`
- `Shared/*.lproj/Localizable.strings`

## Current Localization Mechanism

The current iOS app uses a lightweight wrapper:

- `L10n.tr(key)` for plain strings
- `L10n.format(key, args...)` for formatted strings

Implementation details:

- strings are resolved with `NSLocalizedString`
- formatted strings use `String(format:locale:arguments:)`
- locale-sensitive formatting uses `Locale.current`

Android requirement:

- use standard Android string resources
- use localized formatted strings with placeholders
- preserve locale-aware formatting behavior

## Supported Languages

Current language set extracted from the repository:

- Danish: `da`
- German: `de`
- English: `en`
- Spanish: `es`
- French: `fr`
- Hindi: `hi`
- Italian: `it`
- Japanese: `ja`
- Korean: `ko`
- Norwegian Bokmal: `nb`
- Dutch: `nl`
- Portuguese (Brazil): `pt-BR`
- Portuguese (Portugal): `pt-PT`
- Swedish: `sv`
- Simplified Chinese: `zh-Hans`

Total:

- 15 locales

## Resource Organization on iOS

Current organization:

- one `Localizable.strings` file per locale inside `Shared/<locale>.lproj`

The same key space is shared across:

- training
- routines
- progress
- settings
- paywall
- notifications
- live activity / widget

Android should preserve one shared logical key space, even if files are split physically.

## Recommended Android Locale Mapping

Recommended Android resource qualifiers:

- `en` -> `values`
- `es` -> `values-es`
- `de` -> `values-de`
- `fr` -> `values-fr`
- `it` -> `values-it`
- `ja` -> `values-ja`
- `ko` -> `values-ko`
- `nl` -> `values-nl`
- `sv` -> `values-sv`
- `da` -> `values-da`
- `hi` -> `values-hi`
- `nb` -> `values-nb`
- `pt-BR` -> `values-pt-rBR`
- `pt-PT` -> `values-pt-rPT`
- `zh-Hans` -> `values-b+zh+Hans` or equivalent simplified-Chinese qualifier chosen consistently across the project

The exact Android folder naming can be finalized at implementation time, but the locale coverage itself is not optional.

## Key Parity Rule

The Android app must preserve the same semantic keys and messages as the iOS app.

This does not require using the exact same key names internally in Android, but it is strongly recommended because:

- specs reference the current keys
- copy is already organized around the existing names
- future parity checks are easier

Recommended approach:

- keep the iOS key names as Android string resource names where valid
- if a key name is invalid in Android resource naming, map it through a documented conversion table

## Formatted Strings

Many strings include placeholders.

Examples of formatted content:

- set counts
- streak counts
- progress counts
- calendar completed counts
- paywall limit counts
- trial-period text

Android requirement:

- preserve argument order and plural/number semantics
- use numbered placeholders where needed for translator safety

Important:

- never convert formatted strings into concatenated fragments in code

## Locale-Sensitive Formatting Areas

The app depends on locale-sensitive formatting in these areas:

- decimal and weight formatting
- formatted rest-time strings
- chart/date labels
- day and month names
- time of completion in progress drilldowns

Android parity:

- all user-visible dates, times and numbers must use locale-aware Android formatters
- do not hardcode English month/day names

## Cross-Module Localization Contract

Localization touches all extracted modules:

- training labels and accessibility text
- routines labels, summaries and validation messages
- progress titles, badges, chart labels and calendar accessibility text
- settings labels and descriptions
- paywall copy, legal lines and button labels
- notifications and live-state labels

This means the Android port should not postpone localization until the end as an afterthought. Resource keys must be part of the implementation from the start.

## Accessibility Text

Some strings exist specifically for accessibility:

- set progress labels
- time remaining
- rest value accessibility text
- calendar day accessibility messages

Android requirement:

- preserve dedicated accessibility strings where the current product already distinguishes them
- do not assume the visible text is always enough

## App Name Fallback

There is a localized fallback key:

- `app.name.fallback`

Used in the widget/live activity context when display name cannot be read from bundle configuration.

Android should preserve an equivalent fallback strategy where a localized app name fallback is useful.

## Translation Completeness Rule

The Android port should target the same locale coverage as the current iOS app.

That means:

- do not ship Android initially with only English if the goal is parity with the current iOS product
- if staged rollout is necessary, it must be an explicit business decision, not an accidental regression

## Resource Strategy Recommendation

Recommended Android strategy:

- keep one base `strings.xml` per locale for app-wide strings
- optionally split into multiple files such as `strings_paywall.xml` and `strings_progress.xml` only if the naming and maintenance remain disciplined
- centralize formatting helpers for dates, numbers and durations

## Acceptance Checklist

The Android localization module is correct only if:

- all 15 current locales are represented
- formatted strings remain localized and parameterized
- date, time and number formatting are locale-aware
- accessibility-specific strings are preserved
- paywall, notification and progress strings are localized from the start
