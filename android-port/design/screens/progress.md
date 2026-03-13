# Android Design Contract - Screen: Progress

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/Progress/ProgramProgressView.swift

## Composable Hierarchy

`Scaffold -> Scroll content -> ChartsCard + CalendarCard + ActivityCard + BadgesCard`

## Layout Rules

| Elemento | Regla exacta |
| --- | --- |
| Root horizontal padding | `16dp` |
| Root vertical padding | `12dp` |
| Section spacing | `20dp` |
| Generic section card padding | `14dp` |
| Generic section card radius | `14dp` |
| Generic section card border | `1dp` quaternary |
| Chart height | `180dp` |
| Badges grid spacing | `10dp` |
| Badge card min height | `96dp` |
| Activity card min height | `78dp` |
| Activity card padding | `10dp` |
| Calendar cell circle size | `20dp` |
| Calendar row min height | `22dp` |
| Calendar grid gap | `6dp` |

## Typography and Color Usage

| Elemento | Typography token | Color token |
| --- | --- | --- |
| Card titles | `headlineRegular` | `textPrimary` |
| Secondary info | `captionRegular` / `subheadlineRegular` | `textSecondary` |
| Calendar symbols | `caption2Semibold` | `textSecondary` |
| Workout day icon text | `caption2Semibold` | white over blue fill |
| Streak indicator | `tinyFlame` / custom 14 semibold rounded | `progressCalendarStreak` |

## Responsive Behavior

| Size class | Regla |
| --- | --- |
| compact (`<600dp`) | Vertical stack, no side-by-side cards |
| medium (`600-840dp`) | Keep stack; badge grid remains 2 columns |
| expanded (`>840dp`) | Allow 2-column top area only if delta with iOS remains within tolerance |

## WindowInsets Rules

| Inset | Regla |
| --- | --- |
| statusBar | Content begins below status bar |
| navigationBar | Last section remains fully visible above nav bar |
| IME | Day detail sheet list remains usable with keyboard hidden (read-only flow) |

## State Rules

| Estado | Regla visual |
| --- | --- |
| `normal` | Data cards + charts visible |
| `pressed` | Day cell press feedback only on selectable days |
| `disabled` | Non-selectable calendar days disabled |
| `loading` | During reload task, preserve previous content (no hard blank) |
| `error` | No explicit error UI in current iOS contract |
| `empty_state` | Activity section text fallback |
| `locked_free` | Progress tab locked at shell level |
| `locked_pro` | Full progress content visible |

## Key Compose Snippet

```kotlin
@Composable
fun ProgressScreen(state: ProgressUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { ProgressChartsCard(state) }
        item { ProgressCalendarCard(state) }
        item { ProgressActivityCard(state) }
        item { ProgressBadgesCard(state) }
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

- Añadir 3 golden screenshots iOS (`month`, `quarter`, `selected day sheet`).
