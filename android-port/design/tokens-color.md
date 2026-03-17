# Android Design Contract - Color Tokens

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/ContentView.swift (`Theme` enum)
  - GymTimerPro/PaywallView.swift
  - GymTimerPro/Progress/ProgramProgressView.swift

## Token Rules

- Hex and RGBA values are documented as `Light / Dark` for dynamic iOS semantic colors.
- Alpha values from iOS are preserved in RGBA where used (`separator`, fills, shadows).
- Android implementation must keep these values in theme tokens and not inline literals.

## Color Token Table

| Role | Hex (Light / Dark) | RGBA (Light / Dark) | Compose Variable |
| --- | --- | --- | --- |
| `background` (`systemGroupedBackground`) | `#F2F2F7 / #000000` | `242,242,247,1 / 0,0,0,1` | `colorScheme.background` |
| `cardBackground` (`secondarySystemGroupedBackground`) | `#FFFFFF / #1C1C1E` | `255,255,255,1 / 28,28,30,1` | `colorScheme.surface` |
| `controlsBackground` (`systemBackground`) | `#FFFFFF / #000000` | `255,255,255,1 / 0,0,0,1` | `colorScheme.surfaceBright` |
| `textPrimary` (`label`) | `#000000 / #FFFFFF` | `0,0,0,1 / 255,255,255,1` | `colorScheme.onSurface` |
| `textSecondary` (`secondaryLabel`) | `#3C3C43 / #EBEBF5` | `60,60,67,0.60 / 235,235,245,0.60` | `colorScheme.onSurfaceVariant` |
| `divider` (`separator`) | `#3C3C43 / #545458` | `60,60,67,0.29 / 84,84,88,0.65` | `colorScheme.outlineVariant` |
| `iconTint` (`systemBlue`) | `#007AFF / #0A84FF` | `0,122,255,1 / 10,132,255,1` | `colorScheme.primary` |
| `iconBackground` (`tertiarySystemFill`) | `#767680 / #767680` | `118,118,128,0.12 / 118,118,128,0.24` | `colorScheme.surfaceContainerHighest` |
| `training` (`systemTeal`) | `#5AC8FA / #64D2FF` | `90,200,250,1 / 100,210,255,1` | `gymColors.training` |
| `resting` (`systemOrange`) | `#FF9500 / #FF9F0A` | `255,149,0,1 / 255,159,10,1` | `gymColors.resting` |
| `completed` (`systemGreen`) | `#34C759 / #30D158` | `52,199,89,1 / 48,209,88,1` | `gymColors.completed` |
| `primaryButton` (`systemBlue`) | `#007AFF / #0A84FF` | `0,122,255,1 / 10,132,255,1` | `gymColors.primaryButton` |
| `primaryButtonPressed` | `#007AFF / #0A84FF` | `0,122,255,0.85 / 10,132,255,0.85` | `gymColors.primaryButtonPressed` |
| `primaryButtonDisabled` (`systemGray4`) | `#D1D1D6 / #3A3A3C` | `209,209,214,1 / 58,58,60,1` | `gymColors.primaryButtonDisabled` |
| `primaryButtonText` | `#FFFFFF / #FFFFFF` | `255,255,255,1 / 255,255,255,1` | `colorScheme.onPrimary` |
| `secondaryButtonFill` (`secondarySystemBackground`) | `#F2F2F7 / #1C1C1E` | `242,242,247,1 / 28,28,30,1` | `colorScheme.surfaceContainer` |
| `secondaryButtonBorder` (`systemGray3`) | `#C7C7CC / #48484A` | `199,199,204,1 / 72,72,74,1` | `colorScheme.outline` |
| `metricBackground` (`tertiarySystemBackground`) | `#FFFFFF / #2C2C2E` | `255,255,255,1 / 44,44,46,1` | `colorScheme.surfaceContainerLow` |
| `timerBackground` | `#FF9500 / #FF9F0A` | `255,149,0,0.12 / 255,159,10,0.12` | `gymColors.timerBackground` |
| `cardBorder` | `#3C3C43 / #545458` | `60,60,67,0.30 / 84,84,88,0.30` | `gymColors.cardBorder` |
| `cardShadow` | `#000000 / #000000` | `0,0,0,0.08 / 0,0,0,0.08` | `gymColors.cardShadow` |
| `wheelBackground` (`tertiarySystemBackground`) | `#FFFFFF / #2C2C2E` | `255,255,255,1 / 44,44,46,1` | `gymColors.wheelBackground` |
| `wheelStroke` (`systemGray4`) | `#D1D1D6 / #3A3A3C` | `209,209,214,1 / 58,58,60,1` | `gymColors.wheelStroke` |
| `wheelTick` (`systemGray2`) | `#AEAEB2 / #636366` | `174,174,178,1 / 99,99,102,1` | `gymColors.wheelTick` |
| `wheelIndicator` (`systemBlue`) | `#007AFF / #0A84FF` | `0,122,255,1 / 10,132,255,1` | `gymColors.wheelIndicator` |
| `wheelTrack` (`secondarySystemFill`) | `#787880 / #787880` | `120,120,128,0.16 / 120,120,128,0.32` | `gymColors.wheelTrack` |
| `wheelTrackStroke` (`systemGray4`) | `#D1D1D6 / #3A3A3C` | `209,209,214,1 / 58,58,60,1` | `gymColors.wheelTrackStroke` |
| `wheelFill` | `#007AFF / #0A84FF` | `0,122,255,0.20 / 10,132,255,0.20` | `gymColors.wheelFill` |
| `wheelThumbStroke` | `#FFFFFF / #FFFFFF` | `255,255,255,0.75 / 255,255,255,0.75` | `gymColors.wheelThumbStroke` |
| `progressBadgeUnlocked` | `#FFD60A / #FFD60A` | `255,214,10,1 / 255,214,10,1` | `gymColors.badgeUnlocked` |
| `progressCalendarWorkout` | `#007AFF / #0A84FF` | `0,122,255,1 / 10,132,255,1` | `gymColors.calendarWorkout` |
| `progressCalendarStreak` | `#FF9500 / #FF9F0A` | `255,149,0,1 / 255,159,10,1` | `gymColors.calendarStreak` |

