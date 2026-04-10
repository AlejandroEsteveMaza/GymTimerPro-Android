package com.alejandroestevemaza.gymtimerpro.feature.paywall.model

import com.alejandroestevemaza.gymtimerpro.R

fun PaywallInfoLevel.copySpec(): PaywallCopySpec = when (this) {
    PaywallInfoLevel.Light -> PaywallCopySpec(
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

    PaywallInfoLevel.Standard -> PaywallCopySpec(
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

    PaywallInfoLevel.Detailed -> PaywallCopySpec(
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

object PaywallPlanDefaults {
    // ID real del producto en Google Play Console
    const val subscriptionProductId = "premium"
    // IDs virtuales internos para distinguir los planes en la UI
    const val yearlyProductId = "premium_yearly"
    const val monthlyProductId = "premium_monthly"
    // Lista de IDs reales para reconciliar compras
    val proProductIds: List<String> = listOf(subscriptionProductId)

    fun defaultProductId(availableIds: List<String>): String? {
        if (availableIds.contains(yearlyProductId)) {
            return yearlyProductId
        }
        return availableIds.firstOrNull()
    }
}
