package com.alejandroestevemaza.gymtimerpro.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = TimerOrange,
    onPrimary = CanvasCream,
    secondary = SlateInk,
    onSecondary = CanvasCream,
    surface = CanvasCream,
    onSurface = SlateInk,
    surfaceVariant = PaleStone,
    onSurfaceVariant = SlateMuted,
)

private val DarkColors = darkColorScheme(
    primary = TimerOrange,
    onPrimary = CanvasCream,
    secondary = PaleStone,
    onSecondary = SlateInk,
    surface = NightSurface,
    onSurface = CanvasCream,
    surfaceVariant = NightElevated,
    onSurfaceVariant = PaleStone,
)

@Composable
fun GymTimerProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
