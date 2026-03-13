package com.alejandroestevemaza.gymtimerpro.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTheme

@Composable
fun ProLockedOverlay(
    isUnlocked: Boolean,
    title: String,
    message: String,
    actionText: String,
    onUnlock: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(if (isUnlocked) androidx.compose.ui.unit.Dp.Hairline else GymTheme.layout.lockedOverlayBlurRadius)
                .alpha(if (isUnlocked) 1f else 0.18f),
        ) {
            content()
        }

        if (!isUnlocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = {}),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    modifier = Modifier
                        .padding(horizontal = GymTheme.spacing.s24)
                        .widthIn(max = GymTheme.layout.lockedOverlayMaxWidth)
                        .shadow(
                            elevation = GymTheme.elevation.lockedOverlay,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.r18),
                            ambientColor = GymTheme.colors.cardShadow.copy(alpha = 0.12f),
                            spotColor = GymTheme.colors.cardShadow.copy(alpha = 0.12f),
                            clip = false,
                        ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.r18),
                    shadowElevation = GymTheme.elevation.lockedOverlay,
                    color = GymTheme.colors.cardBackground,
                ) {
                    Column(
                        modifier = Modifier.padding(GymTheme.spacing.s20),
                        verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s10),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = GymTheme.colors.iconTint,
                            modifier = Modifier.size(GymTheme.layout.lockedOverlayIconSize),
                        )
                        Text(
                            text = title,
                            style = GymTheme.type.title2Bold,
                            color = GymTheme.colors.textPrimary,
                        )
                        Text(
                            text = message,
                            style = GymTheme.type.subheadlineRegular,
                            color = GymTheme.colors.textSecondary,
                        )
                        PrimaryCtaButton(
                            text = actionText,
                            onClick = onUnlock,
                            modifier = Modifier.padding(top = GymTheme.spacing.s8),
                            state = GymComponentState.Normal,
                        )
                    }
                }
            }
        }
    }
}
