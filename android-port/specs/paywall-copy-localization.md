# GymTimerPro Android Port: Paywall Copy and Localization Source

## Purpose

This document closes the textual parity gap for the Android paywall port.

The exact paywall copy already exists in the current iOS workspace. Android
does not need placeholder copy generation as long as the port reads from the
same source material and mirrors the same key structure.

## Source of Truth

Exact paywall copy comes from these current iOS sources:

- `GymTimerPro/PaywallContent.swift`
- `GymTimerPro/PaywallView.swift`
- `Shared/L10n.swift`
- `Shared/<locale>.lproj/Localizable.strings`

`PaywallContent.swift` is the behavioral source of truth for:

- entry point and info level combinations
- payload structure
- which keys are required or optional per combination

`Shared/<locale>.lproj/Localizable.strings` is the textual source of truth for:

- exact localized copy
- legal text
- CTA labels
- restore/info/error messages
- trial incentive and plan period fallback strings

## Presentation Matrix

There are 6 real copy variants, not 2.

The current mapping is:

- `light + proModule` -> `paywall.copy.light.pro.*`
- `light + dailyLimitDuringWorkout` -> `paywall.copy.light.limit.*`
- `standard + proModule` -> `paywall.copy.standard.pro.*`
- `standard + dailyLimitDuringWorkout` -> `paywall.copy.standard.limit.*`
- `detailed + proModule` -> `paywall.copy.detailed.pro.*`
- `detailed + dailyLimitDuringWorkout` -> `paywall.copy.detailed.limit.*`

Current runtime usage in the iOS app:

- locked premium modules -> `standard + proModule`
- free upsell row in training -> `standard + proModule`
- daily limit interruption -> `light + dailyLimitDuringWorkout`

Even though only those contexts are currently presented in normal flows, all 6
variants exist in code and localization tables and must be preserved in the
Android port.

## Payload Key Schema

Every variant provides these core keys:

- `title`
- `subtitle`
- `benefits_title`
- `bullet_1`
- `bullet_2`
- `bullet_3`
- `plans_title`
- `annual_label`
- `annual_badge`
- `monthly_label`
- `cta_primary`
- `trust`
- `legal1`
- `legal2`

Conditional fields by variant:

- `cta_secondary`
  - present for both `light` variants
  - present for `standard.limit`
  - present for `detailed.limit`
  - absent for `standard.pro`
  - absent for `detailed.pro`
- include section keys
  - only present for both `detailed` variants:
    - `include_title`
    - `include_1` ... `include_6`

Current behavior encoded in `PaywallContent.swift`:

- monthly badge is always `nil`
- secondary action is always dismiss when secondary CTA exists

Android should preserve this exact payload contract instead of flattening copy
into a single screen-specific resource file.

## Auxiliary Paywall Strings Outside the Main Payload

The paywall also depends on these localized keys outside the `paywall.copy.*`
namespaces:

- `paywall.badge.pro`
- `paywall.subtitle_limit_format`
- `paywall.price.loading`
- `paywall.trial_incentive_format`
- `paywall.period.generic`
- `paywall.button.restore`
- `paywall.button.manage`
- `paywall.button.terms`
- `paywall.button.privacy`
- `paywall.error.title`
- `paywall.error.product_unavailable`
- `paywall.error.failed_verification`
- `paywall.error.user_cancelled`
- `paywall.error.pending`
- `paywall.error.unknown`
- `paywall.restore.no_purchases`
- `paywall.info.title`
- `common.ok`
- `common.cancel`

These keys are part of textual parity and must be ported to Android string
resources together with the main payload keys.

## Locale Coverage Confirmed in Workspace

The paywall copy and related message strings currently exist in these 15 locale
files:

- `Shared/da.lproj/Localizable.strings`
- `Shared/de.lproj/Localizable.strings`
- `Shared/en.lproj/Localizable.strings`
- `Shared/es.lproj/Localizable.strings`
- `Shared/fr.lproj/Localizable.strings`
- `Shared/hi.lproj/Localizable.strings`
- `Shared/it.lproj/Localizable.strings`
- `Shared/ja.lproj/Localizable.strings`
- `Shared/ko.lproj/Localizable.strings`
- `Shared/nb.lproj/Localizable.strings`
- `Shared/nl.lproj/Localizable.strings`
- `Shared/pt-BR.lproj/Localizable.strings`
- `Shared/pt-PT.lproj/Localizable.strings`
- `Shared/sv.lproj/Localizable.strings`
- `Shared/zh-Hans.lproj/Localizable.strings`

This means the Android port is not blocked by missing copy source material.
Textual parity is achievable from the current workspace.

## Android Porting Rule

When implementing Android:

- keep the same 6-variant paywall copy matrix
- map each namespace to Android string resources without collapsing variants
- preserve optional-field behavior per combination
- keep the 15-locale coverage as the initial localization scope
- treat this file and `PaywallContent.swift` as the contract for textual parity
