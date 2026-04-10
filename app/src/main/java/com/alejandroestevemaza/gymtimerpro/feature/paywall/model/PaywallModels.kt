package com.alejandroestevemaza.gymtimerpro.feature.paywall.model

enum class PaywallInfoLevel {
    Light,
    Standard,
    Detailed,
}

enum class PaywallSecondaryAction {
    Dismiss,
}

data class PaywallPresentationRequest(
    val infoLevel: PaywallInfoLevel,
    val dailyLimit: Int,
    val consumedToday: Int,
)

data class PaywallCopySpec(
    val titleRes: Int,
    val subtitleRes: Int,
    val benefitsTitleRes: Int,
    val bulletRes: List<Int>,
    val plansTitleRes: Int,
    val annualLabelRes: Int,
    val annualBadgeRes: Int,
    val monthlyLabelRes: Int,
    val monthlyBadgeRes: Int?,
    val ctaPrimaryRes: Int,
    val ctaSecondaryRes: Int?,
    val ctaSecondaryAction: PaywallSecondaryAction,
    val trustLineRes: Int,
    val legalLine1Res: Int,
    val legalLine2Res: Int,
    val includeSectionTitleRes: Int?,
    val includeItemRes: List<Int>,
)
