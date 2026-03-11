package com.alejandroestevemaza.gymtimerpro.feature.paywall.ui

import com.alejandroestevemaza.gymtimerpro.core.model.PremiumProduct
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumPurchaseError
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallPresentationRequest

data class PaywallUiState(
    val request: PaywallPresentationRequest,
    val isLoadingProducts: Boolean = false,
    val isProcessing: Boolean = false,
    val isPro: Boolean = false,
    val products: List<PremiumProduct> = emptyList(),
    val selectedProductId: String? = null,
    val purchaseError: PremiumPurchaseError? = null,
    val showRestoreInfo: Boolean = false,
)
