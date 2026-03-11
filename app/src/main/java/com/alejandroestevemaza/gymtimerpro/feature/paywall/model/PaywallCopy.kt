package com.alejandroestevemaza.gymtimerpro.feature.paywall.model

import com.alejandroestevemaza.gymtimerpro.R

fun PaywallPresentationContext.copySpec(): PaywallCopySpec = when (infoLevel) {
    PaywallInfoLevel.Light -> when (entryPoint) {
        PaywallEntryPoint.ProModule -> PaywallCopySpec(
            titleRes = R.string.paywall_copy_light_pro_title,
            subtitleRes = R.string.paywall_copy_light_pro_subtitle,
            benefitsTitleRes = R.string.paywall_copy_light_pro_benefits_title,
            bulletRes = listOf(
                R.string.paywall_copy_light_pro_bullet_1,
                R.string.paywall_copy_light_pro_bullet_2,
                R.string.paywall_copy_light_pro_bullet_3,
            ),
            plansTitleRes = R.string.paywall_copy_light_pro_plans_title,
            annualLabelRes = R.string.paywall_copy_light_pro_annual_label,
            annualBadgeRes = R.string.paywall_copy_light_pro_annual_badge,
            monthlyLabelRes = R.string.paywall_copy_light_pro_monthly_label,
            monthlyBadgeRes = null,
            ctaPrimaryRes = R.string.paywall_copy_light_pro_cta_primary,
            ctaSecondaryRes = R.string.paywall_copy_light_pro_cta_secondary,
            ctaSecondaryAction = PaywallSecondaryAction.Dismiss,
            trustLineRes = R.string.paywall_copy_light_pro_trust,
            legalLine1Res = R.string.paywall_copy_light_pro_legal1,
            legalLine2Res = R.string.paywall_copy_light_pro_legal2,
            includeSectionTitleRes = null,
            includeItemRes = emptyList(),
        )

        PaywallEntryPoint.DailyLimitDuringWorkout -> PaywallCopySpec(
            titleRes = R.string.paywall_copy_light_limit_title,
            subtitleRes = R.string.paywall_copy_light_limit_subtitle,
            benefitsTitleRes = R.string.paywall_copy_light_limit_benefits_title,
            bulletRes = listOf(
                R.string.paywall_copy_light_limit_bullet_1,
                R.string.paywall_copy_light_limit_bullet_2,
                R.string.paywall_copy_light_limit_bullet_3,
            ),
            plansTitleRes = R.string.paywall_copy_light_limit_plans_title,
            annualLabelRes = R.string.paywall_copy_light_limit_annual_label,
            annualBadgeRes = R.string.paywall_copy_light_limit_annual_badge,
            monthlyLabelRes = R.string.paywall_copy_light_limit_monthly_label,
            monthlyBadgeRes = null,
            ctaPrimaryRes = R.string.paywall_copy_light_limit_cta_primary,
            ctaSecondaryRes = R.string.paywall_copy_light_limit_cta_secondary,
            ctaSecondaryAction = PaywallSecondaryAction.Dismiss,
            trustLineRes = R.string.paywall_copy_light_limit_trust,
            legalLine1Res = R.string.paywall_copy_light_limit_legal1,
            legalLine2Res = R.string.paywall_copy_light_limit_legal2,
            includeSectionTitleRes = null,
            includeItemRes = emptyList(),
        )
    }

    PaywallInfoLevel.Standard -> when (entryPoint) {
        PaywallEntryPoint.ProModule -> PaywallCopySpec(
            titleRes = R.string.paywall_copy_standard_pro_title,
            subtitleRes = R.string.paywall_copy_standard_pro_subtitle,
            benefitsTitleRes = R.string.paywall_copy_standard_pro_benefits_title,
            bulletRes = listOf(
                R.string.paywall_copy_standard_pro_bullet_1,
                R.string.paywall_copy_standard_pro_bullet_2,
                R.string.paywall_copy_standard_pro_bullet_3,
            ),
            plansTitleRes = R.string.paywall_copy_standard_pro_plans_title,
            annualLabelRes = R.string.paywall_copy_standard_pro_annual_label,
            annualBadgeRes = R.string.paywall_copy_standard_pro_annual_badge,
            monthlyLabelRes = R.string.paywall_copy_standard_pro_monthly_label,
            monthlyBadgeRes = null,
            ctaPrimaryRes = R.string.paywall_copy_standard_pro_cta_primary,
            ctaSecondaryRes = null,
            ctaSecondaryAction = PaywallSecondaryAction.Dismiss,
            trustLineRes = R.string.paywall_copy_standard_pro_trust,
            legalLine1Res = R.string.paywall_copy_standard_pro_legal1,
            legalLine2Res = R.string.paywall_copy_standard_pro_legal2,
            includeSectionTitleRes = null,
            includeItemRes = emptyList(),
        )

        PaywallEntryPoint.DailyLimitDuringWorkout -> PaywallCopySpec(
            titleRes = R.string.paywall_copy_standard_limit_title,
            subtitleRes = R.string.paywall_copy_standard_limit_subtitle,
            benefitsTitleRes = R.string.paywall_copy_standard_limit_benefits_title,
            bulletRes = listOf(
                R.string.paywall_copy_standard_limit_bullet_1,
                R.string.paywall_copy_standard_limit_bullet_2,
                R.string.paywall_copy_standard_limit_bullet_3,
            ),
            plansTitleRes = R.string.paywall_copy_standard_limit_plans_title,
            annualLabelRes = R.string.paywall_copy_standard_limit_annual_label,
            annualBadgeRes = R.string.paywall_copy_standard_limit_annual_badge,
            monthlyLabelRes = R.string.paywall_copy_standard_limit_monthly_label,
            monthlyBadgeRes = null,
            ctaPrimaryRes = R.string.paywall_copy_standard_limit_cta_primary,
            ctaSecondaryRes = R.string.paywall_copy_standard_limit_cta_secondary,
            ctaSecondaryAction = PaywallSecondaryAction.Dismiss,
            trustLineRes = R.string.paywall_copy_standard_limit_trust,
            legalLine1Res = R.string.paywall_copy_standard_limit_legal1,
            legalLine2Res = R.string.paywall_copy_standard_limit_legal2,
            includeSectionTitleRes = null,
            includeItemRes = emptyList(),
        )
    }

    PaywallInfoLevel.Detailed -> when (entryPoint) {
        PaywallEntryPoint.ProModule -> PaywallCopySpec(
            titleRes = R.string.paywall_copy_detailed_pro_title,
            subtitleRes = R.string.paywall_copy_detailed_pro_subtitle,
            benefitsTitleRes = R.string.paywall_copy_detailed_pro_benefits_title,
            bulletRes = listOf(
                R.string.paywall_copy_detailed_pro_bullet_1,
                R.string.paywall_copy_detailed_pro_bullet_2,
                R.string.paywall_copy_detailed_pro_bullet_3,
            ),
            plansTitleRes = R.string.paywall_copy_detailed_pro_plans_title,
            annualLabelRes = R.string.paywall_copy_detailed_pro_annual_label,
            annualBadgeRes = R.string.paywall_copy_detailed_pro_annual_badge,
            monthlyLabelRes = R.string.paywall_copy_detailed_pro_monthly_label,
            monthlyBadgeRes = null,
            ctaPrimaryRes = R.string.paywall_copy_detailed_pro_cta_primary,
            ctaSecondaryRes = null,
            ctaSecondaryAction = PaywallSecondaryAction.Dismiss,
            trustLineRes = R.string.paywall_copy_detailed_pro_trust,
            legalLine1Res = R.string.paywall_copy_detailed_pro_legal1,
            legalLine2Res = R.string.paywall_copy_detailed_pro_legal2,
            includeSectionTitleRes = R.string.paywall_copy_detailed_pro_include_title,
            includeItemRes = listOf(
                R.string.paywall_copy_detailed_pro_include_1,
                R.string.paywall_copy_detailed_pro_include_2,
                R.string.paywall_copy_detailed_pro_include_3,
                R.string.paywall_copy_detailed_pro_include_4,
                R.string.paywall_copy_detailed_pro_include_5,
                R.string.paywall_copy_detailed_pro_include_6,
            ),
        )

        PaywallEntryPoint.DailyLimitDuringWorkout -> PaywallCopySpec(
            titleRes = R.string.paywall_copy_detailed_limit_title,
            subtitleRes = R.string.paywall_copy_detailed_limit_subtitle,
            benefitsTitleRes = R.string.paywall_copy_detailed_limit_benefits_title,
            bulletRes = listOf(
                R.string.paywall_copy_detailed_limit_bullet_1,
                R.string.paywall_copy_detailed_limit_bullet_2,
                R.string.paywall_copy_detailed_limit_bullet_3,
            ),
            plansTitleRes = R.string.paywall_copy_detailed_limit_plans_title,
            annualLabelRes = R.string.paywall_copy_detailed_limit_annual_label,
            annualBadgeRes = R.string.paywall_copy_detailed_limit_annual_badge,
            monthlyLabelRes = R.string.paywall_copy_detailed_limit_monthly_label,
            monthlyBadgeRes = null,
            ctaPrimaryRes = R.string.paywall_copy_detailed_limit_cta_primary,
            ctaSecondaryRes = R.string.paywall_copy_detailed_limit_cta_secondary,
            ctaSecondaryAction = PaywallSecondaryAction.Dismiss,
            trustLineRes = R.string.paywall_copy_detailed_limit_trust,
            legalLine1Res = R.string.paywall_copy_detailed_limit_legal1,
            legalLine2Res = R.string.paywall_copy_detailed_limit_legal2,
            includeSectionTitleRes = R.string.paywall_copy_detailed_limit_include_title,
            includeItemRes = listOf(
                R.string.paywall_copy_detailed_limit_include_1,
                R.string.paywall_copy_detailed_limit_include_2,
                R.string.paywall_copy_detailed_limit_include_3,
                R.string.paywall_copy_detailed_limit_include_4,
                R.string.paywall_copy_detailed_limit_include_5,
                R.string.paywall_copy_detailed_limit_include_6,
            ),
        )
    }
}

object PaywallPlanDefaults {
    const val yearlyProductId = "premium_yearly"
    const val monthlyProductId = "premium_monthly"
    val proProductIds: List<String> = listOf(yearlyProductId, monthlyProductId)

    fun defaultProductId(availableIds: List<String>): String? {
        if (availableIds.contains(yearlyProductId)) {
            return yearlyProductId
        }
        return availableIds.firstOrNull()
    }
}
