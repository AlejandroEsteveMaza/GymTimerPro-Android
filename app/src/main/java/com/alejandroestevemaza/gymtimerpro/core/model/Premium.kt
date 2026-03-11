package com.alejandroestevemaza.gymtimerpro.core.model

enum class PremiumPlanKind {
    Yearly,
    Monthly,
    Other,
}

enum class BillingPeriodUnit {
    Day,
    Week,
    Month,
    Year,
}

data class BillingPeriod(
    val value: Int,
    val unit: BillingPeriodUnit,
)

data class PremiumProduct(
    val id: String,
    val title: String,
    val formattedPrice: String,
    val offerToken: String,
    val planKind: PremiumPlanKind,
    val recurringPeriod: BillingPeriod?,
    val freeTrialPeriod: BillingPeriod?,
)

enum class PremiumPurchaseError {
    ProductUnavailable,
    FailedVerification,
    UserCancelled,
    Pending,
    Unknown,
}

sealed interface PremiumPurchaseResult {
    data object Success : PremiumPurchaseResult

    data class Failure(
        val error: PremiumPurchaseError,
    ) : PremiumPurchaseResult
}

sealed interface PremiumRestoreResult {
    data object Restored : PremiumRestoreResult
    data object NothingToRestore : PremiumRestoreResult

    data class Failure(
        val error: PremiumPurchaseError,
    ) : PremiumRestoreResult
}
