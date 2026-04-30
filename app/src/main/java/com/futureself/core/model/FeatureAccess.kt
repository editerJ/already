package com.futureself.core.model

enum class AccessTier {
    FREE,
    REWARDED_DAY_PASS,
    SUBSCRIPTION
}

enum class RewardWindow {
    SESSION,
    ONE_DAY,
    ONE_WEEK
}

enum class FutureSelfFeature(
    val displayName: String,
    val summary: String
) {
    JOURNAL_WRITE(
        displayName = "Future journaling",
        summary = "The core writing flow stays free."
    ),
    DAILY_MISSION(
        displayName = "Daily mission",
        summary = "One concrete action connected to the future journal."
    ),
    AI_GUIDE(
        displayName = "AI guide",
        summary = "Writer's block support and focused questioning."
    ),
    WOOP_DEEP_DIVE(
        displayName = "WOOP deep dive",
        summary = "Obstacle, emotion, and if-then planning."
    ),
    TIMELINE(
        displayName = "Timeline",
        summary = "Review 1, 3, 5, and 10-year entries in one place."
    ),
    FUTURE_LETTER(
        displayName = "Future letter",
        summary = "AI-crafted letter framed from the future self."
    ),
    HISTORY_REVIEW(
        displayName = "History review",
        summary = "Compare old entries against the current situation."
    ),
    BALANCE_REPORT(
        displayName = "Balance report",
        summary = "Long-range pattern summaries across domains."
    ),
    CLOUD_BACKUP(
        displayName = "Cloud backup",
        summary = "Cross-device sync and account-based recovery."
    )
}

data class FeatureGate(
    val feature: FutureSelfFeature,
    val accessTier: AccessTier,
    val rewardWindow: RewardWindow? = null,
    val freeLimitHint: String? = null
)

object RewardCatalog {
    private val gates = listOf(
        FeatureGate(
            feature = FutureSelfFeature.JOURNAL_WRITE,
            accessTier = AccessTier.FREE,
            freeLimitHint = "Free forever. Keep the core habit frictionless."
        ),
        FeatureGate(
            feature = FutureSelfFeature.DAILY_MISSION,
            accessTier = AccessTier.FREE,
            freeLimitHint = "Free forever. Mission generation is core retention."
        ),
        FeatureGate(
            feature = FutureSelfFeature.AI_GUIDE,
            accessTier = AccessTier.REWARDED_DAY_PASS,
            rewardWindow = RewardWindow.ONE_DAY
        ),
        FeatureGate(
            feature = FutureSelfFeature.WOOP_DEEP_DIVE,
            accessTier = AccessTier.REWARDED_DAY_PASS,
            rewardWindow = RewardWindow.ONE_DAY
        ),
        FeatureGate(
            feature = FutureSelfFeature.TIMELINE,
            accessTier = AccessTier.REWARDED_DAY_PASS,
            rewardWindow = RewardWindow.ONE_DAY
        ),
        FeatureGate(
            feature = FutureSelfFeature.FUTURE_LETTER,
            accessTier = AccessTier.REWARDED_DAY_PASS,
            rewardWindow = RewardWindow.SESSION
        ),
        FeatureGate(
            feature = FutureSelfFeature.HISTORY_REVIEW,
            accessTier = AccessTier.SUBSCRIPTION
        ),
        FeatureGate(
            feature = FutureSelfFeature.BALANCE_REPORT,
            accessTier = AccessTier.SUBSCRIPTION
        ),
        FeatureGate(
            feature = FutureSelfFeature.CLOUD_BACKUP,
            accessTier = AccessTier.SUBSCRIPTION
        )
    )

    fun all(): List<FeatureGate> = gates

    fun gateFor(feature: FutureSelfFeature): FeatureGate = gates.first { it.feature == feature }
}
