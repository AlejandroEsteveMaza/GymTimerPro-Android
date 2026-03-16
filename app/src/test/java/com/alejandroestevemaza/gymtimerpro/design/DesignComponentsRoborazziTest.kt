package com.alejandroestevemaza.gymtimerpro.design

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.ClassificationInputBar
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.GymComponentState
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.HorizontalWheelStepper
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.MetricView
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.NumericConfigRow
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.PaywallPlanCard
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.PrimaryCtaButton
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.ProgressCalendarDayCell
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.ProLockedOverlay
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.RoutineRowItem
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.SectionCard
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w430dp-h932dp-xxhdpi")
class DesignComponentsRoborazziTest {
    @Test
    fun primary_cta_states() = captureScreenRoboImage("component__primary-cta__states__light__pixel5") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GymTheme.spacing.s16),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
        ) {
            PrimaryCtaButton("Normal", onClick = {}, state = GymComponentState.Normal)
            PrimaryCtaButton("Pressed", onClick = {}, state = GymComponentState.Pressed)
            PrimaryCtaButton("Disabled", onClick = {}, state = GymComponentState.Disabled)
            PrimaryCtaButton("Loading", onClick = {}, state = GymComponentState.Loading)
            PrimaryCtaButton("Error", onClick = {}, state = GymComponentState.Error)
        }
    }

    @Test
    fun section_metric_routine_states() = captureScreenRoboImage("component__section-metric-routine__states__light__pixel5") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GymTheme.spacing.s16),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
        ) {
            SectionCard(
                modifier = Modifier.fillMaxWidth(),
                title = { Text("Section") },
            ) {
                MetricView(title = "Sets", value = "4/8")
            }
            RoutineRowItem(
                name = "Push Day",
                summary = "4 sets • 10 reps • 01:30",
                state = GymComponentState.Normal,
            )
            RoutineRowItem(
                name = "Push Day",
                summary = "4 sets • 10 reps • 01:30",
                state = GymComponentState.Disabled,
            )
        }
    }

    @Test
    fun numeric_row_and_wheel_states() = captureScreenRoboImage("component__numeric-wheel__states__light__pixel5") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GymTheme.spacing.s16),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
        ) {
            NumericConfigRow(
                icon = Icons.Rounded.FitnessCenter,
                title = "Sets",
                valueText = "4",
                value = 4,
                valueRange = 1..10,
                valueStep = 1,
                onValueChange = {},
                onOpenEditor = {},
                state = GymComponentState.Normal,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12)) {
                HorizontalWheelStepper(
                    value = 4,
                    valueRange = 1..10,
                    onValueChange = {},
                )
                HorizontalWheelStepper(
                    value = 4,
                    valueRange = 1..10,
                    onValueChange = {},
                    state = GymComponentState.Disabled,
                )
            }
        }
    }

    @Test
    fun paywall_plan_and_input_states() = captureScreenRoboImage("component__plan-input__states__light__pixel5") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GymTheme.spacing.s16),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
        ) {
            PaywallPlanCard(
                title = "Annual",
                price = "\$39.99/year",
                badge = "Best value",
                selected = true,
                onClick = {},
                state = GymComponentState.Normal,
            )
            ClassificationInputBar(
                text = "strength",
                onTextChange = {},
                onCreate = {},
                canCreate = false,
                showDuplicateError = true,
                duplicateMessage = "Duplicate classification",
                state = GymComponentState.Error,
            )
        }
    }

    @Test
    fun calendar_and_overlay_states() = captureScreenRoboImage("component__calendar-overlay__states__light__pixel5") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GymTheme.spacing.s16),
            verticalArrangement = Arrangement.spacedBy(GymTheme.spacing.s12),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(GymTheme.spacing.s8)) {
                ProgressCalendarDayCell(
                    dayLabel = "12",
                    hasWorkout = true,
                    isCurrentMonth = true,
                    isToday = true,
                    isPast = false,
                    isFuture = false,
                    onClick = {},
                )
                ProgressCalendarDayCell(
                    dayLabel = "13",
                    hasWorkout = false,
                    isCurrentMonth = true,
                    isToday = false,
                    isPast = true,
                    isFuture = false,
                    onClick = null,
                )
                ProgressCalendarDayCell(
                    dayLabel = "14",
                    hasWorkout = false,
                    isCurrentMonth = true,
                    isToday = false,
                    isPast = false,
                    isFuture = true,
                    onClick = null,
                )
            }
            ProLockedOverlay(
                isUnlocked = false,
                title = "PRO required",
                message = "Unlock premium to access this module.",
                actionText = "Unlock",
                onUnlock = {},
            ) { Column {} }
        }
    }
}
