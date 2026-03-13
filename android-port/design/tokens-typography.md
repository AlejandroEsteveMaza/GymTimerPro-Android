# Android Design Contract - Typography Tokens

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/ContentView.swift
  - GymTimerPro/PaywallView.swift
  - GymTimerPro/Progress/ProgramProgressView.swift
  - GymTimerPro/Routines/RoutineEditorView.swift

## Typography Rules

- Styles must be implemented as tokens in `MaterialTheme.typography` plus optional `GymType` extension for custom numeric styles.
- Monospaced numeric usage is mandatory for timer and key counters.
- No inline `fontSize` in final screen composables except explicit exceptions documented below.

## Typography Token Table

| Estilo | Size (sp) | Weight | LineHeight (sp) | LetterSpacing (sp) | TextStyle Compose |
| --- | --- | --- | --- | --- | --- |
| `title2Bold` (`.title2.bold`) | 22 | 700 | 28 | 0.0 | `headlineSmall` override |
| `headlineSemibold` (`.headline.weight(.semibold)`) | 17 | 600 | 22 | 0.0 | `titleMedium` |
| `headlineRegular` (`.headline`) | 17 | 400 | 22 | 0.0 | `titleMedium` override |
| `subheadlineRegular` (`.subheadline`) | 15 | 400 | 20 | 0.0 | `bodyMedium` |
| `subheadlineSemibold` (`.subheadline.weight(.semibold)`) | 15 | 600 | 20 | 0.0 | `bodyMedium` override |
| `footnoteRegular` (`.footnote`) | 13 | 400 | 18 | 0.0 | `bodySmall` |
| `footnoteSemibold` (`.footnote.weight(.semibold)`) | 13 | 600 | 18 | 0.0 | `bodySmall` override |
| `captionSemibold` (`.caption.weight(.semibold)`) | 12 | 600 | 16 | 0.0 | `labelMedium` |
| `captionRegular` (`.caption`) | 12 | 400 | 16 | 0.0 | `labelMedium` override |
| `caption2Semibold` (`.caption2.weight(.semibold)`) | 11 | 600 | 13 | 0.0 | `labelSmall` |
| `numericTimer` (`size 72, bold, rounded`) | 72 | 700 | 72 | 0.0 | `displayLarge` custom |
| `numericMetric` (`size 34, bold, rounded`) | 34 | 700 | 40 | 0.0 | `displaySmall` custom |
| `numericCTA` (`size 18, bold, rounded`) | 18 | 700 | 22 | 0.0 | `titleLarge` custom |
| `numericSecondary` (`size 22, bold, rounded`) | 22 | 700 | 28 | 0.0 | `headlineSmall` custom |
| `iconLabel` (`size 14, semibold`) | 14 | 600 | 18 | 0.0 | `labelLarge` custom |
| `valueLabel` (`size 16, semibold`) | 16 | 600 | 20 | 0.0 | `bodyLarge` custom |
| `microDot` (`size 6, semibold`) | 6 | 600 | 8 | 0.0 | custom |
| `tinyFlame` (`size 10, semibold`) | 10 | 600 | 12 | 0.0 | custom |

## iOS UIFont to Compose Mapping

| iOS Usage | Compose API |
| --- | --- |
| `.font(.system(size: 72, weight: .bold, design: .rounded)).monospacedDigit()` | `TextStyle(fontSize = 72.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif, fontFeatureSettings = "tnum")` |
| `.font(.system(size: 34, weight: .bold, design: .rounded)).monospacedDigit()` | `TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum")` |
| `.font(.headline.weight(.semibold))` | `MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)` |
| `.font(.caption2.weight(.semibold))` | `MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)` |

## Compose Typography Snippet

```kotlin
val GymTypography = Typography(
    headlineSmall = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 17.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold),
    bodyMedium = TextStyle(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal),
    bodySmall = TextStyle(fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.Normal),
    labelMedium = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold),
    labelSmall = TextStyle(fontSize = 11.sp, lineHeight = 13.sp, fontWeight = FontWeight.SemiBold)
)

val NumericTimer = TextStyle(
    fontSize = 72.sp,
    lineHeight = 72.sp,
    fontWeight = FontWeight.Bold,
    fontFeatureSettings = "tnum"
)
```

## Acceptance

| Criterio | Delta permitido | Estado |
| --- | --- | --- |
| Tamaños tipográficos | +/- 1sp | PASS/FAIL |
| Pesos tipográficos | Exacto | PASS/FAIL |
| Line-height | +/- 1sp | PASS/FAIL |
| Números con `tnum` en timer/métricas | Exacto | PASS/FAIL |

## Pendiente

- Confirmar la familia tipográfica final (SF equivalent en Android) tras pruebas visuales en Pixel + Samsung para ajuste fino sin romper el delta permitido.
