package com.regretdiary.app

import androidx.fragment.app.FragmentActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

// Replace with your production rewarded ad unit IDs before Play Store release.
private const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

class RewardAdsManager(
    private val activity: FragmentActivity,
    private val getLanguage: () -> AppLanguage,
    private val onStateChanged: (RewardAdUiState) -> Unit,
    private val onRewardEarned: (RewardFeature) -> Unit
) {
    private val rewardedAds = mutableMapOf<RewardFeature, RewardedAd?>()
    private var isStarted = false
    private var uiState = RewardAdUiState(
        statusMessage = localized(
            "광고 보상을 준비하는 중이에요.",
            "Preparing rewarded ads.",
            "リワード広告を準備しています。"
        )
    )

    fun start() {
        if (isStarted) {
            RewardFeature.entries.forEach(::loadAd)
            return
        }
        isStarted = true
        updateState(
            uiState.copy(
                isInitializing = true,
                statusMessage = localized(
                    "광고 SDK를 초기화하는 중이에요.",
                    "Initializing the ads SDK.",
                    "広告SDKを初期化しています。"
                )
            )
        )
        MobileAds.initialize(activity) {
            updateState(
                uiState.copy(
                    isInitializing = false,
                    statusMessage = localized(
                        "광고 보상 기능이 준비되었어요.",
                        "Rewarded ads are ready.",
                        "リワード広告の準備ができました。"
                    )
                )
            )
            RewardFeature.entries.forEach(::loadAd)
        }
    }

    fun showRewardedAd(feature: RewardFeature) {
        if (uiState.isInitializing) {
            start()
            updateState(
                uiState.copy(
                    statusMessage = localized(
                        "광고 초기화가 끝나면 다시 시도해주세요.",
                        "Please try again once ad initialization completes.",
                        "広告の初期化が終わったら、もう一度お試しください。"
                    )
                )
            )
            return
        }

        val ad = rewardedAds[feature]
        if (ad == null) {
            loadAd(feature, announce = true)
            updateState(
                uiState.copy(
                    statusMessage = localized(
                        "${featureLabel(feature)} 광고를 불러오는 중이에요. 잠시 후 다시 눌러주세요.",
                        "Loading the ${featureLabel(feature)} ad. Please try again in a moment.",
                        "${featureLabel(feature)} の広告を読み込み中です。少ししてからもう一度押してください。"
                    )
                )
            )
            return
        }

        rewardedAds[feature] = null
        updateState(
            uiState.copy(
                readyFeatures = uiState.readyFeatures - feature,
                showingFeature = feature,
                statusMessage = localized(
                    "${featureLabel(feature)} 광고를 재생하는 중이에요.",
                    "Showing the ${featureLabel(feature)} ad.",
                    "${featureLabel(feature)} の広告を再生しています。"
                )
            )
        )
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                updateState(uiState.copy(showingFeature = null))
                loadAd(feature)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                updateState(
                    uiState.copy(
                        showingFeature = null,
                        statusMessage = localized(
                            "광고를 보여주지 못했어요. 다시 시도해주세요.",
                            "The ad could not be shown. Please try again.",
                            "広告を表示できませんでした。もう一度お試しください。"
                        )
                    )
                )
                loadAd(feature)
            }
        }
        ad.show(activity) { _: RewardItem ->
            onRewardEarned(feature)
            updateState(
                uiState.copy(
                    statusMessage = localized(
                        rewardGrantedMessage(feature, AppLanguage.KO),
                        rewardGrantedMessage(feature, AppLanguage.EN),
                        rewardGrantedMessage(feature, AppLanguage.JA)
                    )
                )
            )
        }
    }

    fun end() {
        rewardedAds.clear()
    }

    private fun loadAd(feature: RewardFeature, announce: Boolean = false) {
        if (uiState.loadingFeatures.contains(feature) || rewardedAds[feature] != null) return

        updateState(
            uiState.copy(
                loadingFeatures = uiState.loadingFeatures + feature,
                statusMessage = if (announce) {
                    localized(
                        "${featureLabel(feature)} 광고를 준비하는 중이에요.",
                        "Preparing the ${featureLabel(feature)} ad.",
                        "${featureLabel(feature)} の広告を準備しています。"
                    )
                } else {
                    uiState.statusMessage
                }
            )
        )

        RewardedAd.load(
            activity,
            TEST_REWARDED_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAds[feature] = ad
                    updateState(
                        uiState.copy(
                            loadingFeatures = uiState.loadingFeatures - feature,
                            readyFeatures = uiState.readyFeatures + feature
                        )
                    )
                }

                override fun onAdFailedToLoad(loadAdError: com.google.android.gms.ads.LoadAdError) {
                    rewardedAds.remove(feature)
                    updateState(
                        uiState.copy(
                            loadingFeatures = uiState.loadingFeatures - feature,
                            readyFeatures = uiState.readyFeatures - feature,
                            statusMessage = localized(
                                "${featureLabel(feature)} 광고를 불러오지 못했어요. 네트워크를 확인하고 다시 시도해주세요.",
                                "The ${featureLabel(feature)} ad failed to load. Check your network and try again.",
                                "${featureLabel(feature)} の広告を読み込めませんでした。ネットワークを確認してもう一度お試しください。"
                            )
                        )
                    )
                }
            }
        )
    }

    private fun rewardGrantedMessage(feature: RewardFeature, language: AppLanguage): String {
        return when (feature) {
            RewardFeature.AI_COACH -> loc(
                language,
                "AI 코치가 24시간 열렸어요.",
                "AI coach unlocked for 24 hours.",
                "AIコーチが24時間開きました。"
            )
            RewardFeature.TREND_REPORT -> loc(
                language,
                "감정 그래프가 24시간 열렸어요.",
                "Emotion graphs unlocked for 24 hours.",
                "感情グラフが24時間開きました。"
            )
            RewardFeature.PDF_EXPORT -> loc(
                language,
                "PDF 내보내기 1회권을 획득했어요.",
                "You earned 1 PDF export credit.",
                "PDF書き出し1回分を獲得しました。"
            )
        }
    }

    private fun featureLabel(feature: RewardFeature): String {
        return when (feature) {
            RewardFeature.AI_COACH -> localized("AI 코치", "AI coach", "AIコーチ")
            RewardFeature.TREND_REPORT -> localized("감정 그래프", "emotion graph", "感情グラフ")
            RewardFeature.PDF_EXPORT -> localized("PDF 저장", "PDF export", "PDF保存")
        }
    }

    private fun updateState(nextState: RewardAdUiState) {
        uiState = nextState
        onStateChanged(nextState)
    }

    private fun localized(ko: String, en: String, ja: String): String {
        return loc(getLanguage(), ko, en, ja)
    }
}
