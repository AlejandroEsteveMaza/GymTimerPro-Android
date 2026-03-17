package com.alejandroestevemaza.gymtimerpro.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal val GymTypography = Typography(
    displayLarge = TextStyle(
        fontSize = 72.sp,
        lineHeight = 72.sp,
        fontWeight = FontWeight.Bold,
        fontFeatureSettings = "tnum",
    ),
    displaySmall = TextStyle(
        fontSize = 34.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight.Bold,
        fontFeatureSettings = "tnum",
    ),
    headlineSmall = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Bold,
    ),
    titleLarge = TextStyle(
        fontSize = 18.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Bold,
        fontFeatureSettings = "tnum",
    ),
    titleMedium = TextStyle(
        fontSize = 17.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    titleSmall = TextStyle(
        fontSize = 15.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    bodyMedium = TextStyle(
        fontSize = 15.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
    ),
    bodySmall = TextStyle(
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Normal,
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    labelSmall = TextStyle(
        fontSize = 11.sp,
        lineHeight = 13.sp,
        fontWeight = FontWeight.SemiBold,
    ),
)

@Immutable
data class GymType(
    val title2Bold: TextStyle,
    val headlineSemibold: TextStyle,
    val headlineRegular: TextStyle,
    val subheadlineRegular: TextStyle,
    val subheadlineSemibold: TextStyle,
    val footnoteRegular: TextStyle,
    val footnoteSemibold: TextStyle,
    val captionRegular: TextStyle,
    val captionSemibold: TextStyle,
    val caption2Semibold: TextStyle,
    val numericTimer: TextStyle,
    val numericMetric: TextStyle,
    val numericCta: TextStyle,
    val numericSecondary: TextStyle,
    val iconLabel: TextStyle,
    val valueLabel: TextStyle,
    val tinyFlame: TextStyle,
)

internal val GymTypeStyles = GymType(
    title2Bold = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold),
    headlineSemibold = TextStyle(fontSize = 17.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold),
    headlineRegular = TextStyle(fontSize = 17.sp, lineHeight = 22.sp, fontWeight = FontWeight.Normal),
    subheadlineRegular = TextStyle(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal),
    subheadlineSemibold = TextStyle(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    footnoteRegular = TextStyle(fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.Normal),
    footnoteSemibold = TextStyle(fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold),
    captionRegular = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Normal),
    captionSemibold = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold),
    caption2Semibold = TextStyle(fontSize = 11.sp, lineHeight = 13.sp, fontWeight = FontWeight.SemiBold),
    numericTimer = TextStyle(
        fontSize = 72.sp,
        lineHeight = 72.sp,
        fontWeight = FontWeight.Bold,
        fontFeatureSettings = "tnum",
        fontFamily = FontFamily.SansSerif,
    ),
    numericMetric = TextStyle(
        fontSize = 34.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight.Bold,
        fontFeatureSettings = "tnum",
        fontFamily = FontFamily.SansSerif,
    ),
    numericCta = TextStyle(
        fontSize = 18.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Bold,
        fontFeatureSettings = "tnum",
        fontFamily = FontFamily.SansSerif,
    ),
    numericSecondary = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Bold,
        fontFeatureSettings = "tnum",
        fontFamily = FontFamily.SansSerif,
    ),
    iconLabel = TextStyle(fontSize = 14.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold),
    valueLabel = TextStyle(fontSize = 16.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    tinyFlame = TextStyle(fontSize = 10.sp, lineHeight = 12.sp, fontWeight = FontWeight.SemiBold),
)
