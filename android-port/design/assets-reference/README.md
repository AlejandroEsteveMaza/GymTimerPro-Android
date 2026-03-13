# Android Design Contract - Assets Reference

- Version: 1.0.0
- Date: 2026-03-13
- iOS References:
  - GymTimerPro screenshots from release-like build (light and dark where applicable)

## Purpose

This folder stores golden iOS screenshots used as visual source of truth for Android parity.

## Required Screenshot Set

| Screen | Minimum captures |
| --- | --- |
| training | `idle`, `resting`, `completed` |
| routines | `empty`, `catalog`, `editor` |
| progress | `month`, `quarter`, `selected day` |
| settings | `default`, `menu open` |
| paywall | `standard.pro`, `light.limit`, `detailed.pro` |

## Naming Convention

`<screen>__<state>__<theme>__<device>.png`

Examples:

- `training__idle__light__iphone17pro.png`
- `paywall__standard-pro__light__iphone17pro.png`
- `progress__month__dark__iphone17pro.png`

## Capture Rules

- Status bar visible.
- Same locale for baseline comparison (`en-US` first pass).
- Same dynamic type setting as default.
- Same zoom/display scale as release target.
- No debug overlays.

## Verification Workflow

1. Export iOS golden screenshots into this folder.
2. Generate Android screenshots with Paparazzi or Shot using matching states.
3. Compare using contract table:

| Criterio | Delta permitido | Estado |
| --- | --- | --- |
| Layout | +/- 4dp | PASS/FAIL |
| Color | Delta E < 2 | PASS/FAIL |
| Tipografía | +/- 1sp | PASS/FAIL |
| Estados | exactos | PASS/FAIL |

## Automation

Use the single script below to run snapshot and export/rename captures directly
into this folder:

```bash
cd /Users/alejandroestevemaza/Code/GymTimerPro
./scripts/fastlane/generate_assets_reference.sh --clean-assets
```

Related files used by the script:

- `scripts/fastlane/assets_reference_map.txt`
- `scripts/fastlane/assets_reference_required.txt`

If not all required captures are generated yet by UI tests, run:

```bash
./scripts/fastlane/generate_assets_reference.sh --clean-assets --allow-missing
```

## Paparazzi/Shot Reference

- Paparazzi: snapshot tests for composables and component states.
- Shot: on-device screenshot tests for end-to-end screen states.

## Pendiente

- Add first golden pack for all 5 screens in light theme.
- Add dark-theme pack for screens where dark parity is in scope.
