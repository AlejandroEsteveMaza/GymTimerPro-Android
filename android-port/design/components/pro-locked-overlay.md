# Android Design Contract - Component: ProLockedPreviewOverlay

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/MainTabView.swift (`ProLockedPreviewOverlay`)

## Purpose

Overlay used when a Pro tab is locked in free mode.

## Visual Contract

| Propiedad | Valor |
| --- | --- |
| Blur background radius | `10dp` equivalent |
| Saturation | `0.9` |
| Modal card max width | `360dp` |
| Modal card padding | `20dp` |
| Modal card corner radius | `18dp` |
| Modal card horizontal outer padding | `24dp` |
| Inner vertical spacing | `10dp` |
| CTA top padding | `8dp` |
| Icon size | `26sp` semibold |
| CTA shape radius | `12dp` |
| Shadow | radius `18dp`, y `10dp`, alpha `0.12` |

## State Matrix

| Estado | Regla |
| --- | --- |
| `normal` | Overlay hidden |
| `pressed` | CTA pressed feedback only |
| `disabled` | Underlay blocked when lock active |
| `loading` | Not applicable |
| `error` | Not intrinsic |
| `empty_state` | Not applicable |
| `locked_free` | Overlay shown with lock icon + copy + unlock action |
| `locked_pro` | Overlay removed |

## Compose Snippet

```kotlin
@Composable
fun ProLockedOverlay(
    title: String,
    message: String,
    actionText: String,
    onUnlock: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.matchParentSize().background(Color.Transparent))
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .widthIn(max = 360.dp),
            shape = RoundedCornerShape(18.dp),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // icon, title, subtitle, unlock button
            }
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

- Añadir golden screenshot del lock overlay en cada tab bloqueada (`routines`, `progress`, `settings`).
