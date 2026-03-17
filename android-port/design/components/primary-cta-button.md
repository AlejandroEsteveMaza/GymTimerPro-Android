# Android Design Contract - Component: Primary CTA Button

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/ContentView.swift (`PrimaryButtonStyle`)

## Purpose

Primary action button used in training controls.

## Visual Contract

| Propiedad | Valor |
| --- | --- |
| Height | `80dp` |
| Corner radius | `16dp` |
| Label style | `18sp`, bold rounded |
| Press scale | `0.98` |
| Shadow enabled | radius `10dp`, y `6dp`, alpha `0.25` |
| Background normal | `primaryButton` |
| Background pressed | `primaryButtonPressed` |
| Background disabled | `primaryButtonDisabled` |

## State Matrix

| Estado | Regla |
| --- | --- |
| `normal` | Enabled colors + shadow |
| `pressed` | scale `0.98`, pressed color |
| `disabled` | disabled color, no shadow |
| `loading` | Optional progress overlay, interactions blocked |
| `error` | No intrinsic red state in iOS contract |
| `empty_state` | Not applicable |
| `locked_free` | Not intrinsic |
| `locked_pro` | Not intrinsic |

## Compose Snippet

```kotlin
@Composable
fun PrimaryCtaButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GymTheme.colors.primaryButton,
            disabledContainerColor = GymTheme.colors.primaryButtonDisabled
        )
    ) {
        Text(text = text, style = GymType.numericCta)
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

- Añadir capturas golden de `normal`, `pressed`, `disabled`.
