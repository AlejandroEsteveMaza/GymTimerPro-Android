# Android Design Contract - Component: HorizontalWheelStepper

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/ContentView.swift (`HorizontalWheelStepper`)

## Purpose

Discrete horizontal stepper for numeric config values.

## Visual Contract

| Propiedad | Valor |
| --- | --- |
| Default size | `94dp x 32dp` |
| Corner radius | `10dp` |
| Track height | `12dp` |
| Track inset | `8dp` |
| Track vertical inset | `6dp` |
| Tick width | `2dp` |
| Tick spacing | `6dp` |
| Tick heights | `8dp` small / `14dp` large |
| Thumb size | `16dp x 16dp` |
| Hit target | `44dp` iOS -> `48dp` Android minimum |
| Step divisor | `6` |
| Step min width | `10dp` |

## State Matrix

| Estado | Regla |
| --- | --- |
| `normal` | Wheel interactive with drag and accessibility adjust |
| `pressed` | Thumb follows drag, haptic/selection feedback |
| `disabled` | Gesture ignored, visual remains token-consistent |
| `loading` | Not applicable |
| `error` | Not intrinsic; parent handles invalid range |
| `empty_state` | Not applicable |
| `locked_free` | Not intrinsic |
| `locked_pro` | Fully enabled |

## Compose Snippet

```kotlin
@Composable
fun HorizontalWheelStepper(
    value: Int,
    valueRange: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 94.dp, height = 32.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(GymTheme.colors.wheelBackground)
            .border(1.dp, GymTheme.colors.wheelStroke, RoundedCornerShape(10.dp))
            .pointerInput(value) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    // Keep discrete step behavior with min step width 10dp.
                }
            }
    )
}
```

## Acceptance

| Criterio | Delta permitido | Estado |
| --- | --- | --- |
| Layout | +/- 4dp | PASS/FAIL |
| Color | Delta E < 2 | PASS/FAIL |
| Tipografia | N/A | PASS/FAIL |
| Estados | Exactos | PASS/FAIL |

## Pendiente

- Añadir video/captura secuencial golden para validar ticks y thumb alignment durante drag.
