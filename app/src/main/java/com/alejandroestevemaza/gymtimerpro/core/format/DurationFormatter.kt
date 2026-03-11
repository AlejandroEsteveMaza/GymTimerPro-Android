package com.alejandroestevemaza.gymtimerpro.core.format

import com.alejandroestevemaza.gymtimerpro.core.model.TimerDisplayFormat
import kotlin.math.max

fun formatDuration(
    totalSeconds: Int,
    displayFormat: TimerDisplayFormat,
): String {
    val safeSeconds = max(totalSeconds, 0)
    return when (displayFormat) {
        TimerDisplayFormat.Seconds -> safeSeconds.toString()
        TimerDisplayFormat.MinutesAndSeconds -> {
            val minutes = safeSeconds / 60
            val seconds = safeSeconds % 60
            "$minutes:${seconds.toString().padStart(2, '0')}"
        }
    }
}
