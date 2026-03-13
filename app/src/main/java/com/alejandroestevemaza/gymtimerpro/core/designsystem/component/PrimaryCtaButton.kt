package com.alejandroestevemaza.gymtimerpro.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTheme

@Composable
fun PrimaryCtaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: GymComponentState = GymComponentState.Normal,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isLoading = state == GymComponentState.Loading
    val isEnabled = state != GymComponentState.Disabled && !isLoading
    val pressed = state == GymComponentState.Pressed || isPressed
    val scale by animateFloatAsState(targetValue = if (pressed && isEnabled) 0.98f else 1f, label = "cta-scale")
    val containerColor = when {
        !isEnabled -> GymTheme.colors.primaryButtonDisabled
        pressed -> GymTheme.colors.primaryButtonPressed
        else -> GymTheme.colors.primaryButton
    }

    Button(
        onClick = onClick,
        enabled = isEnabled,
        interactionSource = interactionSource,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.r16),
        modifier = modifier
            .fillMaxWidth()
            .height(GymTheme.layout.primaryButtonHeight)
            .then(
                if (isEnabled) {
                    Modifier.shadow(
                        elevation = GymTheme.elevation.primaryButton,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.r16),
                        ambientColor = GymTheme.colors.cardShadow.copy(alpha = 0.25f),
                        spotColor = GymTheme.colors.cardShadow.copy(alpha = 0.25f),
                        clip = false,
                    )
                } else {
                    Modifier
                }
            )
            .scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = GymTheme.colors.primaryButtonText,
            disabledContainerColor = GymTheme.colors.primaryButtonDisabled,
            disabledContentColor = GymTheme.colors.primaryButtonText,
        ),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = GymTheme.colors.primaryButtonText,
                strokeWidth = GymTheme.borders.card,
            )
        } else {
            Text(text = text, style = GymTheme.type.numericCta)
        }
    }
}
