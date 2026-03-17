package com.alejandroestevemaza.gymtimerpro.app.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTheme
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTimerProTheme

@Composable
fun PremiumBottomNavigationBar(
    tabs: List<AppTab>,
    currentDestination: NavDestination?,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val layout = GymTheme.layout
    val colors = GymTheme.colors

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(
                WindowInsets.navigationBars.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
                ),
            )
            .padding(
                start = layout.bottomNavHorizontalPadding,
                end = layout.bottomNavHorizontalPadding,
                bottom = layout.bottomNavVerticalPadding,
            )
            .shadow(
                elevation = GymTheme.elevation.bottomNav,
                shape = RoundedCornerShape(GymTheme.radii.r20),
                clip = false,
            ),
        shape = RoundedCornerShape(GymTheme.radii.r20),
        color = colors.bottomNavBackground,
        border = BorderStroke(
            width = GymTheme.borders.quaternary,
            color = colors.bottomNavBorder,
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(layout.bottomNavHeight)
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            tabs.forEach { tab ->
                val selected = currentDestination?.hierarchy?.any { destination ->
                    destination.route == tab.route
                } == true

                PremiumBottomNavigationItem(
                    tab = tab,
                    selected = selected,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun PremiumBottomNavigationItem(
    tab: AppTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = GymTheme.colors
    val layout = GymTheme.layout
    val type = GymTheme.type
    val animationsEnabled = GymTheme.animationsEnabled

    val contentColor = if (animationsEnabled) {
        animateColorAsState(
            targetValue = if (selected) colors.iconTint else colors.textSecondary,
            label = "BottomNavContentColor",
        )
    } else {
        rememberUpdatedState(if (selected) colors.iconTint else colors.textSecondary)
    }
    val activeIndicatorColor = if (animationsEnabled) {
        animateColorAsState(
            targetValue = if (selected) colors.bottomNavActivePill else Color.Transparent,
            label = "BottomNavIndicatorColor",
        )
    } else {
        rememberUpdatedState(if (selected) colors.bottomNavActivePill else Color.Transparent)
    }
    val contentScale = if (animationsEnabled) {
        animateFloatAsState(
            targetValue = if (selected) 1f else 0.95f,
            animationSpec = spring(
                stiffness = Spring.StiffnessMedium,
                dampingRatio = Spring.DampingRatioNoBouncy,
            ),
            label = "BottomNavItemScale",
        )
    } else {
        rememberUpdatedState(if (selected) 1f else 0.95f)
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .heightIn(min = layout.minTapHeight)
            .clip(RoundedCornerShape(GymTheme.radii.r16))
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab,
            )
            .padding(
                horizontal = layout.bottomNavItemHorizontalPadding,
                vertical = layout.bottomNavItemVerticalPadding,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.graphicsLayer {
                scaleX = contentScale.value
                scaleY = contentScale.value
            },
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(GymTheme.radii.capsule))
                    .background(activeIndicatorColor.value)
                    .padding(
                        horizontal = layout.bottomNavActiveIndicatorHorizontalPadding,
                        vertical = layout.bottomNavActiveIndicatorVerticalPadding,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = stringResource(tab.labelRes),
                    tint = contentColor.value,
                    modifier = Modifier.size(layout.bottomNavItemIconSize),
                )
            }
            Text(
                text = stringResource(tab.labelRes),
                style = type.caption2Semibold,
                color = contentColor.value,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview
@Composable
private fun PremiumBottomNavigationBarPreview() {
    GymTimerProTheme {
        PremiumBottomNavigationBar(
            tabs = AppTab.entries,
            currentDestination = null,
            onTabSelected = {},
        )
    }
}
