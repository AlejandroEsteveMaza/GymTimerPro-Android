package com.alejandroestevemaza.gymtimerpro.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTheme

@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    state: GymComponentState = GymComponentState.Normal,
    title: @Composable () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val radii = GymTheme.radii
    val spacing = GymTheme.spacing
    val colors = GymTheme.colors
    val alpha = if (state == GymComponentState.Disabled) 0.55f else 1f

    Column(
        modifier = modifier
            .alpha(alpha)
            .shadow(
                elevation = GymTheme.elevation.card,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(radii.r20),
                ambientColor = colors.cardShadow,
                spotColor = colors.cardShadow,
                clip = false,
            )
            .background(
                color = colors.cardBackground,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(radii.r20),
            )
            .border(
                width = GymTheme.borders.card,
                color = colors.cardBorder,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(radii.r20),
            )
            .padding(spacing.s16),
        verticalArrangement = Arrangement.spacedBy(spacing.s16),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.s10),
        ) {
            title()
            Spacer(modifier = Modifier.weight(1f))
            trailing?.invoke()
        }
        content()
    }
}
