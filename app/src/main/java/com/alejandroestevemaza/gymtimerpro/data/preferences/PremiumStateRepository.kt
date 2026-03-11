package com.alejandroestevemaza.gymtimerpro.data.preferences

import android.app.Activity
import android.content.Context
import androidx.datastore.preferences.core.edit
import com.alejandroestevemaza.gymtimerpro.core.model.BillingPeriod
import com.alejandroestevemaza.gymtimerpro.core.model.BillingPeriodUnit
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumPlanKind
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumProduct
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumPurchaseError
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumPurchaseResult
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumRestoreResult
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallPlanDefaults
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlin.coroutines.resume
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.suspendCancellableCoroutine

interface PremiumStateRepository {
    val isPro: Flow<Boolean>
    val productsById: StateFlow<Map<String, PremiumProduct>>
    val isLoading: StateFlow<Boolean>

    suspend fun refresh()
    suspend fun purchase(activity: Activity, productId: String): PremiumPurchaseResult
    suspend fun restorePurchases(): PremiumRestoreResult
}

class BillingPremiumStateRepository(
    private val context: Context,
) : PremiumStateRepository {
    private val appContext = context.applicationContext
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val connectionMutex = Mutex()
    private val purchaseMutex = Mutex()
    private val pendingPurchaseMutex = Mutex()
    private val mutableProductsById = MutableStateFlow<Map<String, PremiumProduct>>(emptyMap())
    private val mutableIsLoading = MutableStateFlow(false)
    private val productDetailsById = mutableMapOf<String, ProductDetails>()
    private var pendingPurchaseResult: CompletableDeferred<PremiumPurchaseResult>? = null

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        repositoryScope.launch {
            handlePurchasesUpdated(
                billingResult = billingResult,
                purchases = purchases.orEmpty(),
            )
        }
    }

    override val isPro: Flow<Boolean> = appContext.gymTimerProDataStore.data
        .map { preferences -> preferences[PreferencesKeys.cachedIsPro] ?: false }
        .distinctUntilChanged()

    override val productsById: StateFlow<Map<String, PremiumProduct>> = mutableProductsById.asStateFlow()
    override val isLoading: StateFlow<Boolean> = mutableIsLoading.asStateFlow()

    private val billingClient: BillingClient = BillingClient.newBuilder(appContext)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .enableAutoServiceReconnection()
        .build()

    init {
        repositoryScope.launch {
            refresh()
        }
    }

    override suspend fun refresh() {
        mutableIsLoading.value = true
        try {
            if (!ensureConnection()) return
            loadProductsIfNeeded()
            reconcileEntitlements()
        } finally {
            mutableIsLoading.value = false
        }
    }

    override suspend fun purchase(
        activity: Activity,
        productId: String,
    ): PremiumPurchaseResult = purchaseMutex.withLock {
        if (!ensureConnection()) {
            return PremiumPurchaseResult.Failure(PremiumPurchaseError.Unknown)
        }

        loadProductsIfNeeded()

        val productDetails = productDetailsById[productId]
            ?: return PremiumPurchaseResult.Failure(PremiumPurchaseError.ProductUnavailable)
        val premiumProduct = mutableProductsById.value[productId]
            ?: return PremiumPurchaseResult.Failure(PremiumPurchaseError.ProductUnavailable)

        val deferred = CompletableDeferred<PremiumPurchaseResult>()
        val previousDeferred = pendingPurchaseMutex.withLock {
            val current = pendingPurchaseResult
            if (current == null) {
                pendingPurchaseResult = deferred
            }
            current
        }
        if (previousDeferred != null) {
            return PremiumPurchaseResult.Failure(PremiumPurchaseError.Unknown)
        }

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .setOfferToken(premiumProduct.offerToken)
                        .build()
                )
            )
            .build()

        val launchResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (launchResult.responseCode != BillingClient.BillingResponseCode.OK) {
            clearPendingPurchaseResult(deferred)
            if (launchResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                return if (reconcileEntitlements()) {
                    PremiumPurchaseResult.Success
                } else {
                    PremiumPurchaseResult.Failure(PremiumPurchaseError.Unknown)
                }
            }
            return PremiumPurchaseResult.Failure(mapResponseCode(launchResult.responseCode))
        }

        deferred.await()
    }

    override suspend fun restorePurchases(): PremiumRestoreResult {
        return if (!ensureConnection()) {
            PremiumRestoreResult.Failure(PremiumPurchaseError.Unknown)
        } else {
            loadProductsIfNeeded()
            try {
                if (reconcileEntitlements()) {
                    PremiumRestoreResult.Restored
                } else {
                    PremiumRestoreResult.NothingToRestore
                }
            } catch (_: Throwable) {
                PremiumRestoreResult.Failure(PremiumPurchaseError.Unknown)
            }
        }
    }

    private suspend fun handlePurchasesUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase>,
    ) {
        val deferred = pendingPurchaseMutex.withLock {
            pendingPurchaseResult.also {
                pendingPurchaseResult = null
            }
        }

        val result = when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> handleSuccessfulPurchaseUpdate(purchases)
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                PremiumPurchaseResult.Failure(PremiumPurchaseError.UserCancelled)
            }
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> {
                PremiumPurchaseResult.Failure(PremiumPurchaseError.ProductUnavailable)
            }
            else -> PremiumPurchaseResult.Failure(PremiumPurchaseError.Unknown)
        }

        if (deferred != null) {
            deferred.complete(result)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            reconcileEntitlements()
        }
    }

    private suspend fun handleSuccessfulPurchaseUpdate(
        purchases: List<Purchase>,
    ): PremiumPurchaseResult {
        val premiumPurchases = purchases.filter { purchase ->
            purchase.products.any { productId -> productId in PaywallPlanDefaults.proProductIds }
        }
        if (premiumPurchases.isEmpty()) {
            return PremiumPurchaseResult.Failure(PremiumPurchaseError.FailedVerification)
        }
        if (premiumPurchases.any { it.purchaseState == Purchase.PurchaseState.PENDING }) {
            return PremiumPurchaseResult.Failure(PremiumPurchaseError.Pending)
        }

        premiumPurchases
            .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
            .forEach { purchase ->
                acknowledgeIfNeeded(purchase)
            }

        val purchasedPremium = premiumPurchases.any { it.purchaseState == Purchase.PurchaseState.PURCHASED }
        return if (purchasedPremium) {
            updateCachedIsPro(true)
            repositoryScope.launch {
                reconcileEntitlements()
            }
            PremiumPurchaseResult.Success
        } else {
            PremiumPurchaseResult.Failure(PremiumPurchaseError.FailedVerification)
        }
    }

    private suspend fun loadProductsIfNeeded() {
        if (mutableProductsById.value.isNotEmpty()) return
        if (!ensureConnection()) return

        val products = queryProductDetails()
        productDetailsById.clear()
        productDetailsById.putAll(products.associateBy { productDetails -> productDetails.productId })
        mutableProductsById.value = products
            .mapNotNull(::toPremiumProduct)
            .associateBy(PremiumProduct::id)
    }

    private suspend fun queryProductDetails(): List<ProductDetails> = suspendCancellableCoroutine { continuation ->
        val productList = PaywallPlanDefaults.proProductIds.map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsResult ->
            if (continuation.isActive) {
                continuation.resume(
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        productDetailsResult.productDetailsList
                    } else {
                        emptyList()
                    }
                )
            }
        }
    }

    private fun toPremiumProduct(productDetails: ProductDetails): PremiumProduct? {
        val selectedOffer = selectOffer(productDetails) ?: return null
        val recurringPhase = selectedOffer.pricingPhases.pricingPhaseList
            .lastOrNull { pricingPhase -> pricingPhase.priceAmountMicros > 0L }
            ?: selectedOffer.pricingPhases.pricingPhaseList.lastOrNull()
            ?: return null
        val freeTrialPhase = selectedOffer.pricingPhases.pricingPhaseList
            .firstOrNull { pricingPhase -> pricingPhase.priceAmountMicros == 0L }

        return PremiumProduct(
            id = productDetails.productId,
            title = productDetails.title,
            formattedPrice = recurringPhase.formattedPrice,
            offerToken = selectedOffer.offerToken,
            planKind = inferPlanKind(
                productId = productDetails.productId,
                recurringPeriod = recurringPhase.billingPeriod,
            ),
            recurringPeriod = recurringPhase.billingPeriod.toBillingPeriod(),
            freeTrialPeriod = freeTrialPhase?.billingPeriod?.toBillingPeriod(),
        )
    }

    private fun selectOffer(productDetails: ProductDetails): ProductDetails.SubscriptionOfferDetails? {
        val offerDetails = productDetails.subscriptionOfferDetails.orEmpty()
        return offerDetails.firstOrNull { offer ->
            offer.pricingPhases.pricingPhaseList.any { pricingPhase -> pricingPhase.priceAmountMicros == 0L }
        } ?: offerDetails.firstOrNull()
    }

    private fun inferPlanKind(
        productId: String,
        recurringPeriod: String,
    ): PremiumPlanKind = when {
        productId == PaywallPlanDefaults.yearlyProductId -> PremiumPlanKind.Yearly
        productId == PaywallPlanDefaults.monthlyProductId -> PremiumPlanKind.Monthly
        recurringPeriod == "P1Y" -> PremiumPlanKind.Yearly
        recurringPeriod == "P1M" -> PremiumPlanKind.Monthly
        else -> PremiumPlanKind.Other
    }

    private suspend fun reconcileEntitlements(): Boolean {
        if (!ensureConnection()) return false
        val purchases = queryCurrentPurchases()
            ?: return isPro.first()
        val activePremiumPurchases = purchases.filter { purchase ->
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                purchase.products.any { productId -> productId in PaywallPlanDefaults.proProductIds }
        }

        activePremiumPurchases.forEach { purchase ->
            acknowledgeIfNeeded(purchase)
        }

        val hasPro = activePremiumPurchases.isNotEmpty()
        updateCachedIsPro(hasPro)
        return hasPro
    }

    private suspend fun queryCurrentPurchases(): List<Purchase>? = suspendCancellableCoroutine { continuation ->
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (continuation.isActive) {
                continuation.resume(
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        purchasesList
                    } else {
                        null
                    }
                )
            }
        }
    }

    private suspend fun acknowledgeIfNeeded(purchase: Purchase) {
        if (purchase.isAcknowledged) return
        suspendCancellableCoroutine { continuation ->
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.acknowledgePurchase(params) {
                if (continuation.isActive) {
                    continuation.resume(Unit)
                }
            }
        }
    }

    private suspend fun ensureConnection(): Boolean = connectionMutex.withLock {
        if (billingClient.isReady) {
            return@withLock true
        }

        suspendCancellableCoroutine { continuation ->
            billingClient.startConnection(
                object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (continuation.isActive) {
                            continuation.resume(
                                billingResult.responseCode == BillingClient.BillingResponseCode.OK
                            )
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        if (continuation.isActive) {
                            continuation.resume(false)
                        }
                    }
                }
            )
        }
    }

    private suspend fun clearPendingPurchaseResult(
        deferred: CompletableDeferred<PremiumPurchaseResult>,
    ) {
        pendingPurchaseMutex.withLock {
            if (pendingPurchaseResult === deferred) {
                pendingPurchaseResult = null
            }
        }
    }

    private fun mapResponseCode(responseCode: Int): PremiumPurchaseError = when (responseCode) {
        BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> PremiumPurchaseError.ProductUnavailable
        BillingClient.BillingResponseCode.USER_CANCELED -> PremiumPurchaseError.UserCancelled
        else -> PremiumPurchaseError.Unknown
    }

    private suspend fun updateCachedIsPro(isPro: Boolean) {
        appContext.gymTimerProDataStore.edit { preferences ->
            preferences[PreferencesKeys.cachedIsPro] = isPro
        }
    }
}

private fun String.toBillingPeriod(): BillingPeriod? {
    val match = ISO_PERIOD_REGEX.matchEntire(this) ?: return null
    val value = listOfNotNull(
        match.groups[1]?.value?.toIntOrNull()?.let { BillingPeriod(it, BillingPeriodUnit.Year) },
        match.groups[2]?.value?.toIntOrNull()?.let { BillingPeriod(it, BillingPeriodUnit.Month) },
        match.groups[3]?.value?.toIntOrNull()?.let { BillingPeriod(it, BillingPeriodUnit.Week) },
        match.groups[4]?.value?.toIntOrNull()?.let { BillingPeriod(it, BillingPeriodUnit.Day) },
    )
    return value.firstOrNull()
}

private val ISO_PERIOD_REGEX = Regex("^P(?:(\\d+)Y)?(?:(\\d+)M)?(?:(\\d+)W)?(?:(\\d+)D)?$")
