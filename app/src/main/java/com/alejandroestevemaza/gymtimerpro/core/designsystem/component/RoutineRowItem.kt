package com.alejandroestevemaza.gymtimerpro.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextOverflow
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTheme

@Composable
fun RoutineRowItem(
    name: String,
    summary: String,
    modifier: Modifier = Modifier,
    state: GymComponentState = GymComponentState.Normal,
) {
    val alpha = if (state == GymComponentState.Disabled) 0.55f else 1f
    Column(
        modifier = modifier.alpha(alpha),
        verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s4),
    ) {
        Text(
            text = name,
            style = GymTheme.type.headlineRegular,
            color = GymTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = summary,
            style = GymTheme.type.subheadlineRegular,
            color = GymTheme.colors.textSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
