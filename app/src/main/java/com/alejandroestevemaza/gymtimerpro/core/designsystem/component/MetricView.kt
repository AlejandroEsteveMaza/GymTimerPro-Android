package com.alejandroestevemaza.gymtimerpro.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.material3.Text
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTheme

@Composable
fun MetricView(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    state: GymComponentState = GymComponentState.Normal,
) {
    val alpha = if (state == GymComponentState.Disabled) 0.55f else 1f
    Column(
        modifier = modifier
            .alpha(alpha)
            .fillMaxWidth()
            .background(
                color = GymTheme.colors.metricBackground,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.r16),
            )
            .padding(GymTheme.spacing.s12),
        verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s6),
    ) {
        Text(
            text = title,
            style = GymTheme.type.captionSemibold,
            color = GymTheme.colors.textSecondary,
        )
        Text(
            text = value,
            style = GymTheme.type.numericMetric,
            color = GymTheme.colors.textPrimary,
        )
    }
}
