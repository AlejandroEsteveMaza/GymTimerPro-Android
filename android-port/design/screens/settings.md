# Android Design Contract - Screen: Settings

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/Settings/SettingsRootView.swift

## Composable Hierarchy

`Scaffold -> TopAppBar -> Grouped settings list -> Sections with menu/segmented pickers`

## Layout Rules

| Elemento | Regla exacta |
| --- | --- |
| Root container | Grouped list style equivalent |
| Sections | 6 sections in fixed order |
| Picker styles | menu, segmented, menu, segmented, menu, navigation link |
| Row touch target | minimum `48dp` on Android |
| Navigation title | `tab.settings` |

## Typography and Color Usage

| Elemento | Typography token | Color token |
| --- | --- | --- |
| Section headers | `captionRegular` / Material section label | `textSecondary` |
| Row labels | `subheadlineRegular` | `textPrimary` |
| Picker labels | `subheadlineRegular` | `textPrimary` |

## Responsive Behavior

| Size class | Regla |
| --- | --- |
| compact (`<600dp`) | Single list column |
| medium (`600-840dp`) | Single list column, larger picker touch targets |
| expanded (`>840dp`) | Keep single pane until dedicated settings split contract is defined |

## WindowInsets Rules

| Inset | Regla |
| --- | --- |
| statusBar | Top app bar below status bar |
| navigationBar | Last section visible above nav bar |
| IME | Not expected in current settings screen |

## State Rules

| Estado | Regla visual |
| --- | --- |
| `normal` | All sections visible |
| `pressed` | Standard row press feedback |
| `disabled` | No disabled states defined in current iOS settings |
| `loading` | Not applicable |
| `error` | Not applicable |
| `empty_state` | Not applicable |
| `locked_free` | Settings tab locked at shell level |
| `locked_pro` | Full settings content visible |

## Key Compose Snippet

```kotlin
@Composable
fun SettingsScreen(state: SettingsUiState, onManageClassifications: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        item { WeightUnitSection(state) }
        item { TimerDisplaySection(state) }
        item { MaxSetsSection(state) }
        item { RestIncrementSection(state) }
        item { EnergyModeSection(state) }
        item { ManageClassificationsRow(onClick = onManageClassifications) }
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

- Añadir 2 golden screenshots iOS (`default`, `menu open`).
