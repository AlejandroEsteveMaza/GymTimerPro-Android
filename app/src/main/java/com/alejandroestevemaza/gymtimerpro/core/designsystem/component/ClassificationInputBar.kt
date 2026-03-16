package com.alejandroestevemaza.gymtimerpro.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTheme

@Composable
fun ClassificationInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCreate: () -> Unit,
    canCreate: Boolean,
    showDuplicateError: Boolean,
    duplicateMessage: String,
    modifier: Modifier = Modifier,
    state: GymComponentState = GymComponentState.Normal,
) {
    val enabled = state != GymComponentState.Disabled && state != GymComponentState.Loading
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                start = GymTheme.spacing.s16,
                end = GymTheme.spacing.s16,
                top = GymTheme.spacing.s8,
                bottom = GymTheme.spacing.s10,
            ),
        verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s6),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = GymTheme.colors.secondaryButtonFill,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(GymTheme.radii.r12),
                )
                .padding(horizontal = GymTheme.spacing.s12, vertical = GymTheme.spacing.s8),
            horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = GymTheme.colors.textSecondary,
            )
            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                enabled = enabled,
                textStyle = GymTheme.type.subheadlineRegular,
                placeholder = {
                    Text(
                        text = "",
                        style = GymTheme.type.subheadlineRegular,
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    disabledContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                ),
            )
            if (text.isNotBlank()) {
                IconButton(onClick = { onTextChange("") }, enabled = enabled) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        tint = GymTheme.colors.textSecondary,
                    )
                }
            }
            IconButton(onClick = onCreate, enabled = enabled && canCreate) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    tint = if (canCreate) GymTheme.colors.iconTint else GymTheme.colors.textSecondary,
                )
            }
        }
        if (showDuplicateError || state == GymComponentState.Error) {
            Text(
                text = duplicateMessage,
                style = GymTheme.type.captionRegular,
                color = GymTheme.colors.error,
            )
        }
    }
}
