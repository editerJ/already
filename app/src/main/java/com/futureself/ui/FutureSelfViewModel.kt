package com.futureself.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.futureself.core.i18n.FutureSelfLanguages
import com.futureself.core.notifications.FutureSelfReminderScheduler
import com.futureself.data.local.entity.JournalEntry
import com.futureself.data.preferences.FutureSelfRewardStore
import com.futureself.data.preferences.PersistedQuickWin
import com.futureself.data.preferences.PersistedRewardState
import com.already.app.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

class FutureSelfViewModel(
    private val rewardStore: FutureSelfRewardStore,
    private val reminderScheduler: FutureSelfReminderScheduler
) : ViewModel() {
    private val _uiState = MutableStateFlow(seedState())
    val uiState: StateFlow<FutureSelfUiState> = _uiState.asStateFlow()

    fun onTabSelected(tab: FutureSelfTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onYearSelected(year: Int) {
        _uiState.update { it.copy(selectedYear = year) }
    }

    fun onDomainSelected(domain: String) {
        _uiState.update { it.copy(selectedDomain = domain) }
    }

    fun onDraftChanged(content: String) {
        _uiState.update { it.copy(draftContent = content) }
    }

    fun showAiGuide() {
        _uiState.update {
            if (!it.isCoachingRewardActive) it else it.copy(isAiGuideVisible = true)
        }
    }

    fun dismissAiGuide() {
        _uiState.update { it.copy(isAiGuideVisible = false) }
    }

    fun toggleMissionCompleted() {
        mutatePersistedState { state ->
            val today = LocalDate.now().toString()
            val completed = !state.missionCompleted
            val nextDates = if (completed) {
                state.completedActionDates + today
            } else {
                state.completedActionDates - today
            }
            state.copy(
                missionCompleted = completed,
                completedActionDates = nextDates,
                streakCount = calculateStreak(nextDates)
            )
        }
    }

    fun activateCoachingReward() {
        mutatePersistedState { state ->
            val rewardEndsAt = System.currentTimeMillis() + ChronoUnit.DAYS.duration.multipliedBy(5).toMillis()
            reminderScheduler.scheduleDailyDreamReminder()
            state.copy(coachingRewardEndsAt = rewardEndsAt)
        }
    }

    fun updatePrivacyOptionsAvailable(isAvailable: Boolean) {
        _uiState.update { it.copy(canManagePrivacyChoices = isAvailable) }
    }

    fun startWritingForYear(year: Int) {
        _uiState.update {
            it.copy(
                selectedYear = year,
                selectedTab = FutureSelfTab.WRITE
            )
        }
    }

    fun saveDraft() {
        val current = _uiState.value
        if (current.draftContent.isBlank()) return

        val yearLabel = (LocalDate.now().year + current.selectedYear).toString()
        val newJournal = JournalEntry(
            id = UUID.randomUUID().toString(),
            content = current.draftContent.trim(),
            domain = current.selectedDomain,
            targetYear = current.selectedYear,
            targetYearLabel = yearLabel,
            languageTag = current.preferredLanguageTag
        )

        _uiState.update {
            it.copy(
                journals = listOf(newJournal) + it.journals,
                draftContent = "",
                selectedTab = FutureSelfTab.TIMELINE
            )
        }
    }

    fun showQuickWinSheet() {
        _uiState.update { it.copy(isQuickWinSheetVisible = true) }
    }

    fun dismissQuickWinSheet() {
        _uiState.update { it.copy(isQuickWinSheetVisible = false, quickWinDraft = "") }
    }

    fun onQuickWinDraftChanged(content: String) {
        _uiState.update { it.copy(quickWinDraft = content) }
    }

    fun saveQuickWin() {
        val current = _uiState.value
        if (current.quickWinDraft.isBlank()) return

        val now = System.currentTimeMillis()
        val today = LocalDate.now().toString()
        val newWin = QuickWinEntry(
            id = UUID.randomUUID().toString(),
            text = current.quickWinDraft.trim(),
            createdAt = now
        )

        mutatePersistedState { state ->
            val nextDates = state.completedActionDates + today
            state.copy(
                quickWins = listOf(newWin) + state.quickWins,
                completedActionDates = nextDates,
                missionCompleted = true,
                streakCount = calculateStreak(nextDates),
                isQuickWinSheetVisible = false,
                quickWinDraft = ""
            )
        }
    }

    fun showLanguageDialog() {
        _uiState.update { it.copy(isLanguageDialogVisible = true) }
    }

    fun dismissLanguageDialog() {
        _uiState.update { it.copy(isLanguageDialogVisible = false) }
    }

    fun selectLanguage(languageTag: String) {
        val normalized = if (languageTag == FutureSelfLanguages.SYSTEM) {
            FutureSelfLanguages.SYSTEM
        } else {
            FutureSelfLanguages.normalize(languageTag)
        }
        mutatePersistedState {
            it.copy(
                preferredLanguageTag = normalized,
                isLanguageDialogVisible = false
            )
        }
    }

    fun showResetConfirmation() {
        _uiState.update { it.copy(isResetConfirmVisible = true) }
    }

    fun dismissResetConfirmation() {
        _uiState.update { it.copy(isResetConfirmVisible = false) }
    }

    fun confirmResetAllData() {
        reminderScheduler.cancelDailyDreamReminder()
        mutatePersistedState {
            rewardStore.resetCoachingData(it.preferredLanguageTag)
            FutureSelfUiState(preferredLanguageTag = it.preferredLanguageTag)
        }
    }

    fun showPrivacyPolicy() {
        _uiState.update {
            it.copy(
                legalDialog = LegalDialogState(
                    title = FutureSelfStrings.string(R.string.future_self_legal_privacy_title),
                    body = FutureSelfStrings.string(R.string.future_self_legal_privacy_body)
                )
            )
        }
    }

    fun showTerms() {
        _uiState.update {
            it.copy(
                legalDialog = LegalDialogState(
                    title = FutureSelfStrings.string(R.string.future_self_legal_terms_title),
                    body = FutureSelfStrings.string(R.string.future_self_legal_terms_body)
                )
            )
        }
    }

    fun dismissLegalDialog() {
        _uiState.update { it.copy(legalDialog = null) }
    }

    private fun mutatePersistedState(transform: (FutureSelfUiState) -> FutureSelfUiState) {
        _uiState.update { current ->
            val updated = transform(current)
            rewardStore.save(updated.toPersistedState())
            updated
        }
    }

    private fun seedState(): FutureSelfUiState {
        val persisted = rewardStore.load()
        val completedDates = persisted.completedActionDates
        val today = LocalDate.now().toString()

        return FutureSelfUiState(
            preferredLanguageTag = persisted.preferredLanguageTag,
            coachingRewardEndsAt = persisted.coachingRewardEndsAt,
            completedActionDates = completedDates,
            quickWins = persisted.quickWins.map { it.toUiEntry() },
            missionCompleted = completedDates.contains(today),
            streakCount = calculateStreak(completedDates),
            journals = emptyList()
        )
    }

    private fun calculateStreak(dates: Set<String>): Int {
        var streak = 0
        var cursor = LocalDate.now()
        while (dates.contains(cursor.toString())) {
            streak += 1
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    private fun QuickWinEntry.toPersisted(): PersistedQuickWin {
        return PersistedQuickWin(
            id = id,
            text = text,
            createdAt = createdAt
        )
    }

    private fun PersistedQuickWin.toUiEntry(): QuickWinEntry {
        return QuickWinEntry(
            id = id,
            text = text,
            createdAt = createdAt
        )
    }

    private fun FutureSelfUiState.toPersistedState(): PersistedRewardState {
        return PersistedRewardState(
            preferredLanguageTag = preferredLanguageTag,
            coachingRewardEndsAt = coachingRewardEndsAt,
            completedActionDates = completedActionDates,
            quickWins = quickWins.map { it.toPersisted() }
        )
    }

    companion object {
        fun factory(
            rewardStore: FutureSelfRewardStore,
            reminderScheduler: FutureSelfReminderScheduler
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FutureSelfViewModel(
                        rewardStore = rewardStore,
                        reminderScheduler = reminderScheduler
                    ) as T
                }
            }
        }
    }
}