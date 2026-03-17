#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ASSETS_DIR="$ROOT_DIR/android-port/design/assets-reference"

required=(
  "training__idle__"
  "training__resting__"
  "training__completed__"
  "routines__empty__"
  "routines__catalog__"
  "routines__editor__"
  "progress__month__"
  "progress__quarter__"
  "progress__selected-day__"
  "settings__default__"
  "settings__menu-open__"
  "paywall__standard-pro__"
  "paywall__light-limit__"
  "paywall__detailed-pro__"
)

missing=()
for pattern in "${required[@]}"; do
  if ! find "$ASSETS_DIR" -maxdepth 1 -type f -name "${pattern}*.png" | grep -q .; then
    missing+=("$pattern")
  fi
done

if [[ ${#missing[@]} -eq 0 ]]; then
  echo "PASS: all required iOS golden screenshot patterns are present."
  exit 0
fi

echo "PENDING: missing iOS golden screenshot patterns:"
for pattern in "${missing[@]}"; do
  echo "- ${pattern}*.png"
done
exit 2
