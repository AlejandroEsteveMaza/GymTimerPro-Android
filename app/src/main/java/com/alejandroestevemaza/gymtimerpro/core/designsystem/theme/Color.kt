package com.alejandroestevemaza.gymtimerpro.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

private val LightBackground = Color(0xFFF2F2F7)
private val DarkBackground = Color(0xFF000000)
private val LightCardBackground = Color(0xFFFFFFFF)
private val DarkCardBackground = Color(0xFF1C1C1E)
private val LightControlsBackground = Color(0xFFFFFFFF)
private val DarkControlsBackground = Color(0xFF000000)
private val LightTextPrimary = Color(0xFF000000)
private val DarkTextPrimary = Color(0xFFFFFFFF)
private val LightTextSecondary = Color(0x993C3C43)
private val DarkTextSecondary = Color(0x99EBEBF5)
private val LightDivider = Color(0x4A3C3C43)
private val DarkDivider = Color(0xA6545458)
private val LightIconTint = Color(0xFF007AFF)
private val DarkIconTint = Color(0xFF0A84FF)
private val LightIconBackground = Color(0x1F767680)
private val DarkIconBackground = Color(0x3D767680)
private val LightSecondaryButtonFill = Color(0xFFF2F2F7)
private val DarkSecondaryButtonFill = Color(0xFF1C1C1E)
private val LightSecondaryButtonBorder = Color(0xFFC7C7CC)
private val DarkSecondaryButtonBorder = Color(0xFF48484A)
private val LightMetricBackground = Color(0xFFFFFFFF)
private val DarkMetricBackground = Color(0xFF2C2C2E)
private val LightPrimaryDisabled = Color(0xFFD1D1D6)
private val DarkPrimaryDisabled = Color(0xFF3A3A3C)
private val LightCardBorder = Color(0x4C3C3C43)
private val DarkCardBorder = Color(0x4C545458)
private val CardShadow = Color(0x14000000)

private val TrainingLight = Color(0xFF5AC8FA)
private val TrainingDark = Color(0xFF64D2FF)
private val RestingLight = Color(0xFFFF9500)
private val RestingDark = Color(0xFFFF9F0A)
private val CompletedLight = Color(0xFF34C759)
private val CompletedDark = Color(0xFF30D158)
private val PrimaryPressedLight = Color(0xD9007AFF)
private val PrimaryPressedDark = Color(0xD90A84FF)
private val TimerBackgroundLight = Color(0x1FFF9500)
private val TimerBackgroundDark = Color(0x1FFF9F0A)
private val WheelBackgroundLight = Color(0xFFFFFFFF)
private val WheelBackgroundDark = Color(0xFF2C2C2E)
private val WheelStrokeLight = Color(0xFFD1D1D6)
private val WheelStrokeDark = Color(0xFF3A3A3C)
private val WheelTickLight = Color(0xFFAEAEB2)
private val WheelTickDark = Color(0xFF636366)
private val WheelTrackLight = Color(0x29787880)
private val WheelTrackDark = Color(0x52787880)
private val WheelTrackStrokeLight = Color(0xFFD1D1D6)
private val WheelTrackStrokeDark = Color(0xFF3A3A3C)
private val WheelFillLight = Color(0x33007AFF)
private val WheelFillDark = Color(0x330A84FF)
private val WheelThumbStroke = Color(0xBFFFFFFF)
private val BadgeUnlocked = Color(0xFFFFD60A)
private val CalendarWorkoutLight = Color(0xFF007AFF)
private val CalendarWorkoutDark = Color(0xFF0A84FF)
private val CalendarStreakLight = Color(0xFFFF9500)
private val CalendarStreakDark = Color(0xFFFF9F0A)
private val ErrorLight = Color(0xFFFF3B30)
private val ErrorDark = Color(0xFFFF453A)

internal val GymLightColorScheme: ColorScheme = lightColorScheme(
    primary = LightIconTint,
    onPrimary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightCardBackground,
    onSurface = LightTextPrimary,
    surfaceVariant = LightMetricBackground,
    onSurfaceVariant = LightTextSecondary,
    outline = LightSecondaryButtonBorder,
    outlineVariant = LightDivider,
    surfaceContainer = LightSecondaryButtonFill,
    surfaceContainerLow = LightMetricBackground,
    surfaceContainerHighest = LightIconBackground,
    surfaceBright = LightControlsBackground,
    error = ErrorLight,
)

