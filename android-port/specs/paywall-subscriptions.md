# GymTimerPro Android Port: Paywall and Subscriptions Spec

## Scope

This module covers:

- premium entitlement state
- subscription product loading
- paywall presentation contexts
- plan selection and purchase flow
- restore flow
- legal and management links
- error and info messaging
- premium gating contract used by the rest of the app

Primary iOS sources:

- `GymTimerPro/PurchaseManager.swift`
- `GymTimerPro/PaywallContent.swift`
- `GymTimerPro/PaywallView.swift`
- `GymTimerPro/MainTabView.swift`
- `GymTimerPro/ContentView.swift`
- `GymTimerPro/Info.plist`
- `Shared/L10n.swift`
- `Shared/<locale>.lproj/Localizable.strings`
- `android-port/specs/paywall-copy-localization.md`

## Product Role

Premium unlocks:

- routines tab
- progress tab
- settings tab
- unlimited daily usage in training

Free mode keeps:

- training available
- daily limit of 16 rest-start consumptions

This premium contract is global and must remain unchanged in Android.

## Product IDs

Current subscription product IDs:

- yearly: `premium_yearly`
- monthly: `premium_monthly`

These should be mirrored as the Google Play subscription identifiers or mapped explicitly if Play naming differs.

## Entitlement Model

Current iOS state:

- `isPro: Bool`
- `proProductsByID: [String: Product]`
- `isLoading: Bool`

Cached local key:

- `purchase.cachedIsPro`

Behavior:

- app starts with cached `isPro`
- refresh loads products and then current entitlements
- verified active entitlement for either premium product grants `isPro = true`
- revocation removes premium

Android requirement:

- keep the same observable entitlement model
- allow immediate app startup from cached state
- reconcile cached state against Play Billing entitlements as soon as possible

## Purchase Manager Responsibilities

The purchase manager currently handles:

- loading products
- selecting available premium products
- starting purchase
- verifying purchase result
- finishing transactions
- restoring purchases
- listening for transaction updates
- updating global entitlement state

Android equivalent should centralize these same responsibilities in one billing manager or repository.

## Product Loading

Current behavior:

- products are loaded only when `proProductsByID` is empty
- failed loading leaves product map empty
- loading state is exposed

Android parity:

- expose loading state
- expose the available product details for UI rendering
- allow refresh when needed

## Default Plan Selection

The paywall chooses the default selected plan this way:

- if yearly is available, select yearly
- otherwise select the first available product

This is a non-trivial business rule and should be preserved.

## Purchase Flow

### Buy

When user taps primary CTA:

1. paywall must have a selected product
2. call purchase for that product ID
3. handle result

Current result mapping:

- verified success -> finish transaction -> refresh entitlements
- user cancelled -> no alert
- pending -> show pending error
- unavailable -> show unavailable error
- unverified -> show verification error
- unknown -> show generic error

If premium becomes active:

- dismiss paywall automatically

## Restore Flow

Current behavior:

- restore action runs explicit store sync
- then refreshes entitlements
- if no premium entitlement is found after restore, show info message "no purchases to restore"

Android parity:

- keep a dedicated restore action
- after restore/query, if no entitlement exists, show non-error informational message

## Transaction Update Listening

Current iOS manager continuously listens to StoreKit transaction updates.

Meaning:

- entitlement changes can unlock premium even after paywall render

Android equivalent:

- observe billing purchase updates and entitlement reconciliation continuously enough that premium state stays accurate without relaunch

## Paywall Presentation Context

The paywall is not a single static screen. It varies by:

- entry point
- info level

### Entry Points

- `proModule`
- `dailyLimitDuringWorkout`

### Info Levels

- `light`
- `standard`
- `detailed`

The combination defines the full copy payload.

Android requirement:

- keep this copy/context model
- do not collapse all paywall presentations into a single static string set

## Current Paywall Entry Usage

### From locked premium modules

Shown with:

- entry point: `proModule`
- info level: `standard`

### From daily limit hit during training

Shown with:

- entry point: `dailyLimitDuringWorkout`
- info level: `light`

### From free upsell row in training config

Shown with:

- entry point: `proModule`
- info level: `standard`

Android should preserve these contextual triggers unless explicitly changed later.

## Paywall Copy Model

Each paywall copy payload includes:

- title
- subtitle
- benefits title
- 3 bullets
- plans title
- annual label
- annual badge
- monthly label
- optional monthly badge
- primary CTA
- optional secondary CTA
- trust line
- legal line 1
- legal line 2
- optional include-section title
- optional include items list

The copy is fully localized through string tables.

Android requirement:

- use localized resources
- keep the same payload structure so the same contextual paywall composition remains possible

Exact copy parity source:

- do not derive paywall copy from this spec alone
- the exact localized strings already exist in the workspace
- use `android-port/specs/paywall-copy-localization.md` as the copy-source index
- use `GymTimerPro/PaywallContent.swift` as the runtime contract for key selection

This closes the previous documentation gap: the port is not blocked by missing
paywall source copy.

## Paywall Layout Semantics

The paywall screen includes, in order:

1. header section
2. benefits section
3. optional include section
4. plans section
5. CTA section
6. legal text section
7. links section

It is wrapped in a modal navigation container with a dismiss button.

Dismiss behavior:

- disabled while purchase/restore is processing

## Header Section

Contains:

