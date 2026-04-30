package com.futureself.ui

import com.futureself.core.i18n.FutureSelfLanguages
import com.futureself.core.i18n.SupportedLanguage
import com.futureself.data.local.entity.DailyMission
import com.futureself.data.local.entity.JournalEntry
import com.futureself.util.FutureSelfConfig
import com.already.app.R
import java.time.LocalDate

enum class FutureSelfTab {
    HOME,
    WRITE,
    TIMELINE,
    SETTINGS
}

data class FutureSelfUiState(
    val selectedTab: FutureSelfTab = FutureSelfTab.HOME,
    val selectedYear: Int = 5,
    val selectedDomain: String = "self",
    val draftContent: String = "",
    val isAiGuideVisible: Boolean = false,
    val journals: List<JournalEntry> = emptyList(),
    val missionCompleted: Boolean = false,
    val streakCount: Int = 0,
    val coachingRewardEndsAt: Long = 0L,
    val preferredLanguageTag: String = FutureSelfLanguages.SYSTEM,
    val isLanguageDialogVisible: Boolean = false,
    val completedActionDates: Set<String> = emptySet(),
    val quickWins: List<QuickWinEntry> = emptyList(),
    val isQuickWinSheetVisible: Boolean = false,
    val quickWinDraft: String = "",
    val isResetConfirmVisible: Boolean = false,
    val legalDialog: LegalDialogState? = null,
    val canManagePrivacyChoices: Boolean = false
) {
    private val todayDateKey = LocalDate.now().toString()

    val isCoachingRewardActive: Boolean
        get() = coachingRewardEndsAt > System.currentTimeMillis()

    val todayMission: DailyMission
        get() = FutureSelfStrings.defaultMission(
            journalId = journals.firstOrNull()?.id.orEmpty(),
            isCompleted = missionCompleted
        )

    val homeUiState: HomeUiState
        get() {
            val latestWins = quickWins
                .sortedByDescending { it.createdAt }
                .take(3)
                .map { entry ->
                    QuickWinUiItem(
                        id = entry.id,
                        text = entry.text,
                        dateText = FutureSelfStrings.shortDate(entry.createdAt)
                    )
                }

            return HomeUiState(
                dateText = FutureSelfStrings.homeDateText(),
                greeting = FutureSelfStrings.string(R.string.future_self_home_greeting),
                streakCount = streakCount,
                mission = todayMission,
                recentJournals = journals,
                coachUiState = CoachUiState(
                    isRewardActive = isCoachingRewardActive,
                    title = FutureSelfStrings.string(R.string.future_self_coach_title),
                    nextStep = buildNextStep(todayMission, journals.firstOrNull()),
                    reminderMessage = buildReminderMessage(quickWins.firstOrNull()),
                    wins = latestWins,
                    rewardGuide = if (isCoachingRewardActive) {
                        FutureSelfStrings.string(R.string.future_self_reward_guide_active)
                    } else {
                        FutureSelfStrings.string(R.string.future_self_reward_guide_inactive)
                    }
                ),
                dreamCalendarUiState = DreamCalendarUiState(
                    monthLabel = FutureSelfStrings.monthLabel(),
                    days = buildCalendarDays(completedActionDates, todayDateKey)
                ),
                progressUiState = ProgressUiState(
                    completedActions = completedActionDates.size,
                    weeklyActionCount = completedActionDates.size.coerceAtMost(7),
                    growthScore = (completedActionDates.size * 7).coerceAtMost(100),
                    motivationMessage = when {
                        completedActionDates.isEmpty() -> {
                            FutureSelfStrings.string(R.string.future_self_progress_msg_starting)
                        }
                        completedActionDates.size < 3 -> {
                            FutureSelfStrings.string(R.string.future_self_progress_msg_growing)
                        }
                        else -> {
                            FutureSelfStrings.string(R.string.future_self_progress_msg_steady)
                        }
                    }
                ),
                quickWinUiState = QuickWinSheetUiState(
                    isVisible = isQuickWinSheetVisible,
                    draft = quickWinDraft,
                    canSave = quickWinDraft.isNotBlank()
                )
            )
        }

    val writeUiState: WriteUiState
        get() = WriteUiState(
            selectedYear = selectedYear,
            selectedDomain = selectedDomain,
            content = draftContent,
            isSaveEnabled = draftContent.isNotBlank(),
            isCoachingRewardActive = isCoachingRewardActive
        )

    val timelineUiState: TimelineUiState
        get() = TimelineUiState(
            journals = journals.sortedBy { it.targetYear }
        )

    val settingsUiState: SettingsUiState
        get() = SettingsUiState(
            selectedLanguageTag = preferredLanguageTag,
            selectedLanguageName = FutureSelfLanguages.displayName(preferredLanguageTag),
            availableLanguages = FutureSelfLanguages.supported,
            isLanguageDialogVisible = isLanguageDialogVisible,
            rewardUiState = CoachingRewardUiState(
                isActive = isCoachingRewardActive,
                expiresAtText = if (isCoachingRewardActive) {
                    FutureSelfStrings.rewardDate(coachingRewardEndsAt)
                } else {
                    null
                }
            ),
            canManagePrivacyChoices = canManagePrivacyChoices,
            supportEmail = FutureSelfConfig.supportEmail,
            isResetConfirmVisible = isResetConfirmVisible,
            legalDialog = legalDialog
        )
}

