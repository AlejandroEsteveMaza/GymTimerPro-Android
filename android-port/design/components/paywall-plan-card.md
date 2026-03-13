# Android Design Contract - Component: Paywall Plan Card

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/PaywallView.swift (`planCard`)

## Purpose

Selectable plan card used inside paywall plans section.

## Visual Contract

| Propiedad | Valor |
| --- | --- |
| Card padding | `12dp` |
| Corner radius | `14dp` |
| Inner vertical spacing | `8dp` |
| Row spacing | `10dp` |
| Selected border | `1.5dp`, `accentColor` |
| Unselected border | transparent |
| Plan badge padding | `8dp horizontal`, `3dp vertical` |
| Plan badge style | `caption2Semibold` |

## State Matrix

| Estado | Regla |
| --- | --- |
| `normal` | Unselected circle icon, no border |
| `pressed` | Touch feedback only |
| `disabled` | Inherited from paywall processing |
| `loading` | Replaced by loading text row when products absent |
| `error` | Not intrinsic; error appears in alert |
| `empty_state` | No card rendered when products missing |
| `locked_free` | Not intrinsic |
| `locked_pro` | Not intrinsic |

## Compose Snippet

```kotlin
@Composable
fun PaywallPlanCard(
    title: String,
    price: String,
    selected: Boolean,
    badge: String?,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 0.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
        ),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            // leading selector + text + optional badge + trailing price
        }
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

- Añadir capturas golden de card `selected` y `unselected` en light/dark.