- "PRO" badge
- title
- subtitle
- optional daily-limit status line when user reached the free limit

Daily-limit extra line:

- shown only when `consumedToday >= dailyLimit`

## Plans Section

Rules:

- plans are sorted yearly first, monthly second, unknown others last
- selected plan shows active border and checkmark circle
- yearly plan can show annual badge
- monthly badge is currently optional and usually null
- price line is displayed as `price / period`

Period labeling:

- monthly -> localized month text
- yearly -> localized year text
- other subscription periods fall back to generic period formatting

## Trial Incentive Text

The paywall may show a trial incentive banner above plans.

Current rule:

- inspect introductory offers on annual and monthly products
- only consider offers where payment mode is free trial
- if both products have the same free trial period, show a single merged banner
- otherwise show whichever free-trial period exists

Android parity:

- preserve the semantic behavior if Play Billing data supports the same distinction
- if exact offer introspection differs on Android, document the closest supported equivalent

## Legal and Link Behavior

### Restore

- always shown

### Manage Subscription

- shown if manage URL exists
- current iOS manage URL points to Apple subscriptions page

Android equivalent:

- should deep-link to Play subscription management if possible

### Terms and Privacy

Current source:

- loaded from app configuration keys:
  - `PAYWALL_TERMS_URL`
  - `PAYWALL_PRIVACY_URL`

Current fallback values:

- terms: Apple standard EULA URL
- privacy: Apple privacy URL fallback, though the app currently overrides this with project-specific privacy URL in `Info.plist`

Current configured production values in the app:

- `PAYWALL_TERMS_URL = https://www.apple.com/legal/internet-services/itunes/dev/stdeula/`
- `PAYWALL_PRIVACY_URL = https://alejandroestevemaza.github.io/GymTimerPro-privacy/`

Android requirement:

- keep legal URLs externally configurable
- do not hardcode them only inside UI code
- keep both URLs available even if the paywall screen renders before billing data loads
- keep the manage subscription action separate from terms and privacy

## Info.plist / Config Contract

Current app config contains:

- `PAYWALL_TERMS_URL = https://www.apple.com/legal/internet-services/itunes/dev/stdeula/`
- `PAYWALL_PRIVACY_URL = https://alejandroestevemaza.github.io/GymTimerPro-privacy/`

Android equivalent:

- use build config, manifest metadata, or a centralized config source
- the UI should read from config, not embed environment-specific URLs directly

## Error and Info States

Current alert cases:

- product unavailable
- failed verification
- pending
- unknown

Exact localized message surface used today:

- error title: `paywall.error.title`
- unavailable: `paywall.error.product_unavailable`
- verification failure: `paywall.error.failed_verification`
- pending: `paywall.error.pending`
- generic: `paywall.error.unknown`
- restore-without-entitlement info: `paywall.restore.no_purchases`
- info alert title: `paywall.info.title`

User-cancelled purchase:

- does not show an error alert
- localized key exists (`paywall.error.user_cancelled`) but current UI intentionally suppresses it

Separate informational alert:

- no purchases available to restore

Android parity:

- keep user-cancelled silent
- keep restore-without-entitlement as informational, not failure
- keep restore failures and unknown purchase failures on the error channel
- keep info and error messaging separated in the UI state model

## Processing State

Current paywall behavior while processing purchase or restore:

- interactive dismiss disabled
- close button disabled
- main actions disabled as needed

Android should preserve this anti-double-submit behavior.

## Premium Gating Outside Paywall

The paywall is only one side of the premium system.

Other areas depend on `isPro`:

- tab content lock overlays
- training routine access
- daily usage limiter bypass

Android implementation must keep one shared premium state source used by all features.

## Localization Requirements

The paywall copy is heavily localized and structured.

Android should:

- port all paywall strings to string resources
- preserve entry-point and info-level specific variants
- preserve legal and trust lines
- preserve restore/manage/privacy/terms button strings
- preserve trial incentive formatting and generic period fallback strings
- preserve the current initial scope of 15 locales documented in `paywall-copy-localization.md`

## Billing Dependency Status in Current Workspace

Current repository state:

- there is no Android project checked into this workspace yet
- there is no Gradle build, version catalog, or existing Android billing dependency to reuse
- there is no Android-side billing abstraction already started outside `android-port/`

Implication for the Android port:

- Billing infrastructure must be added from scratch in the Android project
- this spec defines behavior only; it does not assume a pre-existing dependency catalog
- when the Android project is created, add Google Play Billing there rather than trying to infer a reusable dependency setup from this iOS workspace

## Android Implementation Notes

- Implement one billing manager with cached entitlement plus live reconciliation.
- Expose a stable `isPro` flow or state holder shared by the app shell and paywall UI.
- Keep the paywall UI data-driven from a copy model similar to the current `PaywallCopy`.
- Do not bury entitlement checks directly inside composables.
- Read legal URLs from centralized config.
- Keep exact paywall namespaces traceable back to `paywall-copy-localization.md`.

## Acceptance Checklist

The Android paywall/subscriptions module is correct only if:

- yearly and monthly plans are both supported
- yearly is selected by default when available
- successful purchase unlocks premium globally
- restore can recover premium and informs cleanly when nothing is found
- user-cancelled purchases do not show an error
- paywall copy varies by entry point and information level
- legal links are configurable outside UI code
- premium gating semantics remain identical to iOS