internal val GymDarkColorScheme: ColorScheme = darkColorScheme(
    primary = DarkIconTint,
    onPrimary = Color.White,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkCardBackground,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkMetricBackground,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkSecondaryButtonBorder,
    outlineVariant = DarkDivider,
    surfaceContainer = DarkSecondaryButtonFill,
    surfaceContainerLow = DarkMetricBackground,
    surfaceContainerHighest = DarkIconBackground,
    surfaceBright = DarkControlsBackground,
    error = ErrorDark,
)

@Immutable
data class GymColors(
    val cardBackground: Color,
    val controlsBackground: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val divider: Color,
    val iconTint: Color,
    val iconBackground: Color,
    val training: Color,
    val resting: Color,
    val completed: Color,
    val primaryButton: Color,
    val primaryButtonPressed: Color,
    val primaryButtonDisabled: Color,
    val primaryButtonText: Color,
    val secondaryButtonFill: Color,
    val secondaryButtonBorder: Color,
    val metricBackground: Color,
    val timerBackground: Color,
    val cardBorder: Color,
    val cardShadow: Color,
    val wheelBackground: Color,
    val wheelStroke: Color,
    val wheelTick: Color,
    val wheelIndicator: Color,
    val wheelTrack: Color,
    val wheelTrackStroke: Color,
    val wheelFill: Color,
    val wheelThumbStroke: Color,
    val badgeUnlocked: Color,
    val calendarWorkout: Color,
    val calendarStreak: Color,
    val error: Color,
)

internal val GymLightColors = GymColors(
    cardBackground = LightCardBackground,
    controlsBackground = LightControlsBackground,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    divider = LightDivider,
    iconTint = LightIconTint,
    iconBackground = LightIconBackground,
    training = TrainingLight,
    resting = RestingLight,
    completed = CompletedLight,
    primaryButton = LightIconTint,
    primaryButtonPressed = PrimaryPressedLight,
    primaryButtonDisabled = LightPrimaryDisabled,
    primaryButtonText = Color.White,
    secondaryButtonFill = LightSecondaryButtonFill,
    secondaryButtonBorder = LightSecondaryButtonBorder,
    metricBackground = LightMetricBackground,
    timerBackground = TimerBackgroundLight,
    cardBorder = LightCardBorder,
    cardShadow = CardShadow,
    wheelBackground = WheelBackgroundLight,
    wheelStroke = WheelStrokeLight,
    wheelTick = WheelTickLight,
    wheelIndicator = LightIconTint,
    wheelTrack = WheelTrackLight,
    wheelTrackStroke = WheelTrackStrokeLight,
    wheelFill = WheelFillLight,
    wheelThumbStroke = WheelThumbStroke,
    badgeUnlocked = BadgeUnlocked,
    calendarWorkout = CalendarWorkoutLight,
    calendarStreak = CalendarStreakLight,
    error = ErrorLight,
)

internal val GymDarkColors = GymColors(
    cardBackground = DarkCardBackground,
    controlsBackground = DarkControlsBackground,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    divider = DarkDivider,
    iconTint = DarkIconTint,
    iconBackground = DarkIconBackground,
    training = TrainingDark,
    resting = RestingDark,
    completed = CompletedDark,
    primaryButton = DarkIconTint,
    primaryButtonPressed = PrimaryPressedDark,
    primaryButtonDisabled = DarkPrimaryDisabled,
    primaryButtonText = Color.White,
    secondaryButtonFill = DarkSecondaryButtonFill,
    secondaryButtonBorder = DarkSecondaryButtonBorder,
    metricBackground = DarkMetricBackground,
    timerBackground = TimerBackgroundDark,
    cardBorder = DarkCardBorder,
    cardShadow = CardShadow,
    wheelBackground = WheelBackgroundDark,
    wheelStroke = WheelStrokeDark,
    wheelTick = WheelTickDark,
    wheelIndicator = DarkIconTint,
    wheelTrack = WheelTrackDark,
    wheelTrackStroke = WheelTrackStrokeDark,
    wheelFill = WheelFillDark,
    wheelThumbStroke = WheelThumbStroke,
    badgeUnlocked = BadgeUnlocked,
    calendarWorkout = CalendarWorkoutDark,
    calendarStreak = CalendarStreakDark,
    error = ErrorDark,
)
