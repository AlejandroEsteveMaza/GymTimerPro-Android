package com.alejandroestevemaza.gymtimerpro.feature.paywall.model

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri

data class PaywallConfig(
    val termsUri: Uri?,
    val privacyUri: Uri?,
    val manageSubscriptionUri: Uri?,
)

fun loadPaywallConfig(context: Context): PaywallConfig {
    @Suppress("DEPRECATION")
    val appInfo = context.packageManager.getApplicationInfo(
        context.packageName,
        PackageManager.GET_META_DATA,
    )
    val metadata = appInfo.metaData
    val terms = metadata?.getString(TERMS_KEY)
    val privacy = metadata?.getString(PRIVACY_KEY)
    return PaywallConfig(
        termsUri = terms?.let(Uri::parse),
        privacyUri = privacy?.let(Uri::parse),
        manageSubscriptionUri = Uri.parse(
            "https://play.google.com/store/account/subscriptions?package=${context.packageName}"
        ),
    )
}

private const val TERMS_KEY = "PAYWALL_TERMS_URL"
private const val PRIVACY_KEY = "PAYWALL_PRIVACY_URL"