## Compose Theme Mapping

```kotlin
@Immutable
object GymPalette {
    val Light = lightColorScheme(
        primary = Color(0xFF007AFF),
        onPrimary = Color(0xFFFFFFFF),
        background = Color(0xFFF2F2F7),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF000000),
        onSurfaceVariant = Color(0x993C3C43),
        outline = Color(0xFFC7C7CC),
        outlineVariant = Color(0x4A3C3C43)
    )

    val Dark = darkColorScheme(
        primary = Color(0xFF0A84FF),
        onPrimary = Color(0xFFFFFFFF),
        background = Color(0xFF000000),
        surface = Color(0xFF1C1C1E),
        onSurface = Color(0xFFFFFFFF),
        onSurfaceVariant = Color(0x99EBEBF5),
        outline = Color(0xFF48484A),
        outlineVariant = Color(0xA6545458)
    )
}

@Immutable
data class GymColors(
    val training: Color,
    val resting: Color,
    val completed: Color,
    val primaryButtonPressed: Color,
    val primaryButtonDisabled: Color,
    val timerBackground: Color,
    val cardShadow: Color
)
```

## Acceptance

| Criterio | Delta permitido | Estado |
| --- | --- | --- |
| Roles de color mapeados a Compose | Exacto | PASS/FAIL |
| Light/Dark parity iOS semantic | Delta E < 2 | PASS/FAIL |
| Alpha tokens (`separator`, fills, shadow) | Exacto | PASS/FAIL |
| Uso exclusivo de tokens (sin hardcode) | Exacto | PASS/FAIL |

## Pendiente

- Confirmar valores en dispositivo iOS final (build release) con captura comparativa de color picker para cerrar PASS definitivo.
