package com.alejandroestevemaza.gymtimerpro.app

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.runtime.produceState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.LocalEnergySavingActive
import com.alejandroestevemaza.gymtimerpro.core.model.EnergySavingMode
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alejandroestevemaza.gymtimerpro.R
import com.alejandroestevemaza.gymtimerpro.app.navigation.AppTab
import com.alejandroestevemaza.gymtimerpro.app.navigation.PremiumBottomNavigationBar
import com.alejandroestevemaza.gymtimerpro.core.designsystem.component.ProLockedOverlay
import com.alejandroestevemaza.gymtimerpro.core.designsystem.theme.GymTimerProTheme
import com.alejandroestevemaza.gymtimerpro.core.model.DailyUsageState
import com.alejandroestevemaza.gymtimerpro.core.model.TrainingDefaults
import com.alejandroestevemaza.gymtimerpro.data.preferences.AppContainer
import com.alejandroestevemaza.gymtimerpro.feature.progress.ui.ProgressRoute
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallEntryPoint
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallInfoLevel
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallPresentationContext
import com.alejandroestevemaza.gymtimerpro.feature.paywall.model.PaywallPresentationRequest
import com.alejandroestevemaza.gymtimerpro.feature.paywall.ui.PaywallDialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alejandroestevemaza.gymtimerpro.feature.routines.ui.ClassificationManagerDialog
import com.alejandroestevemaza.gymtimerpro.feature.routines.ui.ClassificationsViewModel
import com.alejandroestevemaza.gymtimerpro.feature.routines.ui.RoutinesRoute
import com.alejandroestevemaza.gymtimerpro.feature.settings.ui.SettingsScreen
import com.alejandroestevemaza.gymtimerpro.feature.training.ui.TrainingRoute
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.launch

