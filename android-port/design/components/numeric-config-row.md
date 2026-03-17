# Android Design Contract - Component: NumericConfigRow

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/NumericConfigRow.swift
  - GymTimerPro/ContentView.swift (`ConfigRow`, `ConfigValueEditorButton`)

## Purpose

Row to edit numeric config values with dual interaction:

- editor button/value tap
- horizontal wheel stepper

## Visual Contract

| Propiedad | Valor |
| --- | --- |
| Row spacing | `12dp` |
| Min height | `44dp` iOS, enforce `48dp` Android |
| Icon frame | `28dp x 28dp` |
| Icon corner radius | `8dp` |
| Icon font size | `14sp`, semibold |
| Label size | `16sp`, semibold |
| Value size | `18sp`, bold rounded + monospaced |

## State Matrix

| Estado | Regla |
| --- | --- |
| `normal` | Icon + label + editor + wheel visible |
| `pressed` | Value/editor touch feedback |
| `disabled` | Inherits disabled alpha/tint from parent section |
| `loading` | Not applicable |
| `error` | Value validation error handled by parent form |
| `empty_state` | Not applicable |
| `locked_free` | Optional paywall trigger in hosting row |
| `locked_pro` | Full editing enabled |

## Compose Snippet

```kotlin
@Composable
fun NumericConfigRow(
    icon: ImageVector,
    title: String,
    valueText: String,
    enabled: Boolean,
    onOpenEditor: () -> Unit,
    wheel: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ConfigLabel(icon = icon, title = title, valueText = valueText, onOpenEditor = onOpenEditor)
        wheel()
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

- Añadir captura golden del row en estados `enabled` y `disabled`.
