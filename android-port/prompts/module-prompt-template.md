# Module Prompt Template

Use this template only after the relevant spec files are complete.

## Context

You are implementing the Android version of GymTimerPro.

Read and obey:

- `android-port/specs/master-spec.md`
- `android-port/specs/<module>.md`

The Android app must match the current iOS app in behavior. Do not redesign the product. Do not simplify logic unless the spec explicitly allows it.

## Task

Implement the `<module>` module in Android using:

- Kotlin
- Jetpack Compose
- Room where structured persistence is required
- DataStore where preferences or transient session state are required

## Requirements

- match the module behavior exactly
- preserve all limits and edge cases
- preserve premium gating semantics
- keep code production-grade and modular
- avoid placeholders, fake logic, and TODO-based omissions

## Deliverables

- production-ready Kotlin code
- any Room entities, DAOs, and repositories needed
- any Compose screens and state holders needed
- any Android service or notification integration needed
- a concise list of assumptions, if any are unavoidable

## Verification

Before finishing, verify:

- state transitions match the module spec
- persistence rules match the module spec
- Android-specific implementation choices do not change user-visible behavior
