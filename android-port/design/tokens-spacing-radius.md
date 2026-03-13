# Android Design Contract - Spacing, Radius, Border, Elevation Tokens

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/ContentView.swift (`Layout` enum)
  - GymTimerPro/PaywallView.swift
  - GymTimerPro/Progress/ProgramProgressView.swift

## Spacing Scale

| Token | Value (dp) | iOS Source | Compose Variable |
| --- | --- | --- | --- |
| `space4` | 4 | Paywall badge vertical / plan badge vertical | `GymSpacing.s4` |
| `space6` | 6 | Progress calendar row spacing | `GymSpacing.s6` |
| `space8` | 8 | Paywall trial row / include row / top paddings | `GymSpacing.s8` |
| `space10` | 10 | Section header row gap / cards | `GymSpacing.s10` |
| `space12` | 12 | `Layout.rowSpacing`, `metricPadding`, cards | `GymSpacing.s12` |
| `space14` | 14 | Progress section card padding | `GymSpacing.s14` |
| `space16` | 16 | `Layout.cardPadding`, paywall section padding | `GymSpacing.s16` |
| `space20` | 20 | `Layout.sectionSpacing`, horizontal main padding | `GymSpacing.s20` |
| `space24` | 24 | Main horizontal lock overlay / scroll bottom additive | `GymSpacing.s24` |
| `space32` | 32 | Empty-state top offset | `GymSpacing.s32` |

## Radius Tokens

| Token | Value (dp) | iOS Source | Compose Shape |
| --- | --- | --- | --- |
| `radius6` | 6 | `Layout.resetIconCornerRadius` | `RoundedCornerShape(6.dp)` |
| `radius8` | 8 | Icon badge background | `RoundedCornerShape(8.dp)` |
| `radius10` | 10 | `Layout.wheelCornerRadius` | `RoundedCornerShape(10.dp)` |
| `radius12` | 12 | Progress cards/calendar chips | `RoundedCornerShape(12.dp)` |
| `radius14` | 14 | Paywall plan cards / progress section cards | `RoundedCornerShape(14.dp)` |
| `radius16` | 16 | `Layout.metricCornerRadius`, button | `RoundedCornerShape(16.dp)` |
| `radius18` | 18 | MainTab lock overlay container | `RoundedCornerShape(18.dp)` |
| `radius20` | 20 | `Layout.cardCornerRadius`, paywall sections | `RoundedCornerShape(20.dp)` |
| `capsule` | 999 | Capsule badges/buttons | `RoundedCornerShape(percent = 50)` |

## Border Tokens

| Token | Width (dp) | Color Token |
| --- | --- | --- |
| `borderCard` | 1 | `cardBorder` |
| `borderPlanSelected` | 1.5 | `primary` |
| `borderProgressCell` | 1.5 | calendar day border logic |
| `borderWheelTrack` | 1 | `wheelTrackStroke` |
| `borderResetIcon` | 1 | `secondaryButtonBorder` |
| `borderQuaternary` | 1 | `outlineVariant` equivalent |

## Elevation and Shadow Tokens

| Token | Value | iOS Source | Compose Implementation |
| --- | --- | --- | --- |
| `shadowCard` | radius `12`, y `6`, alpha `0.08` | `Theme.cardShadow` + `.shadow` | `Modifier.shadow(12.dp, spotColor = cardShadow)` |
| `shadowBottomControls` | radius `12`, y `-6`, alpha `0.08` | controls section shadow | `Modifier.shadow(12.dp)` + top divider |
| `shadowPrimaryButton` | radius `10`, y `6`, alpha `0.25` when enabled | `PrimaryButtonStyle` | `Modifier.shadow(10.dp)` conditional |

## Layout Tokens

| Token | Value | Source |
| --- | --- | --- |
| `minTapHeight` | `44dp` iOS -> `48dp` Android enforced touch target | `Layout.minTapHeight` |
| `primaryButtonHeight` | `80dp` | `Layout.primaryButtonHeight` |
| `scrollBottomPadding` | `104dp` (`24 + 80`) | `Layout.scrollBottomPadding` |
| `wheelThumbSize` | `16dp x 16dp` | `Layout.wheelThumbSize` |
| `wheelHitTarget` | `44dp` iOS -> `48dp` Android floor | `Layout.wheelHitTarget` |

## Compose Shapes/Spacing Snippet

```kotlin
object GymSpacing {
    val s4 = 4.dp
    val s6 = 6.dp
    val s8 = 8.dp
    val s10 = 10.dp
    val s12 = 12.dp
    val s14 = 14.dp
    val s16 = 16.dp
    val s20 = 20.dp
    val s24 = 24.dp
    val s32 = 32.dp
}

val GymShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp)
)
```

## Acceptance

| Criterio | Delta permitido | Estado |
| --- | --- | --- |
| Espaciados | +/- 4dp | PASS/FAIL |
| Radios | +/- 2dp | PASS/FAIL |
| Bordes | Exacto | PASS/FAIL |
| Sombras/elevaciones | Visualmente equivalentes | PASS/FAIL |
| Touch target Android (min 48dp) | Exacto | PASS/FAIL |

## Pendiente

- Cerrar la verificación visual en dispositivos con densidad baja/alta para confirmar que radios pequeños (`6/8`) no se perciben más duros que en iOS.
