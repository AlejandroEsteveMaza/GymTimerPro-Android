package com.alejandroestevemaza.gymtimerpro.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalGymColors = staticCompositionLocalOf { GymLightColors }
private val LocalGymSpacing = staticCompositionLocalOf { GymSpacing() }
private val LocalGymRadii = staticCompositionLocalOf { GymRadii() }
private val LocalGymBorders = staticCompositionLocalOf { GymBorders() }
private val LocalGymElevation = staticCompositionLocalOf { GymElevation() }
private val LocalGymLayout = staticCompositionLocalOf { GymLayout() }
private val LocalGymType = staticCompositionLocalOf { GymTypeStyles }
val LocalEnergySavingActive = staticCompositionLocalOf { false }

object GymTheme {
    val colors: GymColors
        @Composable
        @ReadOnlyComposable
        get() = LocalGymColors.current

    val spacing: GymSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalGymSpacing.current

    val radii: GymRadii
        @Composable
        @ReadOnlyComposable
        get() = LocalGymRadii.current

    val borders: GymBorders
        @Composable
        @ReadOnlyComposable
        get() = LocalGymBorders.current

    val elevation: GymElevation
        @Composable
        @ReadOnlyComposable
        get() = LocalGymElevation.current

    val layout: GymLayout
        @Composable
        @ReadOnlyComposable
        get() = LocalGymLayout.current

    val type: GymType
        @Composable
        @ReadOnlyComposable
        get() = LocalGymType.current

    val animationsEnabled: Boolean
        @Composable
        @ReadOnlyComposable
        get() = !LocalEnergySavingActive.current
}

@Composable
fun GymTimerProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) GymDarkColorScheme else GymLightColorScheme
    val gymColors = if (darkTheme) GymDarkColors else GymLightColors
    CompositionLocalProvider(
        LocalGymColors provides gymColors,
        LocalGymSpacing provides GymSpacing(),
        LocalGymRadii provides GymRadii(),
        LocalGymBorders provides GymBorders(),
        LocalGymElevation provides GymElevation(),
        LocalGymLayout provides GymLayout(),
        LocalGymType provides GymTypeStyles,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = GymTypography,
            shapes = GymShapeTokens,
            content = content,
        )
    }
}
