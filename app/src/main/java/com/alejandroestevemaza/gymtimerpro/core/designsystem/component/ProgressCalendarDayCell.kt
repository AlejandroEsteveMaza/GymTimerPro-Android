package com.alejandroestevemaza.gymtimerpro.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTheme

@Composable
fun ProgressCalendarDayCell(
    dayLabel: String,
    hasWorkout: Boolean,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    isPast: Boolean,
    isFuture: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    state: GymComponentState = GymComponentState.Normal,
) {
    val borderColor: Color
    val fillColor: Color
    val textColor: Color

    when {
        hasWorkout -> {
            fillColor = GymTheme.colors.calendarWorkout
            textColor = GymTheme.colors.primaryButtonText
            borderColor = if (isToday || isPast) GymTheme.colors.calendarStreak else GymTheme.colors.textSecondary
        }

        isToday -> {
            fillColor = Color.Transparent
            textColor = GymTheme.colors.calendarStreak
            borderColor = GymTheme.colors.calendarStreak
        }

        isPast -> {
            fillColor = GymTheme.colors.textSecondary.copy(alpha = 0.22f)
            textColor = GymTheme.colors.textSecondary
            borderColor = GymTheme.colors.textSecondary.copy(alpha = 0.65f)
        }

        isFuture -> {
            fillColor = Color.Transparent
            textColor = GymTheme.colors.textSecondary
            borderColor = GymTheme.colors.textSecondary.copy(alpha = 0.45f)
        }

        else -> {
            fillColor = Color.Transparent
            textColor = GymTheme.colors.textSecondary
            borderColor = Color.Transparent
        }
    }

    Box(
        modifier = modifier
            .heightIn(min = GymTheme.layout.progressCalendarRowMinHeight),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(GymTheme.layout.progressCalendarCellSize)
                .background(fillColor, CircleShape)
                .border(
                    width = GymTheme.borders.progressCell,
                    color = borderColor,
                    shape = CircleShape,
                )
                .then(
                    if (
                        state != GymComponentState.Disabled &&
                        isCurrentMonth &&
                        hasWorkout &&
                        onClick != null
                    ) {
                        Modifier.clickable { onClick() }
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (hasWorkout) {
                Icon(
                    imageVector = Icons.Rounded.FitnessCenter,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(GymTheme.layout.progressCalendarWorkoutIconSize),
                )
            } else {
                Text(
                    text = dayLabel,
                    style = GymTheme.type.caption2Semibold,
                    color = if (isCurrentMonth) textColor else GymTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
