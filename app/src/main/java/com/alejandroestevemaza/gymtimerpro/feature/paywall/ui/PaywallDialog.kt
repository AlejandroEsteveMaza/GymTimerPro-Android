package com.alejandroestevemaza.gymtimerpro.feature.paywall.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.icu.text.MeasureFormat
import android.icu.util.Measure
import android.icu.util.MeasureUnit
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alejandroestevemaza.gymtimerpro.R
import com.alejandroestevemaza.gymtimerpro.core.model.BillingPeriod
import com.alejandroestevemaza.gymtimerpro.core.model.BillingPeriodUnit
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumPlanKind
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumProduct
import com.alejandroestevemaza.gymtimerpro.core.model.PremiumPurchaseError
import com.alejandroestevemaza.gymtimerpro.data.preferences.AppContainer
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallConfig
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallCopySpec
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallPlanDefaults
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallPresentationRequest
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallSecondaryAction
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.copySpec
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.loadPaywallConfig
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PaywallDialog(
    appContainer: AppContainer,
    request: PaywallPresentationRequest,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val config = remember(context) { loadPaywallConfig(context) }
    val paywallViewModel: PaywallViewModel = viewModel(
        key = request.toString(),
        factory = PaywallViewModel.factory(
            premiumStateRepository = appContainer.premiumStateRepository,
            request = request,
        )
    )
    val uiState by paywallViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        paywallViewModel.refresh()
    }
    LaunchedEffect(uiState.isPro) {
        if (uiState.isPro) {
            onDismiss()
        }
    }

    PaywallDialogContent(
        uiState = uiState,
        config = config,
        onDismiss = onDismiss,
        onSelectProduct = paywallViewModel::onSelectProduct,
        onDismissError = paywallViewModel::onDismissError,
        onDismissInfo = paywallViewModel::onDismissInfo,
        onPurchase = { activity -> paywallViewModel.onPurchase(activity) },
        onRestore = paywallViewModel::onRestore,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaywallDialogContent(
    uiState: PaywallUiState,
    config: PaywallConfig,
    onDismiss: () -> Unit,
    onSelectProduct: (String) -> Unit,
    onDismissError: () -> Unit,
    onDismissInfo: () -> Unit,
    onPurchase: (Activity) -> Unit,
    onRestore: () -> Unit,
) {
    val copySpec = uiState.request.context.copySpec()
    val locale = remember { Locale.getDefault() }
    val uriHandler = LocalUriHandler.current
    val activity = LocalContext.current.findActivity()

    if (uiState.purchaseError != null) {
        AlertDialog(
            onDismissRequest = onDismissError,
            title = { Text(text = stringResource(R.string.paywall_error_title)) },
            text = { Text(text = stringResource(uiState.purchaseError.messageRes())) },
            confirmButton = {
                TextButton(onClick = onDismissError) {
                    Text(text = stringResource(R.string.common_ok))
                }
            },
        )
    }

    if (uiState.showRestoreInfo) {
        AlertDialog(
            onDismissRequest = onDismissInfo,
            title = { Text(text = stringResource(R.string.paywall_info_title)) },
            text = { Text(text = stringResource(R.string.paywall_restore_no_purchases)) },
            confirmButton = {
                TextButton(onClick = onDismissInfo) {
                    Text(text = stringResource(R.string.common_ok))
                }
            },
        )
    }

    Dialog(
        onDismissRequest = {
            if (!uiState.isProcessing) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = !uiState.isProcessing,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {},
                        actions = {
                            IconButton(
                                onClick = onDismiss,
                                enabled = !uiState.isProcessing,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = stringResource(R.string.common_cancel),
                                )
                            }
                        },
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    PaywallHeader(
                        copySpec = copySpec,
                        uiState = uiState,
                    )
                    PaywallBenefits(copySpec = copySpec)
                    if (copySpec.includeSectionTitleRes != null && copySpec.includeItemRes.isNotEmpty()) {
                        PaywallIncludeSection(copySpec = copySpec)
                    }
                    PaywallPlansSection(
                        uiState = uiState,
                        copySpec = copySpec,
                        locale = locale,
                        onSelectProduct = onSelectProduct,
                    )
                    PaywallActions(
                        uiState = uiState,
                        copySpec = copySpec,
                        onPurchase = {
                            if (activity != null) {
                                onPurchase(activity)
                            }
                        },
                        onDismiss = onDismiss,
                    )
                    PaywallLegal(copySpec = copySpec)
                    PaywallLinks(
                        config = config,
                        isProcessing = uiState.isProcessing,
                        onRestore = onRestore,
                        onOpenUrl = uriHandler::openUri,
                    )
                }
            }
        }
    }
}

