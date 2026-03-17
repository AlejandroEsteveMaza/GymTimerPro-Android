# Android Design Contract - Component: Classification Input Bar

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro/Routines/Classifications/RoutineClassificationPickerView.swift (`classificationInputBar`)

## Purpose

Bottom input/search bar for classification search + creation flow.

## Visual Contract

| Propiedad | Valor |
| --- | --- |
| Outer horizontal padding | `16dp` |
| Outer top/bottom padding | `8dp / 10dp` |
| Inner row spacing | `8dp` |
| Inner row padding | `12dp horizontal`, `8dp vertical` |
| Input container radius | `12dp` |
| Error text spacing | `6dp` |
| Error font | `captionRegular` |
| Error color | system red |

## State Matrix

| Estado | Regla |
| --- | --- |
| `normal` | Search icon + text field |
| `pressed` | Create button feedback |
| `disabled` | Create button disabled when name invalid/unavailable |
| `loading` | Not applicable |
| `error` | Duplicate classification message shown |
| `empty_state` | Works even with empty classification list |
| `locked_free` | Not intrinsic |
| `locked_pro` | Full functionality |

## Compose Snippet

```kotlin
@Composable
fun ClassificationInputBar(
    text: String,
    canCreate: Boolean,
    showDuplicateError: Boolean,
    onTextChange: (String) -> Unit,
    onCreate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, top = 8.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // icon + text input + optional create button
        }
        if (showDuplicateError) {
            Text(text = "Duplicate", style = MaterialTheme.typography.labelMedium, color = Color.Red)
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

- Añadir golden screenshot del input bar en estados `canCreate=true` y `duplicateError=true`.
