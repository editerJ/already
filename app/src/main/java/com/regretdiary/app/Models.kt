package com.regretdiary.app

data class Entry(
    val id: String,
    val title: String,
    val situation: String,
    val feeling: String,
    val mentalState: String,
    val lesson: String,
    val aiReflection: String,
    val tag: String,
    val timestamp: Long
) {
    val dateKey: String
        get() = timestamp.toDateKey()
}

enum class TabItem { HOME, INSIGHTS, ARCHIVE, SETTINGS }

enum class AppLanguage { KO, EN, JA }

enum class AppFontStyle { SANS, SERIF, MONO, ROUNDED }

enum class RewardFeature { AI_COACH, TREND_REPORT, PDF_EXPORT }

const val REWARDED_ACCESS_DURATION_MS: Long = 24L * 60L * 60L * 1000L

data class RewardAdUiState(
    val isInitializing: Boolean = true,
    val readyFeatures: Set<RewardFeature> = emptySet(),
    val loadingFeatures: Set<RewardFeature> = emptySet(),
    val showingFeature: RewardFeature? = null,
    val statusMessage: String = ""
) {
    fun isReady(feature: RewardFeature): Boolean = readyFeatures.contains(feature)

    fun isBusy(feature: RewardFeature): Boolean {
        return loadingFeatures.contains(feature) || showingFeature == feature
    }
}

data class ProductState(
    val lockEnabled: Boolean = false,
    val sortDescending: Boolean = true,
    val onboardingDone: Boolean = false,
    val language: AppLanguage = AppLanguage.KO,
    val fontStyle: AppFontStyle = AppFontStyle.SANS,
    val aiCoachUnlockedUntil: Long = 0L,
    val trendUnlockedUntil: Long = 0L,
    val pdfExportCredits: Int = 0
) {
    val aiCoachUnlocked: Boolean
        get() = aiCoachUnlockedUntil == Long.MAX_VALUE || aiCoachUnlockedUntil > System.currentTimeMillis()

    val trendUnlocked: Boolean
        get() = trendUnlockedUntil == Long.MAX_VALUE || trendUnlockedUntil > System.currentTimeMillis()
}

data class ReflectionDraft(
    val reflection: String,
    val lesson: String,
    val tag: String,
    val mentalState: String,
    val rewriteSuggestion: String
)

data class InsightReport(
    val pattern: String,
    val need: String,
    val action: String,
    val compass: String
)

data class JournalStats(
    val totalEntries: Int = 0,
    val activeDays: Int = 0,
    val currentStreak: Int = 0,
    val thisMonthEntries: Int = 0,
    val topTag: String = "",
    val topMentalState: String = ""
)

data class CoachProfile(
    val writingStyle: String,
    val moodSummary: String,
    val depressionSignal: String,
    val mbtiGuess: String,
    val advice: String
)

data class TrendPoint(
    val label: String,
    val score: Float,
    val count: Int
)

data class TrendReport(
    val weeklyPoints: List<TrendPoint>,
    val monthlyPoints: List<TrendPoint>,
    val outlookPoints: List<TrendPoint>,
    val summary: String,
    val outlook: String
)

data class PdfExportResult(
    val success: Boolean,
    val message: String,
    val path: String? = null
)

fun ProductState.withReward(feature: RewardFeature, now: Long = System.currentTimeMillis()): ProductState {
    return when (feature) {
        RewardFeature.AI_COACH -> copy(
            aiCoachUnlockedUntil = extendRewardWindow(aiCoachUnlockedUntil, now)
        )
        RewardFeature.TREND_REPORT -> copy(
            trendUnlockedUntil = extendRewardWindow(trendUnlockedUntil, now)
        )
        RewardFeature.PDF_EXPORT -> copy(pdfExportCredits = pdfExportCredits + 1)
    }
}

fun ProductState.consumePdfCredit(): ProductState {
    return if (pdfExportCredits > 0) copy(pdfExportCredits = pdfExportCredits - 1) else this
}

private fun extendRewardWindow(currentUntil: Long, now: Long): Long {
    if (currentUntil == Long.MAX_VALUE) return Long.MAX_VALUE
    val base = maxOf(currentUntil, now)
    return base + REWARDED_ACCESS_DURATION_MS
}