@Composable
private fun PaywallHeader(
    copySpec: PaywallCopySpec,
    uiState: PaywallUiState,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.paywall_badge_pro),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(999.dp),
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
            Text(
                text = stringResource(copySpec.titleRes),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(copySpec.subtitleRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (uiState.request.consumedToday >= uiState.request.dailyLimit) {
                Text(
                    text = stringResource(
                        R.string.paywall_subtitle_limit_format,
                        uiState.request.consumedToday,
                        uiState.request.dailyLimit,
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PaywallBenefits(
    copySpec: PaywallCopySpec,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SectionTitle(title = stringResource(copySpec.benefitsTitleRes))
            copySpec.bulletRes.take(3).forEach { bulletRes ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(bulletRes),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun PaywallIncludeSection(
    copySpec: PaywallCopySpec,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SectionTitle(title = stringResource(copySpec.includeSectionTitleRes!!))
            copySpec.includeItemRes.forEach { includeRes ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .size(6.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                shape = CircleShape,
                            )
                    )
                    Text(
                        text = stringResource(includeRes),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun PaywallPlansSection(
    uiState: PaywallUiState,
    copySpec: PaywallCopySpec,
    locale: Locale,
    onSelectProduct: (String) -> Unit,
) {
    val trialTemplate = stringResource(R.string.paywall_trial_incentive_format)
    val genericPeriodText = stringResource(R.string.paywall_period_generic)
    val trialText = remember(uiState.products, locale, trialTemplate) {
        trialBannerText(
            products = uiState.products,
            locale = locale,
            trialTemplate = trialTemplate,
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SectionTitle(title = stringResource(copySpec.plansTitleRes))
            if (trialText != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(999.dp),
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = trialText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            if (uiState.products.isEmpty()) {
                Text(
                    text = stringResource(R.string.paywall_price_loading),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                uiState.products.forEach { product ->
                    PlanCard(
                        product = product,
                        isSelected = uiState.selectedProductId == product.id,
                        copySpec = copySpec,
                        locale = locale,
                        genericPeriodText = genericPeriodText,
                        onSelect = { onSelectProduct(product.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun PlanCard(
    product: PremiumProduct,
    isSelected: Boolean,
    copySpec: PaywallCopySpec,
    locale: Locale,
    genericPeriodText: String,
    onSelect: () -> Unit,
) {
    val label = when (product.planKind) {
        PremiumPlanKind.Yearly -> stringResource(copySpec.annualLabelRes)
        PremiumPlanKind.Monthly -> stringResource(copySpec.monthlyLabelRes)
        PremiumPlanKind.Other -> product.title
    }
    val badge = when (product.planKind) {
        PremiumPlanKind.Yearly -> stringResource(copySpec.annualBadgeRes)
        PremiumPlanKind.Monthly -> copySpec.monthlyBadgeRes?.let { stringResource(it) }
        PremiumPlanKind.Other -> null
    }
    val priceLine = remember(product, locale, genericPeriodText) {
        priceLine(
            product = product,
            locale = locale,
            genericPeriodText = genericPeriodText,
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSelect)
                .then(
                    if (isSelected) {
                        Modifier.border(
                            width = 1.5.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(14.dp),
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                if (badge != null) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                shape = RoundedCornerShape(999.dp),
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }
            Text(
                text = priceLine,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
private fun PaywallActions(
    uiState: PaywallUiState,
    copySpec: PaywallCopySpec,
    onPurchase: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Button(
            onClick = onPurchase,
            enabled = !uiState.isProcessing && !uiState.isLoadingProducts && uiState.selectedProductId != null,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(copySpec.ctaPrimaryRes))
        }

        if (copySpec.ctaSecondaryRes != null) {
            TextButton(
                onClick = {
                    if (copySpec.ctaSecondaryAction == PaywallSecondaryAction.Dismiss && !uiState.isProcessing) {
                        onDismiss()
                    }
                },
                enabled = !uiState.isProcessing,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(text = stringResource(copySpec.ctaSecondaryRes))
            }
        }

        Text(
            text = stringResource(copySpec.trustLineRes),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PaywallLegal(
    copySpec: PaywallCopySpec,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(copySpec.legalLine1Res),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(copySpec.legalLine2Res),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun PaywallLinks(
    config: PaywallConfig,
    isProcessing: Boolean,
    onRestore: () -> Unit,
    onOpenUrl: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextButton(
            onClick = onRestore,
            enabled = !isProcessing,
        ) {
            Text(text = stringResource(R.string.paywall_button_restore))
        }

        if (config.manageSubscriptionUri != null) {
            TextButton(
                onClick = { onOpenUrl(config.manageSubscriptionUri.toString()) },
                enabled = !isProcessing,
            ) {
                Text(text = stringResource(R.string.paywall_button_manage))
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            if (config.termsUri != null) {
                TextButton(
                    onClick = { onOpenUrl(config.termsUri.toString()) },
                    enabled = !isProcessing,
                ) {
                    Text(text = stringResource(R.string.paywall_button_terms))
                }
            }
            if (config.privacyUri != null) {
                TextButton(
                    onClick = { onOpenUrl(config.privacyUri.toString()) },
                    enabled = !isProcessing,
                ) {
                    Text(text = stringResource(R.string.paywall_button_privacy))
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold,
    )
}

private fun PremiumPurchaseError.messageRes(): Int = when (this) {
    PremiumPurchaseError.ProductUnavailable -> R.string.paywall_error_product_unavailable
    PremiumPurchaseError.FailedVerification -> R.string.paywall_error_failed_verification
    PremiumPurchaseError.UserCancelled -> R.string.paywall_error_user_cancelled
    PremiumPurchaseError.Pending -> R.string.paywall_error_pending
    PremiumPurchaseError.Unknown -> R.string.paywall_error_unknown
}

private fun trialBannerText(
    products: List<PremiumProduct>,
    locale: Locale,
    trialTemplate: String,
): String? {
    val annualPeriod = products.firstOrNull { it.planKind == PremiumPlanKind.Yearly }?.freeTrialPeriod
    val monthlyPeriod = products.firstOrNull { it.planKind == PremiumPlanKind.Monthly }?.freeTrialPeriod
    val selectedPeriod = when {
        annualPeriod != null && monthlyPeriod != null && annualPeriod == monthlyPeriod -> annualPeriod
        annualPeriod != null -> annualPeriod
        else -> monthlyPeriod
    } ?: return null

    val formattedPeriod = formatFullPeriod(locale = locale, period = selectedPeriod)
        ?: return null
    return formatWithArgument(
        locale = locale,
        template = trialTemplate,
        value = formattedPeriod,
    )
}

private fun priceLine(
    product: PremiumProduct,
    locale: Locale,
    genericPeriodText: String,
): String {
    val periodText = product.recurringPeriod?.let { recurringPeriod ->
        formatPeriodUnit(locale = locale, period = recurringPeriod)
    } ?: genericPeriodText
    return "${product.formattedPrice}/$periodText"
}

private fun formatPeriodUnit(
    locale: Locale,
    period: BillingPeriod,
): String {
    val fullPeriod = formatFullPeriod(locale = locale, period = period) ?: return ""
    if (period.value != 1) {
        return fullPeriod
    }

    val one = NumberFormat.getIntegerInstance(locale).format(1)
    val trimmed = fullPeriod.trim()
    return trimmed.removePrefix(one).trim().ifEmpty { fullPeriod }
}

private fun formatFullPeriod(
    locale: Locale,
    period: BillingPeriod,
): String? {
    val measureUnit = when (period.unit) {
        BillingPeriodUnit.Day -> MeasureUnit.DAY
        BillingPeriodUnit.Week -> MeasureUnit.WEEK
        BillingPeriodUnit.Month -> MeasureUnit.MONTH
        BillingPeriodUnit.Year -> MeasureUnit.YEAR
    }
    return MeasureFormat.getInstance(locale, MeasureFormat.FormatWidth.WIDE)
        .format(Measure(period.value, measureUnit))
}

private fun formatWithArgument(
    locale: Locale,
    template: String,
    value: String,
): String = String.format(locale, template, value)

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
