package com.alejandroestevemaza.gymtimerpro.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTheme

@Composable
fun HorizontalWheelStepper(
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
    state: GymComponentState = GymComponentState.Normal,
) {
    val layout = GymTheme.layout
    val radii = GymTheme.radii
    val colors = GymTheme.colors
    val enabled = state != GymComponentState.Disabled && state != GymComponentState.Loading
    var dragAccumulatorPx by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .width(layout.wheelWidth)
            .height(layout.wheelHeight)
            .heightIn(min = layout.wheelHitTarget)
            .alpha(if (enabled) 1f else 0.55f)
            .background(
                color = colors.wheelBackground,
                shape = RoundedCornerShape(radii.r10),
            )
            .border(
                width = GymTheme.borders.wheelTrack,
                color = colors.wheelStroke,
                shape = RoundedCornerShape(radii.r10),
            )
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                val minStepPx = layout.wheelStepMinWidth.toPx()
                detectDragGestures(
                    onDragEnd = { dragAccumulatorPx = 0f },
                    onDragCancel = { dragAccumulatorPx = 0f },
                ) { change, dragAmount ->
                    change.consume()
                    dragAccumulatorPx += dragAmount.x

                    while (dragAccumulatorPx >= minStepPx) {
                        onIncrement()
                        dragAccumulatorPx -= minStepPx
                    }
                    while (dragAccumulatorPx <= -minStepPx) {
                        onDecrement()
                        dragAccumulatorPx += minStepPx
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = layout.wheelTrackInset,
                    vertical = layout.wheelTrackVerticalInset,
                )
                .height(layout.wheelTrackHeight)
                .background(
                    color = colors.wheelTrack,
                    shape = RoundedCornerShape(radii.capsule),
                )
                .border(
                    width = GymTheme.borders.wheelTrack,
                    color = colors.wheelTrackStroke,
                    shape = RoundedCornerShape(radii.capsule),
                ),
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = layout.wheelTrackInset),
        ) {
            val tickSpacingPx = layout.wheelTickSpacing.toPx()
            val tickWidthPx = layout.wheelTickWidth.toPx()
            val smallTickHeightPx = layout.wheelTickSmallHeight.toPx()
            val largeTickHeightPx = layout.wheelTickLargeHeight.toPx()
            val centerY = size.height / 2f
            val tickCount = (size.width / tickSpacingPx).toInt()

            repeat(tickCount + 1) { index ->
                val x = index * tickSpacingPx
                val tickHeight = if (index % 3 == 0) largeTickHeightPx else smallTickHeightPx
                drawRoundRect(
                    color = colors.wheelTick,
                    topLeft = Offset(
                        x = x - tickWidthPx / 2f,
                        y = centerY - tickHeight / 2f,
                    ),
                    size = Size(width = tickWidthPx, height = tickHeight),
                    cornerRadius = CornerRadius(tickWidthPx, tickWidthPx),
                )
            }
        }

        Box(
            modifier = Modifier
                .height(layout.wheelThumbSize)
                .width(layout.wheelThumbSize)
                .background(colors.wheelIndicator, CircleShape)
                .border(
                    width = GymTheme.borders.wheelTrack,
                    color = colors.wheelThumbStroke,
                    shape = CircleShape,
                ),
        )
    }
}
