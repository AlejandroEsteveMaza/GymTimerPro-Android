# Android Design Contract - Screen: Routines

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/Routines/RoutinesListView.swift
  - GymTimerPro/Routines/RoutineCatalogListView.swift
  - GymTimerPro/Routines/RoutineEditorView.swift
  - GymTimerPro/Routines/RoutineRowView.swift

## Composable Hierarchy

`Scaffold -> TopAppBar -> List(content/search/sections) -> Row actions -> Editor Sheet`

## Layout Rules

| Elemento | Regla exacta |
| --- | --- |
| Empty top padding | `32dp` |
| List style | `InsetGrouped` equivalent in Compose (`LazyColumn` + section cards) |
| Row vertical padding (classification picker rows) | `6dp` |
| Input bar horizontal padding | `16dp` |
| Input bar top/bottom | `8dp / 10dp` |
| Input bar inner padding | `12dp horizontal`, `8dp vertical` |
| Input bar corner radius | `12dp` |
| Editor min tap height | `44dp` iOS, enforce `48dp` Android |
| Editor popover size (regular width) | `minWidth 260dp`, `minHeight 320dp` |

## Typography and Color Usage

| Elemento | Typography token | Color token |
| --- | --- | --- |
| Routine title | `headlineRegular` | `textPrimary` |
| Routine summary | `subheadlineRegular` | `textSecondary` |
| Name counter | `captionSemibold` | `textSecondary` |
| Weight value | `numericCTA` + monospaced | `textPrimary` |
| Error text | `captionRegular` | system red (`#FF3B30 / #FF453A`) |

## Responsive Behavior

| Size class | Regla |
| --- | --- |
| compact (`<600dp`) | Full-screen list + bottom sheet for classification picker |
| medium (`600-840dp`) | Same list density; editor popover allowed |
| expanded (`>840dp`) | Keep single-pane list/editor flow unless explicit split mode is approved |

## WindowInsets Rules

| Inset | Regla |
| --- | --- |
| statusBar | Toolbar starts below status bar |
| navigationBar | Safe area inset for bottom classification input bar |
| IME | Search/create input bar must remain visible while keyboard is shown |

## State Rules

| Estado | Regla visual |
| --- | --- |
| `normal` | Full list interaction |
| `pressed` | Row action feedback, no layout shift |
| `disabled` | Save/apply buttons disabled by form validity |
| `loading` | Not used; data local and immediate |
| `error` | Inline duplicate/error captions in forms |
| `empty_state` | `ContentUnavailableView` equivalent with icon+message |
| `locked_free` | List screen itself is Pro-locked at tab level |
| `locked_pro` | Full content visible |

## Key Compose Snippet

```kotlin
@Composable
fun RoutinesScreen(state: RoutinesUiState, onAddRoutine: () -> Unit) {
    Scaffold(
        topBar = { RoutinesTopBar(onAddRoutine = onAddRoutine) }
    ) { innerPadding ->
        if (state.isEmpty) {
            EmptyRoutinesView(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(top = 32.dp)
            )
        } else {
            RoutineCatalogList(
                modifier = Modifier.padding(innerPadding),
                sections = state.sections
            )
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

- Añadir 3 golden screenshots iOS (`empty`, `catalog`, `editor`).
