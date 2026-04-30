package com.futureself.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.futureself.core.i18n.FutureSelfLanguages
import com.futureself.core.notifications.FutureSelfReminderScheduler
import com.futureself.core.privacy.FutureSelfConsentManager
import com.futureself.data.preferences.FutureSelfRewardStore
import com.futureself.ui.component.BottomNav
import com.futureself.ui.home.HomeScreen
import com.futureself.ui.settings.SettingsScreen
import com.futureself.ui.theme.FutureSelfTheme
import com.futureself.ui.theme.Ink050
import com.futureself.ui.timeline.TimelineScreen
import com.futureself.ui.write.AiGuideSheet
import com.futureself.ui.write.WriteScreen
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.already.app.BuildConfig
import com.already.app.R

@Composable
fun FutureSelfApp(
    launchQuickWin: Boolean = false,
    onLaunchQuickWinConsumed: () -> Unit = {}
) {
    val context = LocalContext.current
    val rewardStore = remember(context) { FutureSelfRewardStore(context.applicationContext) }
    val reminderScheduler = remember(context) { FutureSelfReminderScheduler(context.applicationContext) }
    val consentManager = remember(context) { FutureSelfConsentManager(context.applicationContext) }
    val factory = remember(rewardStore, reminderScheduler) {
        FutureSelfViewModel.factory(
            rewardStore = rewardStore,
            reminderScheduler = reminderScheduler
        )
    }
    val viewModel: FutureSelfViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = context.findActivity()

    LaunchedEffect(Unit) {
        MobileAds.initialize(context)
    }

    LaunchedEffect(activity) {
        if (activity != null) {
            consentManager.requestConsent(activity) { state ->
                viewModel.updatePrivacyOptionsAvailable(state.isPrivacyOptionsRequired)
            }
        }
    }

    LaunchedEffect(launchQuickWin) {
        if (launchQuickWin) {
            viewModel.showQuickWinSheet()
            onLaunchQuickWinConsumed()
        }
    }

    FutureSelfAppContent(
        uiState = uiState,
        onTabSelected = viewModel::onTabSelected,
        onYearSelected = viewModel::onYearSelected,
        onDomainSelected = viewModel::onDomainSelected,
        onDraftChanged = viewModel::onDraftChanged,
        onMissionToggle = viewModel::toggleMissionCompleted,
        onSave = viewModel::saveDraft,
        onShowAiGuide = viewModel::showAiGuide,
        onDismissAiGuide = viewModel::dismissAiGuide,
        onStartWritingForYear = viewModel::startWritingForYear,
        onShowQuickWinSheet = viewModel::showQuickWinSheet,
        onDismissQuickWinSheet = viewModel::dismissQuickWinSheet,
        onQuickWinDraftChanged = viewModel::onQuickWinDraftChanged,
        onSaveQuickWin = viewModel::saveQuickWin,
        onShowLanguageDialog = viewModel::showLanguageDialog,
        onDismissLanguageDialog = viewModel::dismissLanguageDialog,
        onLanguageSelected = { languageTag ->
            viewModel.selectLanguage(languageTag)
            FutureSelfLanguages.apply(languageTag)
        },
        onWatchRewardAd = rememberRewardHandler(
            onRewardGranted = viewModel::activateCoachingReward
        ),
        onShowPrivacyPolicy = viewModel::showPrivacyPolicy,
        onShowTerms = viewModel::showTerms,
        onDismissLegalDialog = viewModel::dismissLegalDialog,
        onShowResetConfirmation = viewModel::showResetConfirmation,
        onDismissResetConfirmation = viewModel::dismissResetConfirmation,
        onConfirmResetAllData = viewModel::confirmResetAllData,
        onShowPrivacyChoices = {
            if (activity != null) {
                consentManager.showPrivacyOptions(activity) { state ->
                    viewModel.updatePrivacyOptionsAvailable(state.isPrivacyOptionsRequired)
                }
            }
        }
    )
}

