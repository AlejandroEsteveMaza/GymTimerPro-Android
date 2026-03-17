package com.alejandroestevemaza.gymtimerpro.core.format

import android.icu.util.LocaleData
import android.icu.util.ULocale
import com.alejandroestevemaza.gymtimerpro.core.model.WeightUnitPreference
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.round

enum class ResolvedWeightUnit {
    Kilograms,
    Pounds,
}

fun resolveWeightUnit(
    preference: WeightUnitPreference,
    locale: Locale = Locale.getDefault(),
): ResolvedWeightUnit = when (preference) {
    WeightUnitPreference.Kilograms -> ResolvedWeightUnit.Kilograms
    WeightUnitPreference.Pounds -> ResolvedWeightUnit.Pounds
    WeightUnitPreference.Automatic -> {
        when (LocaleData.getMeasurementSystem(ULocale.forLocale(locale))) {
            LocaleData.MeasurementSystem.US,
            LocaleData.MeasurementSystem.UK -> ResolvedWeightUnit.Pounds
            else -> ResolvedWeightUnit.Kilograms
        }
    }
}

fun formatWeight(
    weightKg: Double?,
    preference: WeightUnitPreference,
    locale: Locale = Locale.getDefault(),
): String? {
    if (weightKg == null) return null
    val resolvedUnit = resolveWeightUnit(preference, locale)
    val numericValue = when (resolvedUnit) {
        ResolvedWeightUnit.Kilograms -> weightKg
        ResolvedWeightUnit.Pounds -> weightKg * 2.2046226218
    }
    val formatter = NumberFormat.getNumberInstance(locale).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }
    val unitLabel = when (resolvedUnit) {
        ResolvedWeightUnit.Kilograms -> "kg"
        ResolvedWeightUnit.Pounds -> "lb"
    }
    return "${formatter.format(numericValue)} $unitLabel"
}

fun sanitizeWeightInput(raw: String): String {
    val filtered = buildString {
        var sawDecimalSeparator = false
        raw.forEach { char ->
            when {
                char.isDigit() -> append(char)
                (char == '.' || char == ',') && !sawDecimalSeparator -> {
                    append('.')
                    sawDecimalSeparator = true
                }
            }
        }
    }
    if (filtered.isEmpty()) return ""

    val parts = filtered.split('.', limit = 2)
    val integerPart = parts.firstOrNull().orEmpty().take(3)
    val decimalPart = parts.getOrNull(1)?.take(2).orEmpty()
    return if (filtered.contains('.') && integerPart.length < 3) {
        buildString {
            append(integerPart)
            append('.')
            append(decimalPart)
        }
    } else {
        integerPart
    }
}

fun parseWeightInputToKilograms(
    input: String,
    preference: WeightUnitPreference,
    locale: Locale = Locale.getDefault(),
): Double? {
    if (input.isBlank()) return null
    val normalized = input.replace(',', '.')
    val numericValue = normalized.toDoubleOrNull() ?: return Double.NaN
    if (numericValue < 0 || numericValue > 999) return Double.NaN
    val weightKg = when (resolveWeightUnit(preference, locale)) {
        ResolvedWeightUnit.Kilograms -> numericValue
        ResolvedWeightUnit.Pounds -> numericValue / 2.2046226218
    }
    return round(weightKg * 100) / 100
}
