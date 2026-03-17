# GymTimerPro Android Port: Notifications and Widget Parity Spec

## Scope

This module covers:

- end-of-rest local notifications
- permission request behavior
- active live state representation during a rest session
- parity expectations for the current iOS widget target

Primary iOS sources:

- `GymTimerPro/LiveActivityManager.swift`
- `Shared/GymTimerLiveActivityAttributes.swift`
- `GymTimerProWidget/GymTimerProWidget.swift`
- `GymTimerProWidget-Info.plist`

## Current iOS Product Behavior

The iOS app uses two related mechanisms:

1. local notifications for rest completion
2. Live Activity via ActivityKit and WidgetKit

Important:

- there is no standalone Home Screen widget in the current project
- the widget target exists only to render the Live Activity / Dynamic Island UI

This matters for Android parity:

- the real product requirement is live rest visibility plus end notification
- not a general-purpose home widget

## Notification Authorization

Current iOS behavior:

- request notification authorization only when needed
- skip permission request during UI testing launch mode
- request only if current status is `notDetermined`

Requested options:

- alert
- sound

Android parity intent:

- ask for notification permission only when needed
- avoid prompting during automated tests if test infrastructure replicates this
- permission timing should remain pragmatic, tied to actual rest/notification usage

## End-of-Rest Notification

Current behavior:

- notification is scheduled for rest end
- title uses `notification.rest_finished.title`
- body uses `notification.rest_finished.body_format`
- default sound is enabled
- identifier is fixed: `restTimer.end`
- any previous pending request with that identifier is removed before scheduling

Important semantics:

- only one pending rest-end notification exists at a time
- reset or finish acknowledgement cancels the pending notification

Android requirement:

- preserve single-active-rest-notification semantics
- preserve sound feedback
- preserve cancellation on reset/finish

## Live Activity State Contract

Current Live Activity payload contains:

- `currentSet`
- `totalSets`
- `endDate`
- `mode`

The only current mode values are:

- `resting`
- `training`

In practice, current usage is rest-focused because the activity is updated with rest end date and progress during rest.

Android parity should preserve the same visible state contract:

- current set over total sets
- countdown to rest end
- current mode label
- visual progress indicator

## Start / Update Behavior

Current iOS behavior:

- if an activity already exists, update it
- otherwise, if activities are enabled, request a new Live Activity
- if Live Activity cannot be created, fall back to scheduling end notification only

Android equivalent:

- if a persistent live rest surface already exists, update it
- otherwise create it
- if the richer live surface is unavailable, the app must still guarantee end notification behavior

## End Behavior

Current iOS behavior:

- end current activity immediately
- clear stored reference

Android equivalent:

- terminate the ongoing rest surface immediately when rest ends or workout resets

## Widget / Live UI Content

Current iOS Live Activity UI shows:

- mode icon and mode label
- countdown
- set progress text
- progress bar
- app display name

Dynamic Island / compact views:

- compact leading: timer icon plus set compact text
- compact trailing: countdown
- minimal: countdown only

Lock screen view:

- mode label
- set progress text
- progress bar
- app name
- large countdown

Android parity requirement:

- the ongoing rest surface must expose countdown and set progress at minimum
- app name and mode label are desirable parity details

## Visual Semantics

Current accent behavior:

- both training and resting currently use orange accent in the widget code
- mode symbol is always `timer`

Android should preserve:

- timer-first visual identity
- compact and readable countdown
- set progress prominence

Exact Apple-specific layouts do not need to be copied.

## Current Product Recommendation for Android

Because Android has no direct ActivityKit equivalent, the closest correct parity is:

- ongoing notification during an active rest
- foreground service only if required by the chosen implementation and platform behavior
- end-of-rest notification with sound

Optional parity enhancement:

- Android home screen widget or lock-screen surface only if added deliberately later

This is optional because the iOS codebase does not currently contain a generic widget separate from Live Activity.

## Android Non-Goals for v1

Do not treat these as mandatory for first Android parity:

- standalone widget for static routine/progress content
- multiple widget sizes
- cross-feature widget customization

These are not part of the current iOS product contract.

## Localization Dependencies

This module depends on localized keys such as:

- `notification.rest_finished.title`
- `notification.rest_finished.body_format`
- `live_activity.mode.resting`
- `live_activity.mode.training`
- `live_activity.set_progress_expanded_format`
- `live_activity.set_progress_compact_format`
- `app.name.fallback`

Android must use localized resources for all notification/live-surface labels.

## Acceptance Checklist

The Android notifications/widget parity module is correct only if:

- rest completion can notify the user with sound
- only one active end-of-rest notification exists at a time
- resetting or finishing clears pending notification state
- active rest state is visible outside the main screen through an ongoing surface
- that live surface shows countdown and set progress
- no unnecessary standalone widget requirement is introduced beyond the current iOS contract
