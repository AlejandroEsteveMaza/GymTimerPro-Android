# Android Design Contract - Component: SectionCard

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/ContentView.swift (`SectionCard`)

## Purpose

Reusable card wrapper for major sections (`configuration`, `progress`) in Training screen.

## Visual Contract

| Propiedad | Valor |
| --- | --- |
| Padding | `16dp` |
| Corner radius | `20dp` |
| Border | `1dp`, `cardBorder` |
| Shadow | radius `12dp`, y `6dp`, alpha `0.08` |
| Header spacing | `10dp` |
| Content spacing | `16dp` (`Layout.cardSpacing`) |

## State Matrix

| Estado | Regla |
| --- | --- |
| `normal` | Fill `cardBackground`, border visible |
| `pressed` | No intrinsic style change; child controls handle press |
| `disabled` | Parent controls opacity (`0.55`) when applied by container |
| `loading` | Child placeholder allowed; shell unchanged |
| `error` | Child-provided error only; shell unchanged |
| `empty_state` | Child-specific empty content |
| `locked_free` | Not intrinsic |
| `locked_pro` | Not intrinsic |

## Compose Snippet

```kotlin
@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = GymTheme.colors.cardShadow)
            .background(GymTheme.colors.cardBackground, RoundedCornerShape(20.dp))
            .border(1.dp, GymTheme.colors.cardBorder, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            title()
            Spacer(Modifier.weight(1f))
            trailing?.invoke()
        }
        content()
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

- Añadir captura golden de `configuration` card y `progress` card para comparación directa.