@Composable
private fun FutureSelfAppContent(
    uiState: FutureSelfUiState,
    onTabSelected: (FutureSelfTab) -> Unit,
    onYearSelected: (Int) -> Unit,
    onDomainSelected: (String) -> Unit,
    onDraftChanged: (String) -> Unit,
    onMissionToggle: () -> Unit,
    onSave: () -> Unit,
    onShowAiGuide: () -> Unit,
    onDismissAiGuide: () -> Unit,
    onStartWritingForYear: (Int) -> Unit,
    onShowQuickWinSheet: () -> Unit,
    onDismissQuickWinSheet: () -> Unit,
    onQuickWinDraftChanged: (String) -> Unit,
    onSaveQuickWin: () -> Unit,
    onShowLanguageDialog: () -> Unit,
    onDismissLanguageDialog: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    onWatchRewardAd: () -> Unit,
    onShowPrivacyPolicy: () -> Unit,
    onShowTerms: () -> Unit,
    onDismissLegalDialog: () -> Unit,
    onShowResetConfirmation: () -> Unit,
    onDismissResetConfirmation: () -> Unit,
    onConfirmResetAllData: () -> Unit,
    onShowPrivacyChoices: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNav(
                selectedTab = uiState.selectedTab,
                onTabSelected = onTabSelected
            )
        },
        containerColor = Ink050
    ) { innerPadding ->
        when (uiState.selectedTab) {
            FutureSelfTab.HOME -> HomeScreen(
                uiState = uiState.homeUiState,
                onMissionCheck = onMissionToggle,
                onWriteClick = { onTabSelected(FutureSelfTab.WRITE) },
                onShowQuickWinSheet = onShowQuickWinSheet,
                onDismissQuickWinSheet = onDismissQuickWinSheet,
                onQuickWinDraftChanged = onQuickWinDraftChanged,
                onSaveQuickWin = onSaveQuickWin,
                modifier = Modifier.padding(innerPadding)
            )
            FutureSelfTab.WRITE -> WriteScreen(
                uiState = uiState.writeUiState,
                onYearChange = onYearSelected,
                onDomainChange = onDomainSelected,
                onContentChange = onDraftChanged,
                onAiGuideClick = onShowAiGuide,
                onSave = onSave,
                modifier = Modifier.padding(innerPadding)
            )
            FutureSelfTab.TIMELINE -> TimelineScreen(
                uiState = uiState.timelineUiState,
                onWriteClick = onStartWritingForYear,
                modifier = Modifier.padding(innerPadding)
            )
            FutureSelfTab.SETTINGS -> SettingsScreen(
                uiState = uiState.settingsUiState,
                onLanguageClick = onShowLanguageDialog,
                onLanguageDismiss = onDismissLanguageDialog,
                onLanguageSelected = onLanguageSelected,
                onWatchRewardAd = onWatchRewardAd,
                onShowPrivacyChoices = onShowPrivacyChoices,
                onShowPrivacyPolicy = onShowPrivacyPolicy,
                onShowTerms = onShowTerms,
                onDismissLegalDialog = onDismissLegalDialog,
                onShowResetConfirmation = onShowResetConfirmation,
                onDismissResetConfirmation = onDismissResetConfirmation,
                onConfirmResetAllData = onConfirmResetAllData,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    if (uiState.isAiGuideVisible) {
        AiGuideSheet(onDismiss = onDismissAiGuide)
    }
}

@Composable
private fun rememberRewardHandler(
    onRewardGranted: () -> Unit
): () -> Unit {
    val context = LocalContext.current
    val activity = context.findActivity()

    return remember(context, activity, onRewardGranted) {
        {
            if (activity == null) {
                onRewardGranted()
            } else {
                RewardedAd.load(
                    context,
                    BuildConfig.FUTURE_SELF_REWARDED_AD_UNIT_ID,
                    AdRequest.Builder().build(),
                    object : RewardedAdLoadCallback() {
                        override fun onAdLoaded(ad: RewardedAd) {
                            ad.show(activity) { _: RewardItem ->
                                onRewardGranted()
                            }
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.future_self_ad_load_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F4EF)
@Composable
private fun FutureSelfAppPreview() {
    if (LocalInspectionMode.current) {
        FutureSelfTheme {
            FutureSelfAppContent(
                uiState = FutureSelfUiState(selectedTab = FutureSelfTab.WRITE),
                onTabSelected = {},
                onYearSelected = {},
                onDomainSelected = {},
                onDraftChanged = {},
                onMissionToggle = {},
                onSave = {},
                onShowAiGuide = {},
                onDismissAiGuide = {},
                onStartWritingForYear = {},
                onShowQuickWinSheet = {},
                onDismissQuickWinSheet = {},
                onQuickWinDraftChanged = {},
                onSaveQuickWin = {},
                onShowLanguageDialog = {},
                onDismissLanguageDialog = {},
                onLanguageSelected = {},
                onWatchRewardAd = {},
                onShowPrivacyPolicy = {},
                onShowTerms = {},
                onDismissLegalDialog = {},
                onShowResetConfirmation = {},
                onDismissResetConfirmation = {},
                onConfirmResetAllData = {},
                onShowPrivacyChoices = {}
            )
        }
    }
}