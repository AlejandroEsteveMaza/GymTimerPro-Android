# Android Design Contract - Screen: Paywall

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/PaywallView.swift
  - GymTimerPro/PaywallContent.swift

## Composable Hierarchy

`Scaffold(Modal) -> TopBar(close) -> Scroll content -> Header -> Benefits -> Optional Include -> Plans -> CTA -> Legal -> Links`

## Layout Rules

| Elemento | Regla exacta |
| --- | --- |
| Content horizontal padding | `20dp` |
| Content top/bottom padding | `12dp` / `24dp` |
| Section spacing | `20dp` |
| Section card padding | `16dp` |
| Section card corner radius | `20dp` |
| Plan card padding | `12dp` |
| Plan card corner radius | `14dp` |
| Plan selected border | `1.5dp` |
| Trial banner padding | `12dp horizontal`, `8dp vertical` |
| Header badge padding | `10dp horizontal`, `4dp vertical` |
| Plan badge padding | `8dp horizontal`, `3dp vertical` |
| Links horizontal gap | `16dp` |

## Typography and Color Usage

| Elemento | Typography token | Color token |
| --- | --- | --- |
| Header badge | `captionSemibold` | `primary` |
| Title | `title2Bold` | `textPrimary` |
| Subtitle | `subheadlineRegular` | `textSecondary` |
| Section title | `subheadlineSemibold` | `textSecondary` |
| Plan title/price | `headlineRegular` | `textPrimary` |
| Primary CTA | `headlineRegular` | `onPrimary` |
| Trust/links/legal | `footnoteRegular` / `captionRegular` / `footnoteSemibold` | `textSecondary` |

## Variant Rules

| Variant | Rule |
| --- | --- |
| `entryPoint` | `proModule` or `dailyLimitDuringWorkout` |
| `infoLevel` | `light`, `standard`, `detailed` |
| Copy matrix | 6 combinations required (`light/standard/detailed` x `pro/limit`) |
| Include section | Only for `detailed.*` variants |
| Secondary CTA | Present only in variants where `PaywallCopy` defines it |

## Responsive Behavior

| Size class | Regla |
| --- | --- |
| compact (`<600dp`) | Full-screen modal scroll |
| medium (`600-840dp`) | Same structure, larger horizontal margin optional |
| expanded (`>840dp`) | Center content max width `560dp` while preserving internal measurements |

## WindowInsets Rules

| Inset | Regla |
| --- | --- |
| statusBar | Toolbar close button must remain below status bar |
| navigationBar | Legal/links remain accessible above nav bar |
| IME | Not expected in paywall main flow |

## State Rules

| Estado | Regla visual |
| --- | --- |
| `normal` | Full interactive paywall |
| `pressed` | Button and selectable card press feedback |
| `disabled` | `isProcessing=true` disables dismiss, close, purchase and links |
| `loading` | Product list loading row (`paywall.price.loading`) |
| `error` | Error alert for purchase/restore failures except userCancelled |
| `empty_state` | Product list can be empty while loading |
| `locked_free` | N/A (this is the unlock screen) |
| `locked_pro` | Auto-dismiss when `isPro=true` |

## Key Compose Snippet

```kotlin
@Composable
fun PaywallScreen(state: PaywallUiState, onBuy: () -> Unit, onRestore: () -> Unit) {
    Scaffold(
        topBar = { PaywallTopBar(onClose = state.onClose, enabled = !state.isProcessing) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { PaywallHeaderCard(state) }
            item { PaywallBenefitsCard(state) }
            if (state.includeSection != null) item { PaywallIncludeCard(state.includeSection) }
            item { PaywallPlansCard(state) }
            item { PaywallCtaSection(state, onBuy) }
            item { PaywallLegalSection(state) }
            item { PaywallLinksSection(state, onRestore) }
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

- Añadir 3 golden screenshots iOS por variante activa (`standard.pro`, `light.limit`, `detailed.pro`).
