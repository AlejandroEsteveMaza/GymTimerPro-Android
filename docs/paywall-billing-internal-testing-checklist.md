# GymTimerPro Android - Paywall/Billing Internal Testing Checklist

## Scope

Validate end-to-end Play Billing behavior and paywall parity against:

- `android-port/specs/master-spec.md`
- `android-port/specs/paywall-subscriptions.md`
- `android-port/specs/training.md`
- `android-port/specs/localization.md`

This checklist covers purchase, restore, entitlement refresh, gating behavior, and contextual paywall entry points.

## Local Build Verification (Completed)

Command executed:

```bash
JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' \
./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleRelease :app:bundleRelease
```

Result: `BUILD SUCCESSFUL` on March 11, 2026.

Artifacts:

- `app/build/outputs/bundle/release/app-release.aab`
- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/outputs/apk/release/app-release-unsigned.apk`

Checksums (SHA-256):

- `app-release.aab`: `94b2843882fec31f8a8d2c5b5c467a98742f5fa1a186aea26ed476491411e6c2`
- `app-debug.apk`: `be7b0aef205605e179f78eadabda7f81ded57730df4a2648af23a4722c824563`
- `app-release-unsigned.apk`: `da07086ece7321fc737d8ec1e27d8b6083715baea2d5419e5e9c12d5d6cb1c22`

## Play Console Preconditions

1. Application ID in Play Console matches:
   - `com.alejandroestevemaza.gymtimerpro`
2. Subscription products exist and are active:
   - `premium_yearly`
   - `premium_monthly`
3. Tester Google account is added in:
   - Play Console testing track users
   - Play Billing license testers
4. Internal testing release is uploaded with a valid signed AAB.
5. Device is logged into Play Store with the tester account.
6. App is installed from Play internal testing build (not side-loaded APK) for real billing tests.

## Billing + Paywall Runtime Checklist

### A. Free user baseline and gating

1. Fresh install with no active subscription.
2. Open app.
3. Confirm:
   - Training is accessible.
   - Routines, Progress, Settings are visible but locked.
4. Tap each locked tab and verify paywall open context:
   - entry point behaves as `proModule`
   - info level behaves as `standard`

Expected:

- No premium content accessible.
- Contextual paywall appears instead of generic lock behavior.

### B. Daily limit contextual paywall (Training)

1. Stay in free mode.
2. Trigger successful rest starts up to daily limit (16).
3. Attempt one more rest start.

Expected:

- On limit reached, paywall opens with `dailyLimitDuringWorkout` + `light`.
- Daily limit status line appears when `consumedToday >= dailyLimit`.
- No extra usage is consumed after limit is reached.

### C. Plan ordering and default selection

1. Open paywall with products loaded.

Expected:

- Plans sorted yearly first, monthly second.
- Yearly selected by default when available.
- Price rendered as `price / period`.

### D. Purchase success path

1. In paywall, buy yearly or monthly from internal test offer.
2. Complete purchase in Play flow.

Expected:

- Paywall dismisses automatically after entitlement becomes active.
- `isPro` unlocks globally:
  - Routines unlocked
  - Progress unlocked
  - Settings unlocked
  - Daily usage limit bypassed in Training

### E. Purchase cancellation and error states

1. Start purchase and cancel from Play sheet.
2. Repeat and simulate unavailable/pending if possible.

Expected:

- User cancel shows no error alert.
- Pending shows pending error.
- Unavailable/verification/unknown show mapped error alert.

### F. Restore path

Case 1: tester with active subscription history.

1. Reinstall app or clear app data.
2. Open paywall.
3. Tap Restore.

Expected:

- Premium entitlement restored.
- Paywall dismisses when `isPro=true`.

Case 2: tester without purchases.

1. Open paywall.
2. Tap Restore.

Expected:

- Informational message "no purchases to restore".
- Not treated as hard failure.

### G. Entitlement reconciliation

1. With active sub, kill app and relaunch.
2. Bring app background/foreground.

Expected:

- Cached state is used at startup.
- Entitlement reconciles against Play and remains accurate.
- Premium state remains consistent across tabs and Training.

### H. Links and legal config

In paywall, verify:

1. Restore button always present.
2. Manage subscription button opens Play subscription management.
3. Terms opens `PAYWALL_TERMS_URL`.
4. Privacy opens `PAYWALL_PRIVACY_URL`.

### I. Localization smoke

Run quick checks in at least 3 locales, including `en`, `es`, and one CJK locale.

Expected:

- Paywall copy resolves localized strings.
- Buttons, trust/legal lines, and lock texts are localized.

## Evidence to Capture

1. Video of:
   - free lock -> paywall
   - purchase success -> unlock
   - restore with no purchases
2. Screenshots:
   - paywall from `proModule/standard`
   - paywall from `dailyLimitDuringWorkout/light`
   - yearly preselected plan
3. Notes with timestamps and tester account used.

## Known Android-Specific Adaptations

1. Restore parity uses Play purchase query/reconciliation instead of StoreKit `AppStore.sync()`.
2. Manage subscription uses Play URL:
   - `https://play.google.com/store/account/subscriptions?package=<packageName>`

## Fail Criteria

Any of these is a release blocker:

1. User-cancelled purchase shows error alert.
2. Successful purchase does not unlock all premium-gated modules globally.
3. Restore does not recover known entitlement.
4. Daily free limit logic breaks (wrong threshold or wrong paywall context).
5. Legal links are missing or hardcoded incorrectly.
