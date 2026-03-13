# Android Design Contract - Component: Progress Calendar Day Cell

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/Progress/ProgramProgressView.swift (`MonthlyCompletionCalendarView.dayCell`)

## Purpose

Interactive cell used in monthly completion calendar.

## Visual Contract

| Propiedad | Valor |
| --- | --- |
| Current month cell size | `20dp x 20dp` |
| Row min height | `22dp` |
| Border width | `1.5dp` |
| Font | `caption2Semibold` |
| Workout icon | `dumbbell.fill` |
| Non-current month color | `textSecondary` |

## Color Logic Rules

| Condition | Fill | Border |
| --- | --- | --- |
| has workout | blue (`primary`) | orange if today/past workout, else gray logic |
| today no workout | transparent | orange |
| past no workout | gray alpha `0.22` | gray alpha `0.65` |
| future no workout | transparent | gray alpha `0.45` |

## State Matrix

| Estado | Regla |
| --- | --- |
| `normal` | Cell rendered by date status |
| `pressed` | Only enabled when current month + has workout |
| `disabled` | No click if no workout or non-current month |
| `loading` | Not applicable |
| `error` | Not intrinsic |
| `empty_state` | No workouts -> plain day number |
| `locked_free` | Not intrinsic |
| `locked_pro` | Fully enabled in progress tab |

## Compose Snippet

```kotlin
@Composable
fun ProgressDayCell(
    isCurrentMonth: Boolean,
    isToday: Boolean,
    isPast: Boolean,
    workoutCount: Int,
    onClick: (() -> Unit)?
) {
    val enabled = isCurrentMonth && workoutCount > 0 && onClick != null
    TextButton(
        onClick = { onClick?.invoke() },
        enabled = enabled,
        modifier = Modifier.heightIn(min = 22.dp)
    ) {
        // Draw circle fill/stroke and icon/number with 20dp size contract.
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

- Añadir golden screenshot de una semana con mezcla de estados (`today`, `past+workout`, `past empty`, `future`).
