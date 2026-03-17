package com.alejandroestevemaza.gymtimerpro.feature.paywall

import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallEntryPoint
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallInfoLevel
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallPlanDefaults
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallPresentationContext
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallSecondaryAction
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.copySpec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PaywallCopyMatrixContractTest {

    @Test
    fun `copy matrix exposes all 6 variants with expected optional sections`() {
        val cases = listOf(
            Case(PaywallInfoLevel.Light, PaywallEntryPoint.ProModule, hasSecondaryCta = true, hasIncludeSection = false),
            Case(PaywallInfoLevel.Light, PaywallEntryPoint.DailyLimitDuringWorkout, hasSecondaryCta = true, hasIncludeSection = false),
            Case(PaywallInfoLevel.Standard, PaywallEntryPoint.ProModule, hasSecondaryCta = false, hasIncludeSection = false),
            Case(PaywallInfoLevel.Standard, PaywallEntryPoint.DailyLimitDuringWorkout, hasSecondaryCta = true, hasIncludeSection = false),
            Case(PaywallInfoLevel.Detailed, PaywallEntryPoint.ProModule, hasSecondaryCta = false, hasIncludeSection = true),
            Case(PaywallInfoLevel.Detailed, PaywallEntryPoint.DailyLimitDuringWorkout, hasSecondaryCta = true, hasIncludeSection = true),
        )

        cases.forEach { case ->
            val spec = PaywallPresentationContext(
                entryPoint = case.entryPoint,
                infoLevel = case.infoLevel,
            ).copySpec()

            assertEquals("Every variant must include 3 bullet lines", 3, spec.bulletRes.size)
            assertNull("Monthly badge must remain null by contract", spec.monthlyBadgeRes)
            assertEquals("Secondary action contract changed unexpectedly", PaywallSecondaryAction.Dismiss, spec.ctaSecondaryAction)

            if (case.hasSecondaryCta) {
                assertNotNull("Missing secondary CTA for $case", spec.ctaSecondaryRes)
            } else {
                assertNull("Unexpected secondary CTA for $case", spec.ctaSecondaryRes)
            }

            if (case.hasIncludeSection) {
                assertNotNull("Missing include section title for $case", spec.includeSectionTitleRes)
                assertEquals("Detailed variants must expose 6 include rows", 6, spec.includeItemRes.size)
            } else {
                assertNull("Unexpected include section title for $case", spec.includeSectionTitleRes)
                assertTrue("Unexpected include rows for $case", spec.includeItemRes.isEmpty())
            }
        }
    }

    @Test
    fun `yearly plan is selected by default when present`() {
        val defaultWhenBoth = PaywallPlanDefaults.defaultProductId(
            availableIds = listOf(PaywallPlanDefaults.monthlyProductId, PaywallPlanDefaults.yearlyProductId),
        )
        assertEquals(PaywallPlanDefaults.yearlyProductId, defaultWhenBoth)

        val defaultWhenOnlyMonthly = PaywallPlanDefaults.defaultProductId(
            availableIds = listOf(PaywallPlanDefaults.monthlyProductId),
        )
        assertEquals(PaywallPlanDefaults.monthlyProductId, defaultWhenOnlyMonthly)
    }
}

private data class Case(
    val infoLevel: PaywallInfoLevel,
    val entryPoint: PaywallEntryPoint,
    val hasSecondaryCta: Boolean,
    val hasIncludeSection: Boolean,
)