data class HomeUiState(
    val dateText: String,
    val greeting: String,
    val streakCount: Int,
    val mission: DailyMission,
    val recentJournals: List<JournalEntry>,
    val coachUiState: CoachUiState,
    val dreamCalendarUiState: DreamCalendarUiState,
    val progressUiState: ProgressUiState,
    val quickWinUiState: QuickWinSheetUiState
)

data class WriteUiState(
    val selectedYear: Int,
    val selectedDomain: String,
    val content: String,
    val isSaveEnabled: Boolean,
    val isCoachingRewardActive: Boolean
)

data class TimelineUiState(
    val journals: List<JournalEntry>
)

data class SettingsUiState(
    val selectedLanguageTag: String,
    val selectedLanguageName: String,
    val availableLanguages: List<SupportedLanguage>,
    val isLanguageDialogVisible: Boolean,
    val rewardUiState: CoachingRewardUiState,
    val canManagePrivacyChoices: Boolean,
    val supportEmail: String,
    val isResetConfirmVisible: Boolean,
    val legalDialog: LegalDialogState?
)

data class CoachingRewardUiState(
    val isActive: Boolean,
    val expiresAtText: String?
)

data class CoachUiState(
    val isRewardActive: Boolean,
    val title: String,
    val nextStep: String,
    val reminderMessage: String,
    val wins: List<QuickWinUiItem>,
    val rewardGuide: String
)

data class DreamCalendarUiState(
    val monthLabel: String,
    val days: List<CalendarDayUiState>
)

data class CalendarDayUiState(
    val dayOfMonth: Int,
    val isToday: Boolean,
    val isCompleted: Boolean
)

data class ProgressUiState(
    val completedActions: Int,
    val weeklyActionCount: Int,
    val growthScore: Int,
    val motivationMessage: String
)

data class QuickWinSheetUiState(
    val isVisible: Boolean,
    val draft: String,
    val canSave: Boolean
)

data class QuickWinEntry(
    val id: String,
    val text: String,
    val createdAt: Long
)

data class QuickWinUiItem(
    val id: String,
    val text: String,
    val dateText: String
)

data class LegalDialogState(
    val title: String,
    val body: String
)

private fun buildCalendarDays(
    completedDates: Set<String>,
    todayDateKey: String
): List<CalendarDayUiState> {
    val now = LocalDate.now()
    return (1..now.lengthOfMonth()).map { day ->
        val date = now.withDayOfMonth(day).toString()
        CalendarDayUiState(
            dayOfMonth = day,
            isToday = date == todayDateKey,
            isCompleted = completedDates.contains(date)
        )
    }
}

private fun buildNextStep(
    mission: DailyMission,
    latestJournal: JournalEntry?
): String {
    val anchor = latestJournal?.content?.take(34)?.trim()
    return if (anchor.isNullOrBlank()) {
        FutureSelfStrings.string(R.string.future_self_next_step_no_journal, mission.mission)
    } else {
        FutureSelfStrings.string(R.string.future_self_next_step_with_journal, anchor, mission.mission)
    }
}

private fun buildReminderMessage(latestWin: QuickWinEntry?): String {
    return if (latestWin == null) {
        FutureSelfStrings.string(R.string.future_self_reminder_no_win)
    } else {
        FutureSelfStrings.string(R.string.future_self_reminder_with_win, latestWin.text)
    }
}