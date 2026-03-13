package com.alejandroestevemaza.gymtimerpro.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTheme

@Composable
fun PaywallPlanCard(
    title: String,
    price: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null,
    state: GymComponentState = GymComponentState.Normal,
) {
    val enabled = state != GymComponentState.Disabled && state != GymComponentState.Loading
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = GymTheme.layout.minTapHeight),
        shape = RoundedCornerShape(GymTheme.radii.r14),
        border = BorderStroke(
            width = if (selected) GymTheme.borders.planSelected else 0.dp,
            color = if (selected) GymTheme.colors.iconTint else androidx.compose.ui.graphics.Color.Transparent,
        ),
        color = GymTheme.colors.metricBackground,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GymTheme.spacing.s12),
            horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s10),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = if (selected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (selected) GymTheme.colors.iconTint else GymTheme.colors.textSecondary,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
            ) {
                Text(
                    text = title,
                    style = GymTheme.type.headlineRegular,
                    color = GymTheme.colors.textPrimary,
                    maxLines = 1,
                )
                if (badge != null) {
                    Text(
                        text = badge,
                        modifier = Modifier
                            .padding(horizontal = GymTheme.spacing.s8, vertical = GymTheme.spacing.s3),
                        style = GymTheme.type.caption2Semibold,
                        color = GymTheme.colors.iconTint,
                    )
                }
            }
            Text(
                text = price,
                style = GymTheme.type.headlineSemibold,
                color = GymTheme.colors.textPrimary,
                maxLines = 1,
            )
        }
    }
}
