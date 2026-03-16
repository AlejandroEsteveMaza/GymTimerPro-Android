package com.alejandroestevemaza.gymtimerpro.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTheme
import kotlin.math.floor
import kotlin.math.roundToInt

@Composable
fun HorizontalWheelStepper(
    value: Int,
    valueRange: IntRange,
    step: Int = 1,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    state: GymComponentState = GymComponentState.Normal,
) {
    val layout = GymTheme.layout
    val radii = GymTheme.radii
    val colors = GymTheme.colors
    val enabled = state != GymComponentState.Disabled && state != GymComponentState.Loading
    val density = LocalDensity.current

    val minValue = minOf(valueRange.first, valueRange.last)
    val maxValue = maxOf(valueRange.first, valueRange.last)
    val safeStep = step.coerceAtLeast(1)
    val clampedValue = value.coerceIn(minValue, maxValue)

    var dragValue by remember { mutableStateOf<Int?>(null) }
    var dragProgress by remember { mutableStateOf<Float?>(null) }
    val displayValue = (dragValue ?: clampedValue).coerceIn(minValue, maxValue)

    val wheelWidthPx = with(density) { layout.wheelWidth.toPx() }
    val trackInsetPx = with(density) { layout.wheelTrackInset.toPx() }
    val thumbSizePx = with(density) { layout.wheelThumbSize.toPx() }
    val thumbRadiusPx = thumbSizePx / 2f
    val thumbCenterMinPx = trackInsetPx + thumbRadiusPx
    val thumbCenterMaxPx = wheelWidthPx - trackInsetPx - thumbRadiusPx
    val thumbCenterRangePx = (thumbCenterMaxPx - thumbCenterMinPx).coerceAtLeast(1f)
    val latestOnValueChange by rememberUpdatedState(onValueChange)
    val latestClampedValue by rememberUpdatedState(clampedValue)
    val latestMinValue by rememberUpdatedState(minValue)
    val latestMaxValue by rememberUpdatedState(maxValue)
    val latestSafeStep by rememberUpdatedState(safeStep)
    val latestThumbCenterMinPx by rememberUpdatedState(thumbCenterMinPx)
    val latestThumbCenterMaxPx by rememberUpdatedState(thumbCenterMaxPx)
    val latestThumbCenterRangePx by rememberUpdatedState(thumbCenterRangePx)

    val snappedProgress = if (maxValue == minValue) {
        0f
    } else {
        (displayValue - minValue).toFloat() / (maxValue - minValue).toFloat()
    }
    val visualProgress = (dragProgress ?: snappedProgress).coerceIn(0f, 1f)
    val thumbCenterPx = thumbCenterMinPx + thumbCenterRangePx * visualProgress
    val thumbOffsetPx = thumbCenterPx - wheelWidthPx / 2f

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
                detectDragGestures(
                    onDragStart = {
                        val startValue = latestClampedValue
                        val startMin = latestMinValue
                        val startMax = latestMaxValue
                        dragValue = startValue
                        dragProgress = if (startMax == startMin) {
                            0f
                        } else {
                            (startValue - startMin).toFloat() / (startMax - startMin).toFloat()
                        }
                    },
                    onDragEnd = {
                        dragValue = null
                        dragProgress = null
                    },
                    onDragCancel = {
                        dragValue = null
                        dragProgress = null
                    },
                ) { change, _ ->
                    change.consume()
                    val min = latestMinValue
                    val max = latestMaxValue
                    val current = (dragValue ?: latestClampedValue).coerceIn(min, max)
                    val touchX = change.position.x
                    val clampedTouchX = touchX.coerceIn(latestThumbCenterMinPx, latestThumbCenterMaxPx)
                    val targetProgress = (clampedTouchX - latestThumbCenterMinPx) / latestThumbCenterRangePx
                    val rawTarget = min + ((max - min) * targetProgress).roundToInt()
                    val safeStepLocal = latestSafeStep
                    val stepIndex = ((rawTarget - min).toFloat() / safeStepLocal).roundToInt()
                    val targetValue = (min + stepIndex * safeStepLocal).coerceIn(min, max)

                    if (targetValue != current) {
                        latestOnValueChange(targetValue)
                    }
                    dragValue = targetValue
                    dragProgress = targetProgress
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
                ),
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = layout.wheelTrackInset,
                    vertical = layout.wheelTrackVerticalInset,
                )
                .height(layout.wheelTrackHeight)
                .clip(RoundedCornerShape(radii.capsule)),
        ) {
            val tickSpacingPx = layout.wheelTickSpacing.toPx()
            val tickWidthPx = layout.wheelTickWidth.toPx()
            val smallTickHeightPx = layout.wheelTickSmallHeight.toPx()
            val largeTickHeightPx = layout.wheelTickLargeHeight.toPx()
            val centerY = size.height / 2f
            val visibleTickCount = (size.width / tickSpacingPx).toInt() + 4
            val maxStepIndex = ((maxValue - minValue).toFloat() / safeStep.toFloat()).coerceAtLeast(1f)
            val wheelPosition = visualProgress * maxStepIndex
            val baseIndex = floor(wheelPosition).toInt()
            val fractionalOffset = (wheelPosition - baseIndex) * tickSpacingPx

            repeat(visibleTickCount) { localIndex ->
                val i = localIndex - 2
                val x = i * tickSpacingPx - fractionalOffset
                if (x < -tickWidthPx || x > size.width + tickWidthPx) return@repeat
                val patternIndex = i + baseIndex
                val tickHeight = if (kotlin.math.abs(patternIndex) % 3 == 0) {
                    largeTickHeightPx
                } else {
                    smallTickHeightPx
                }
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
                .fillMaxWidth()
                .padding(
                    horizontal = layout.wheelTrackInset,
                    vertical = layout.wheelTrackVerticalInset,
                )
                .height(layout.wheelTrackHeight)
                .border(
                    width = GymTheme.borders.wheelTrack,
                    color = colors.wheelTrackStroke,
                    shape = RoundedCornerShape(radii.capsule),
                ),
        )

        Box(
            modifier = Modifier
                .height(layout.wheelThumbSize)
                .width(layout.wheelThumbSize)
                .offset {
                    IntOffset(x = thumbOffsetPx.roundToInt(), y = 0)
                }
                .background(colors.wheelIndicator, CircleShape)
                .border(
                    width = GymTheme.borders.wheelTrack,
                    color = colors.wheelThumbStroke,
                    shape = CircleShape,
                ),
        )
    }
}
