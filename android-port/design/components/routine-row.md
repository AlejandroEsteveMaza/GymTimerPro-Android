# Android Design Contract - Component: RoutineRow

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/Routines/RoutineRowView.swift

## Purpose

Row renderer for routine items in routine catalog/list.

## Visual Contract

| Propiedad | Valor |
| --- | --- |
| Layout | `Column`, alignment leading |
| Vertical spacing | `4dp` |
| Title style | `headlineRegular` |
| Subtitle style | `subheadlineRegular` |
| Title color | `textPrimary` |
| Subtitle color | `textSecondary` |

## State Matrix

| Estado | Regla |
| --- | --- |
| `normal` | Name + summary visible |
| `pressed` | List row press feedback |
| `disabled` | Not intrinsic |
| `loading` | Not applicable |
| `error` | Not intrinsic |
| `empty_state` | Not used (screen-level state handles empty) |
| `locked_free` | Not intrinsic |
| `locked_pro` | Full row shown |

## Compose Snippet

```kotlin
@Composable
fun RoutineRow(name: String, summary: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        Text(summary, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

- Añadir golden screenshot de una fila con nombre largo + truncado de resumen.
