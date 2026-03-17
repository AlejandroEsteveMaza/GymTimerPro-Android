package com.alejandroestevemaza.gymtimerpro.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTheme

@Composable
fun RoutineCatalogSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = GymTheme.colors.cardBackground,
                shape = RoundedCornerShape(GymTheme.radii.r12),
            )
            .padding(horizontal = GymTheme.spacing.s12, vertical = GymTheme.spacing.s4),
        horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = null,
            tint = GymTheme.colors.textSecondary,
            modifier = Modifier.size(GymTheme.layout.icon18),
        )
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            textStyle = GymTheme.type.subheadlineRegular,
            placeholder = {
                Text(
                    text = placeholder,
                    style = GymTheme.type.subheadlineRegular,
                    color = GymTheme.colors.textSecondary,
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            singleLine = true,
        )
        if (GymTheme.animationsEnabled) {
            AnimatedVisibility(visible = query.isNotBlank()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        tint = GymTheme.colors.textSecondary,
                        modifier = Modifier.size(GymTheme.layout.icon18),
                    )
                }
            }
        } else if (query.isNotBlank()) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = GymTheme.colors.textSecondary,
                    modifier = Modifier.size(GymTheme.layout.icon18),
                )
            }
        }
    }
}

@Composable
fun RoutineCatalogSectionHeader(
    title: String,
    routineCount: Int,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = GymTheme.layout.minTapHeight)
            .background(
                color = GymTheme.colors.cardBackground,
                shape = RoundedCornerShape(GymTheme.radii.r12),
            )
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = GymTheme.spacing.s10,
                    end = GymTheme.spacing.s6,
                    top = GymTheme.spacing.s8,
                    bottom = GymTheme.spacing.s8,
                ),
            horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s10),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(GymTheme.layout.configIconFrame)
                    .background(
                        color = GymTheme.colors.iconBackground,
                        shape = RoundedCornerShape(GymTheme.radii.r8),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Layers,
                    contentDescription = null,
                    tint = GymTheme.colors.iconTint,
                    modifier = Modifier.size(GymTheme.layout.configIconGlyph),
                )
            }
            Text(
                text = title,
                style = GymTheme.type.subheadlineSemibold,
                color = GymTheme.colors.textPrimary,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = routineCount.toString(),
                style = GymTheme.type.captionSemibold,
                color = GymTheme.colors.textSecondary,
                modifier = Modifier
                    .background(
                        color = GymTheme.colors.cardBackground,
                        shape = RoundedCornerShape(GymTheme.radii.capsule),
                    )
                    .padding(
                        horizontal = GymTheme.spacing.s8,
                        vertical = GymTheme.spacing.s4,
                    ),
            )
        }
        Icon(
            imageVector = if (isExpanded) Icons.Rounded.ExpandMore else Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = GymTheme.colors.textSecondary,
            modifier = Modifier.padding(end = GymTheme.spacing.s12),
        )
    }
}

@Composable
fun RoutineCatalogRow(
    name: String,
    summary: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false,
    trailing: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = GymTheme.layout.minTapHeight)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(GymTheme.radii.r12),
        color = if (isHighlighted) GymTheme.colors.iconBackground else GymTheme.colors.metricBackground,
        border = BorderStroke(
            width = GymTheme.borders.card,
            color = if (isHighlighted) {
                GymTheme.colors.iconTint.copy(alpha = 0.45f)
            } else {
                GymTheme.colors.divider.copy(alpha = 0.65f)
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GymTheme.spacing.s10, vertical = GymTheme.spacing.s8),
            horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s10),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(GymTheme.layout.configIconFrame)
                    .background(
                        color = GymTheme.colors.iconBackground,
                        shape = RoundedCornerShape(GymTheme.radii.r8),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.FitnessCenter,
                    contentDescription = null,
                    tint = GymTheme.colors.iconTint,
                    modifier = Modifier.size(GymTheme.layout.configIconGlyph),
                )
            }
            RoutineRowItem(
                modifier = Modifier.weight(1f),
                name = name,
                summary = summary,
            )
            trailing()
        }
    }
}
