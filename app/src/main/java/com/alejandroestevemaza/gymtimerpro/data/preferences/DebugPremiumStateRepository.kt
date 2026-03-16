package com.alejandroestevemaza.gymtimerpro.data.preferences

import android.app.Activity
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumProduct
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumPurchaseError
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumPurchaseResult
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumRestoreResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

/**
 * Implementación fake de [PremiumStateRepository] para builds de DEBUG.
 *
 * Permite desarrollar y probar la app sin necesidad de subir un AAB a Play Console
 * ni pasar por el flujo real de Google Play Billing.
 *
 * Cambia [_isPro] a false si quieres probar la experiencia de usuario no-premium.
 */
class DebugPremiumStateRepository : PremiumStateRepository {

    private val _isPro = MutableStateFlow(true)

    override val isPro: Flow<Boolean> = _isPro

    override val productsById: StateFlow<Map<String, PremiumProduct>> =
        MutableStateFlow(emptyMap<String, PremiumProduct>()).asStateFlow()

    override val isLoading: StateFlow<Boolean> =
        MutableStateFlow(false).asStateFlow()

    override suspend fun refresh() = Unit

    override suspend fun purchase(
        activity: Activity,
        productId: String,
    ): PremiumPurchaseResult = PremiumPurchaseResult.Success

    override suspend fun restorePurchases(): PremiumRestoreResult =
        PremiumRestoreResult.Restored
}
