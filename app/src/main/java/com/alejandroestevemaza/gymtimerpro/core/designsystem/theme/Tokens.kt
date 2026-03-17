package com.alejandroestevemaza.gymtimerpro.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class GymSpacing(
    val s2: Dp = 2.dp,
    val s3: Dp = 3.dp,
    val s4: Dp = 4.dp,
    val s6: Dp = 6.dp,
    val s8: Dp = 8.dp,
    val s10: Dp = 10.dp,
    val s12: Dp = 12.dp,
    val s14: Dp = 14.dp,
    val s16: Dp = 16.dp,
    val s20: Dp = 20.dp,
    val s24: Dp = 24.dp,
    val s32: Dp = 32.dp,
)

@Immutable
data class GymRadii(
    val r6: Dp = 6.dp,
    val r8: Dp = 8.dp,
    val r10: Dp = 10.dp,
    val r12: Dp = 12.dp,
    val r14: Dp = 14.dp,
    val r16: Dp = 16.dp,
    val r18: Dp = 18.dp,
    val r20: Dp = 20.dp,
    val capsule: Dp = 999.dp,
)

@Immutable
data class GymBorders(
    val card: Dp = 1.dp,
    val planSelected: Dp = 1.5.dp,
    val progressCell: Dp = 1.5.dp,
    val wheelTrack: Dp = 1.dp,
    val resetIcon: Dp = 1.dp,
    val quaternary: Dp = 1.dp,
)

@Immutable
data class GymElevation(
    val card: Dp = 12.dp,
    val bottomControls: Dp = 12.dp,
    val bottomNav: Dp = 10.dp,
    val primaryButton: Dp = 10.dp,
    val lockedOverlay: Dp = 18.dp,
)

@Immutable
data class GymLayout(
    val minTapHeight: Dp = 48.dp,
    val primaryButtonHeight: Dp = 80.dp,
    val scrollBottomPadding: Dp = 104.dp,
    val contentMaxWidthExpanded: Dp = 560.dp,
    val editorPopoverMinWidth: Dp = 260.dp,
    val editorPopoverMinHeight: Dp = 320.dp,
    val classificationListHeight: Dp = 280.dp,
    val icon18: Dp = 18.dp,
    val configIconFrame: Dp = 28.dp,
    val configIconGlyph: Dp = 14.dp,
    val lockedOverlayBlurRadius: Dp = 10.dp,
    val lockedOverlayMaxWidth: Dp = 360.dp,
    val lockedOverlayIconSize: Dp = 26.dp,
    val wheelWidth: Dp = 94.dp,
    val wheelHeight: Dp = 32.dp,
    val wheelTrackHeight: Dp = 12.dp,
    val wheelTrackInset: Dp = 8.dp,
    val wheelTrackVerticalInset: Dp = 6.dp,
    val wheelTickWidth: Dp = 2.dp,
    val wheelTickSpacing: Dp = 6.dp,
    val wheelTickSmallHeight: Dp = 8.dp,
    val wheelTickLargeHeight: Dp = 14.dp,
    val wheelStepMinWidth: Dp = 10.dp,
    val wheelThumbSize: Dp = 16.dp,
    val wheelHitTarget: Dp = 48.dp,
    val progressChartHeight: Dp = 180.dp,
    val progressBadgeMinHeight: Dp = 96.dp,
    val progressActivityMinHeight: Dp = 78.dp,
    val progressCalendarCellSize: Dp = 20.dp,
    val progressCalendarRowMinHeight: Dp = 22.dp,
    val progressCalendarWorkoutIconSize: Dp = 12.dp,
    val progressCalendarGridGap: Dp = 6.dp,
    val progressStreakIndicatorSize: Dp = 32.dp,
    val progressStreakColumnWidth: Dp = 40.dp,
    val bottomNavHeight: Dp = 60.dp,
    val bottomNavHorizontalPadding: Dp = 14.dp,
    val bottomNavVerticalPadding: Dp = 8.dp,
    val bottomNavItemIconSize: Dp = 20.dp,
    val bottomNavItemHorizontalPadding: Dp = 6.dp,
    val bottomNavItemVerticalPadding: Dp = 6.dp,
    val bottomNavActiveIndicatorHorizontalPadding: Dp = 8.dp,
    val bottomNavActiveIndicatorVerticalPadding: Dp = 3.dp,
)

internal val GymShapeTokens = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
)
