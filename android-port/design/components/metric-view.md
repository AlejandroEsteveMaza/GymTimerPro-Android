# Android Design Contract - Component: MetricView

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/ContentView.swift (`MetricView`)

## Purpose

Small metric card used in training progress panel.

## Visual Contract

| Propiedad | Valor |
| --- | --- |
| Vertical spacing | `6dp` |
| Padding | `12dp` |
| Corner radius | `16dp` |
| Title style | `captionSemibold` |
| Value style | `numericMetric` + monospaced |

## State Matrix

| Estado | Regla |
| --- | --- |
| `normal` | Title + value visible |
| `pressed` | Not interactive |
| `disabled` | Inherits parent disabled opacity |
| `loading` | Placeholder optional |
| `error` | Not intrinsic |
| `empty_state` | Value fallback text possible |
| `locked_free` | Not intrinsic |
| `locked_pro` | Not intrinsic |

## Compose Snippet

```kotlin
@Composable
fun MetricView(title: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GymTheme.colors.metricBackground, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(title, style = GymType.captionSemibold, color = GymTheme.colors.textSecondary)
        Text(value, style = GymType.numericMetric, color = GymTheme.colors.textPrimary)
    }
}
```

## Acceptance

| Criterio | Delta permitido | Estado |
| --- | --- | --- |
| Layout | +/- 4dp | PASS/FAIL |
| Color | Delta E < 2 | PASS/FAIL |
| Tipografia | +/- 1sp | PASS/FAIL |
| Estados | Exactos | PASS/FAIL |

## Pendiente

- Añadir golden screenshot de métricas con valores de 1 y 2 dígitos.