@Composable
fun GymTimerProApp(
    appContainer: AppContainer,
) {
    val coroutineScope = rememberCoroutineScope()
    var paywallRequest by remember { mutableStateOf<PaywallPresentationRequest?>(null) }
    val settings by appContainer.appSettingsRepository.settings.collectAsStateWithLifecycle(
        initialValue = com.alejandroestevemaza.gymtimerpro.core.model.AppSettings()
    )
    val isPro by appContainer.premiumStateRepository.isPro.collectAsStateWithLifecycle(
        initialValue = false
    )
    val rawDailyUsage by appContainer.trainingSessionRepository.dailyUsageState.collectAsStateWithLifecycle(
        initialValue = DailyUsageState()
    )
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val dailyUsage = remember(rawDailyUsage) {
        normalizedDailyUsage(rawDailyUsage)
    }
    val classificationsViewModel: ClassificationsViewModel = viewModel(
        factory = ClassificationsViewModel.factory(appContainer.routinesRepository)
    )
    val classificationsUiState by classificationsViewModel.uiState.collectAsStateWithLifecycle()

    val energySavingActive = isEnergySavingActive(settings.energySavingMode)

    GymTimerProTheme {
        CompositionLocalProvider(LocalEnergySavingActive provides energySavingActive) {
        paywallRequest?.let { request ->
            PaywallDialog(
                appContainer = appContainer,
                request = request,
                onDismiss = { paywallRequest = null },
            )
        }
        if (classificationsUiState.isOpen) {
            ClassificationManagerDialog(
                classifications = classificationsUiState.classifications,
                searchQuery = classificationsUiState.searchQuery,
                draft = classificationsUiState.draft,
                onClose = classificationsViewModel::closeManager,
                onSearchChanged = classificationsViewModel::onSearchQueryChanged,
                onStartCreate = classificationsViewModel::onStartCreate,
                onStartRename = classificationsViewModel::onStartRename,
                onDraftChanged = classificationsViewModel::onDraftChanged,
                onCancelDraft = classificationsViewModel::onCancelDraft,
                onSaveDraft = classificationsViewModel::onSaveDraft,
                onDelete = classificationsViewModel::onDelete,
            )
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                PremiumBottomNavigationBar(
                    tabs = AppTab.entries,
                    currentDestination = currentDestination,
                    onTabSelected = { tab ->
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppTab.Training.route,
                modifier = Modifier.padding(innerPadding),
                enterTransition = {
                    if (energySavingActive) EnterTransition.None
                    else fadeIn(animationSpec = tween(durationMillis = 180))
                },
                exitTransition = {
                    if (energySavingActive) ExitTransition.None
                    else fadeOut(animationSpec = tween(durationMillis = 140))
                },
                popEnterTransition = {
                    if (energySavingActive) EnterTransition.None
                    else fadeIn(animationSpec = tween(durationMillis = 180))
                },
                popExitTransition = {
                    if (energySavingActive) ExitTransition.None
                    else fadeOut(animationSpec = tween(durationMillis = 140))
                },
            ) {
                composable(AppTab.Training.route) {
                    TrainingRoute(
                        appContainer = appContainer,
                        onRequestPaywall = { request -> paywallRequest = request },
                    )
                }
                composable(AppTab.Routines.route) {
                    PremiumFeatureGate(
                        isUnlocked = isPro,
                        title = stringResource(R.string.pro_locked_routines_title),
                        message = stringResource(R.string.pro_locked_message),
                        actionTitle = stringResource(R.string.pro_locked_unlock),
                        onUnlock = {
                            paywallRequest = PaywallPresentationRequest(
                                context = PaywallPresentationContext(
                                    entryPoint = PaywallEntryPoint.ProModule,
                                    infoLevel = PaywallInfoLevel.Standard,
                                ),
                                dailyLimit = TrainingDefaults.dailyFreeUsageLimit,
                                consumedToday = dailyUsage.consumedCount,
                            )
                        },
                    ) {
                        RoutinesRoute(appContainer = appContainer)
                    }
                }
                composable(AppTab.Progress.route) {
                    PremiumFeatureGate(
                        isUnlocked = isPro,
                        title = stringResource(R.string.pro_locked_progress_title),
                        message = stringResource(R.string.pro_locked_message),
                        actionTitle = stringResource(R.string.pro_locked_unlock),
                        onUnlock = {
                            paywallRequest = PaywallPresentationRequest(
                                context = PaywallPresentationContext(
                                    entryPoint = PaywallEntryPoint.ProModule,
                                    infoLevel = PaywallInfoLevel.Standard,
                                ),
                                dailyLimit = TrainingDefaults.dailyFreeUsageLimit,
                                consumedToday = dailyUsage.consumedCount,
                            )
                        },
                    ) {
                        ProgressRoute(appContainer = appContainer)
                    }
                }
                composable(AppTab.Settings.route) {
                    PremiumFeatureGate(
                        isUnlocked = isPro,
                        title = stringResource(R.string.pro_locked_settings_title),
                        message = stringResource(R.string.pro_locked_message),
                        actionTitle = stringResource(R.string.pro_locked_unlock),
                        onUnlock = {
                            paywallRequest = PaywallPresentationRequest(
                                context = PaywallPresentationContext(
                                    entryPoint = PaywallEntryPoint.ProModule,
                                    infoLevel = PaywallInfoLevel.Standard,
                                ),
                                dailyLimit = TrainingDefaults.dailyFreeUsageLimit,
                                consumedToday = dailyUsage.consumedCount,
                            )
                        },
                    ) {
                        SettingsScreen(
                            settings = settings,
                            onWeightUnitPreferenceSelected = { value ->
                                coroutineScope.launch {
                                    appContainer.appSettingsRepository.setWeightUnitPreference(value)
                                }
                            },
                            onTimerDisplayFormatSelected = { value ->
                                coroutineScope.launch {
                                    appContainer.appSettingsRepository.setTimerDisplayFormat(value)
                                }
                            },
                            onMaxSetsPreferenceSelected = { value ->
                                coroutineScope.launch {
                                    appContainer.appSettingsRepository.setMaxSetsPreference(value)
                                }
                            },
                            onRestIncrementPreferenceSelected = { value ->
                                coroutineScope.launch {
                                    appContainer.appSettingsRepository.setRestIncrementPreference(value)
                                }
                            },
                            onEnergySavingModeSelected = { value ->
                                coroutineScope.launch {
                                    appContainer.appSettingsRepository.setEnergySavingMode(value)
                                }
                            },
                            onManageClassifications = classificationsViewModel::openManager,
                        )
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun PremiumFeatureGate(
    isUnlocked: Boolean,
    title: String,
    message: String,
    actionTitle: String,
    onUnlock: () -> Unit,
    content: @Composable () -> Unit,
) {
    ProLockedOverlay(
        isUnlocked = isUnlocked,
        title = title,
        message = message,
        actionText = actionTitle,
        onUnlock = onUnlock,
        content = content,
    )
}

@Composable
private fun isEnergySavingActive(mode: EnergySavingMode): Boolean {
    val context = LocalContext.current
    val initialBatteryLow = remember(mode, context) {
        if (mode != EnergySavingMode.Automatic) return@remember false
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        isBatteryLow(intent)
    }
    val isLowBattery by produceState(initialValue = initialBatteryLow, mode) {
        if (mode == EnergySavingMode.Automatic) {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context, intent: Intent) {
                    value = isBatteryLow(intent)
                }
            }
            context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            awaitDispose { context.unregisterReceiver(receiver) }
        }
    }
    return when (mode) {
        EnergySavingMode.On -> true
        EnergySavingMode.Off -> false
        EnergySavingMode.Automatic -> isLowBattery
    }
}

private fun isBatteryLow(intent: Intent?): Boolean {
    val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: return false
    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
    return level >= 0 && scale > 0 && (level * 100 / scale) < BATTERY_LOW_THRESHOLD
}

private const val BATTERY_LOW_THRESHOLD = 20

private fun normalizedDailyUsage(
    current: DailyUsageState,
    zoneId: ZoneId = ZoneId.systemDefault(),
): DailyUsageState {
    val todayStartEpochMillis = Instant.now()
        .atZone(zoneId)
        .truncatedTo(ChronoUnit.DAYS)
        .toInstant()
        .toEpochMilli()
    return if (current.dayStartEpochMillis == todayStartEpochMillis) {
        current
    } else {
        DailyUsageState(
            dayStartEpochMillis = todayStartEpochMillis,
            consumedCount = 0,
        )
    }
}
