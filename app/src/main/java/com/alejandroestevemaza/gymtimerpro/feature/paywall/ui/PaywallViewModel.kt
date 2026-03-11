package com.alejandroestevemaza.gymtimerpro.feature.paywall.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumPlanKind
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumProduct
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumPurchaseError
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumPurchaseResult
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumRestoreResult
import com.alejandroestevemaza.gymtimerpro.data.preferences.PremiumStateRepository
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallPlanDefaults
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallPresentationRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PaywallViewModel(
    private val premiumStateRepository: PremiumStateRepository,
    private val request: PaywallPresentationRequest,
) : ViewModel() {
    private val selectedProductId = MutableStateFlow<String?>(null)
    private val isProcessing = MutableStateFlow(false)
    private val purchaseError = MutableStateFlow<PremiumPurchaseError?>(null)
    private val showRestoreInfo = MutableStateFlow(false)

    private val repositoryState = combine(
        premiumStateRepository.isPro,
        premiumStateRepository.productsById,
        premiumStateRepository.isLoading,
    ) { isPro, productsById, isLoading ->
        RepositoryState(
            isPro = isPro,
            productsById = productsById,
            isLoading = isLoading,
        )
    }

    val uiState: StateFlow<PaywallUiState> = combine(
        repositoryState,
        selectedProductId,
        isProcessing,
        purchaseError,
        showRestoreInfo,
    ) { repositoryState, selectedProductId, isProcessing, purchaseError, showRestoreInfo ->
        val orderedProducts = repositoryState.productsById.values.sortedWith(
            compareBy<PremiumProduct> { product ->
                when (product.planKind) {
                    PremiumPlanKind.Yearly -> 0
                    PremiumPlanKind.Monthly -> 1
                    PremiumPlanKind.Other -> 2
                }
            }.thenBy { product -> product.id }
        )
        val effectiveSelectedProductId = selectedProductId
            ?.takeIf { candidate -> orderedProducts.any { product -> product.id == candidate } }
            ?: PaywallPlanDefaults.defaultProductId(orderedProducts.map(PremiumProduct::id))

        PaywallUiState(
            request = request,
            isLoadingProducts = repositoryState.isLoading,
            isProcessing = isProcessing,
            isPro = repositoryState.isPro,
            products = orderedProducts,
            selectedProductId = effectiveSelectedProductId,
            purchaseError = purchaseError,
            showRestoreInfo = showRestoreInfo,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PaywallUiState(request = request),
    )

    fun refresh() {
        viewModelScope.launch {
            premiumStateRepository.refresh()
        }
    }

    fun onSelectProduct(productId: String) {
        selectedProductId.value = productId
        purchaseError.value = null
        showRestoreInfo.value = false
    }

    fun onDismissError() {
        purchaseError.value = null
    }

    fun onDismissInfo() {
        showRestoreInfo.value = false
    }

    fun onPurchase(activity: Activity) {
        val productId = uiState.value.selectedProductId
        if (productId == null) {
            purchaseError.value = PremiumPurchaseError.ProductUnavailable
            return
        }

        viewModelScope.launch {
            isProcessing.value = true
            purchaseError.value = null
            showRestoreInfo.value = false
            try {
                when (val result = premiumStateRepository.purchase(activity, productId)) {
                    PremiumPurchaseResult.Success -> Unit
                    is PremiumPurchaseResult.Failure -> {
                        if (result.error != PremiumPurchaseError.UserCancelled) {
                            purchaseError.value = result.error
                        }
                    }
                }
            } finally {
                isProcessing.value = false
            }
        }
    }

    fun onRestore() {
        viewModelScope.launch {
            isProcessing.value = true
            purchaseError.value = null
            showRestoreInfo.value = false
            try {
                when (val result = premiumStateRepository.restorePurchases()) {
                    PremiumRestoreResult.Restored -> Unit
                    PremiumRestoreResult.NothingToRestore -> showRestoreInfo.value = true
                    is PremiumRestoreResult.Failure -> purchaseError.value = result.error
                }
            } finally {
                isProcessing.value = false
            }
        }
    }

    companion object {
        fun factory(
            premiumStateRepository: PremiumStateRepository,
            request: PaywallPresentationRequest,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PaywallViewModel(
                    premiumStateRepository = premiumStateRepository,
                    request = request,
                ) as T
            }
        }
    }
}

private data class RepositoryState(
    val isPro: Boolean,
    val productsById: Map<String, PremiumProduct>,
    val isLoading: Boolean,
)
