# Android Design Contract - System

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/ContentView.swift
  - GymTimerPro/PaywallView.swift
  - GymTimerPro/Progress/ProgramProgressView.swift
  - GymTimerPro/Routines/RoutinesListView.swift
  - GymTimerPro/Settings/SettingsRootView.swift

## Scope

This contract defines visual parity rules for Android Compose implementation against current iOS SwiftUI behavior.

Design implementation order is mandatory:

1. Tokens
2. Screen layout
3. Reusable components
4. Golden screenshot verification

## Global Principles

| Principle | Rule | Verification |
| --- | --- | --- |
| Token-first | No hardcoded visual values in screen composables | Static scan and code review |
| Layout determinism | Use fixed dp/sp values documented in this contract | Snapshot comparison |
| State completeness | Every reusable component defines `normal`, `pressed`, `disabled`, `loading`, `error` | Component test matrix |
| Inset compliance | All primary screens apply status/navigation/IME insets explicitly | Runtime layout test |
| Accessibility floor | Minimum touch target `48dp` for interactive controls on Android | UI test |

## iOS to Compose Mapping

| iOS Primitive | Android Compose Primitive | Rule |
| --- | --- | --- |
| `ScrollView + VStack` | `LazyColumn` or `Column` in `Scaffold` content slot | Preserve top/bottom paddings and section spacing |
| `safeAreaInset(edge: .bottom)` | `WindowInsets.navigationBars` + bottom container in `Scaffold.bottomBar` | Keep fixed control bar behavior |
| `RoundedRectangle(cornerRadius: X)` | `RoundedCornerShape(X.dp)` | Radius parity required |
| `Color(uiColor: ...)` semantic colors | `MaterialTheme.colorScheme` role mapping in theme | Use token table in `tokens-color.md` |
| `.font(...)` | `MaterialTheme.typography` mapped text styles | Use token table in `tokens-typography.md` |
| `.sheet` / `.popover` | `ModalBottomSheet` / `Dialog` / adaptive sheet | Keep behavioral trigger parity |

## Global Compose Contract Snippet

```kotlin
@Composable
fun GymTimerProAppShell(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    GymTimerProTheme(darkTheme = darkTheme) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                content()
            }
        }
    }
}
```

## Global Acceptance Checklist

| Criterio | Delta permitido | Estado |
| --- | --- | --- |
| Layout global (espaciados y radios) | +/- 4dp | PASS/FAIL |
| Color roles en tema | Delta E < 2 | PASS/FAIL |
| Tipografía base | +/- 1sp | PASS/FAIL |
| Estados interactivos base | Exactos | PASS/FAIL |
| Insets status/navigation/IME | Exactos | PASS/FAIL |

## Pendiente

- Añadir evidencia de golden screenshots por pantalla para cerrar PASS/FAIL global.
