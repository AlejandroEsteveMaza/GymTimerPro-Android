package com.alejandroestevemaza.gymtimerpro.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTheme

@Composable
fun NumericConfigRow(
    icon: ImageVector,
    title: String,
    valueText: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onOpenEditor: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    state: GymComponentState = GymComponentState.Normal,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = GymTheme.layout.minTapHeight)
            .then(if (state == GymComponentState.Disabled) Modifier.alpha(0.55f) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(GymTheme.layout.configIconFrame)
                    .background(
                        color = GymTheme.colors.iconBackground,
                        shape = RoundedCornerShape(GymTheme.radii.r8),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GymTheme.colors.iconTint,
                    modifier = Modifier.size(GymTheme.layout.configIconGlyph),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s2)) {
                Text(
                    text = title,
                    style = GymTheme.type.valueLabel,
                    color = GymTheme.colors.textPrimary,
                )
                Text(
                    text = valueText,
                    modifier = if (onOpenEditor != null && state != GymComponentState.Disabled) {
                        Modifier.clickable(onClick = onOpenEditor)
                    } else {
                        Modifier
                    },
                    style = GymTheme.type.numericCta,
                    color = GymTheme.colors.textPrimary,
                )
            }
        }
        HorizontalWheelStepper(
            onDecrement = onDecrease,
            onIncrement = onIncrease,
            state = state,
        )
    }
}
