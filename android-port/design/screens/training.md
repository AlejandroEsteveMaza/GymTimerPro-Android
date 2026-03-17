# Android Design Contract - Screen: Training

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/ContentView.swift
  - GymTimerPro/NumericConfigRow.swift

## Composable Hierarchy

`Scaffold -> Content(Scroll/LazyColumn) -> ConfigurationCard + ProgressCard -> BottomBar(Primary CTA)`

## Layout Rules

| Elemento | Regla exacta |
| --- | --- |
| Root background | `Theme.background` token |
| Main horizontal padding | `20dp` |
| Top padding | `12dp` |
| Section spacing | `20dp` |
| Bottom scroll padding | `104dp` (`24 + CTA 80`) |
| Card padding | `16dp` |
| Card corner radius | `20dp` |
| Row spacing inside config card | `12dp` |
| Metrics gap | `12dp` |
| Metric card padding | `12dp` |
| Metric card corner radius | `16dp` |
| Timer card padding | `16dp` |
| Bottom controls vertical padding | `12dp` |
| Primary CTA height | `80dp` |
| Primary CTA corner radius | `16dp` |

## Typography and Color Usage

| Elemento | Typography token | Color token |
| --- | --- | --- |
| Section title | `headlineSemibold` | `textPrimary` |
| Config row title | `valueLabel` | `textPrimary` |
| Secondary labels | `subheadlineRegular` | `textSecondary` |
| Metric value | `numericMetric` + monospaced | `textPrimary` |
| Timer value | `numericTimer` + monospaced | `resting` |
| Primary CTA label | `numericCTA` | `primaryButtonText` |

## Responsive Behavior

| Size class | Regla |
| --- | --- |
| compact (`<600dp`) | Single column, fixed bottom CTA bar |
| medium (`600-840dp`) | Single column; popover for discrete value editor (`minWidth=260dp`, `minHeight=320dp`) |
| expanded (`>840dp`) | Keep single column contract unless explicit split-screen decision is approved |

## WindowInsets Rules

| Inset | Regla |
| --- | --- |
| statusBar | Apply top inset before content starts |
| navigationBar | Keep bottom controls above nav bar |
| IME | Editor popover/sheet must avoid overlap; no overlap on wheel stepper controls |

## State Rules

| Estado | Regla visual |
| --- | --- |
| `normal` | Cards fully enabled, opacity `1.0` |
| `pressed` | Primary CTA scale `0.98`, pressed color alpha `0.85` |
| `disabled` | Config section opacity `0.55` when timer running/completed; CTA disabled color |
| `loading` | Not used in training root; inherited from paywall/billing only |
| `error` | Inline alert only in paywall flow; training screen itself no modal error |
| `empty_state` | Not applicable |
| `locked_free` | Pro-only row shown, triggers paywall |
| `locked_pro` | Not applicable (training always accessible) |

## Key Compose Snippet

```kotlin
@Composable
fun TrainingScreen(
    state: TrainingUiState,
    onStartRest: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            TrainingControlsBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                buttonHeight = 80.dp,
                onStartRest = onStartRest,
                enabled = !state.isResting && !state.completed
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { TrainingConfigurationCard(state) }
            item { TrainingProgressCard(state) }
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

- Añadir 3 golden screenshots iOS (`idle`, `resting`, `completed`) para cierre de paridad visual.
