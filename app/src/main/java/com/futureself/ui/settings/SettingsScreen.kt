package com.futureself.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.futureself.core.i18n.SupportedLanguage
import com.futureself.ui.LegalDialogState
import com.futureself.ui.SettingsUiState
import com.futureself.ui.component.AccentCard
import com.futureself.ui.component.SectionLabel
import com.futureself.ui.component.TextLinkButton
import com.futureself.ui.theme.Ink050
import com.futureself.ui.theme.Ink400
import com.futureself.ui.theme.Ink700
import com.futureself.ui.theme.Ink900
import com.futureself.ui.theme.PaperWarm
import com.futureself.ui.theme.Sp
import com.futureself.ui.theme.Terracotta
import com.already.app.R

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onLanguageClick: () -> Unit,
    onLanguageDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    onWatchRewardAd: () -> Unit,
    onShowPrivacyChoices: () -> Unit,
    onShowPrivacyPolicy: () -> Unit,
    onShowTerms: () -> Unit,
    onDismissLegalDialog: () -> Unit,
    onShowResetConfirmation: () -> Unit,
    onDismissResetConfirmation: () -> Unit,
    onConfirmResetAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Ink050),
        contentPadding = PaddingValues(bottom = Sp.xl)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = Sp.lg, vertical = Sp.xl),
                verticalArrangement = Arrangement.spacedBy(Sp.xs)
            ) {
                Text(
                    text = stringResource(R.string.future_self_settings_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Ink900
                )
                Text(
                    text = stringResource(R.string.future_self_settings_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink400
                )
            }
        }

        item {
            SectionLabel(
                text = stringResource(R.string.future_self_settings_language_section),
                modifier = Modifier.padding(horizontal = Sp.lg)
            )
            Spacer(Modifier.padding(top = Sp.sm))
            AccentCard(
                accentColor = Terracotta,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Sp.lg)
            ) {
                Text(
                    text = stringResource(R.string.future_self_settings_language_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink900
                )
                Spacer(Modifier.padding(top = Sp.xs))
                Text(
                    text = stringResource(
                        R.string.future_self_settings_language_summary,
                        uiState.selectedLanguageName
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink700
                )
                Spacer(Modifier.padding(top = Sp.sm))
                TextLinkButton(
                    text = stringResource(R.string.future_self_settings_language_action),
                    onClick = onLanguageClick
                )
            }
            Spacer(Modifier.padding(top = Sp.xxl))
        }

        item {
            SectionLabel(
                text = stringResource(R.string.future_self_settings_reward_section),
                modifier = Modifier.padding(horizontal = Sp.lg)
            )
            Spacer(Modifier.padding(top = Sp.sm))
            AccentCard(
                accentColor = Terracotta,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Sp.lg)
            ) {
                Text(
                    text = stringResource(R.string.future_self_settings_reward_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink900
                )
                Spacer(Modifier.padding(top = Sp.xs))
                Text(
                    text = if (uiState.rewardUiState.isActive) {
                        stringResource(
                            R.string.future_self_settings_reward_active,
                            uiState.rewardUiState.expiresAtText.orEmpty()
                        )
                    } else {
                        stringResource(R.string.future_self_settings_reward_inactive)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink700
                )
                Spacer(Modifier.padding(top = Sp.xs))
                Text(
                    text = stringResource(R.string.future_self_settings_reward_benefits),
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink400
                )
                Spacer(Modifier.padding(top = Sp.sm))
                TextLinkButton(
                    text = if (uiState.rewardUiState.isActive) {
                        stringResource(R.string.future_self_settings_reward_refresh)
                    } else {
                        stringResource(R.string.future_self_settings_reward_action)
                    },
                    onClick = onWatchRewardAd
                )
            }
            Spacer(Modifier.padding(top = Sp.xxl))
        }

        item {
            SectionLabel(
                text = stringResource(R.string.future_self_settings_privacy_section),
                modifier = Modifier.padding(horizontal = Sp.lg)
            )
            Spacer(Modifier.padding(top = Sp.sm))
            AccentCard(
                accentColor = Terracotta,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Sp.lg)
            ) {
                Text(
                    text = stringResource(R.string.future_self_settings_privacy_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink900
                )
                Spacer(Modifier.padding(top = Sp.xs))
                Text(
                    text = stringResource(R.string.future_self_settings_privacy_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink700
                )
                Spacer(Modifier.padding(top = Sp.sm))
                if (uiState.canManagePrivacyChoices) {
                    TextLinkButton(
                        text = stringResource(R.string.future_self_settings_privacy_action),
                        onClick = onShowPrivacyChoices
                    )
                    Spacer(Modifier.padding(top = Sp.sm))
                }
                TextLinkButton(
                    text = stringResource(R.string.future_self_settings_privacy_policy_action),
                    onClick = onShowPrivacyPolicy
                )
                Spacer(Modifier.padding(top = Sp.sm))
                TextLinkButton(
                    text = stringResource(R.string.future_self_settings_terms_action),
                    onClick = onShowTerms
                )
            }
            Spacer(Modifier.padding(top = Sp.xxl))
        }

        item {
            SectionLabel(
                text = stringResource(R.string.future_self_settings_data_section),
                modifier = Modifier.padding(horizontal = Sp.lg)
            )
            Spacer(Modifier.padding(top = Sp.sm))
            AccentCard(
                accentColor = Terracotta,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Sp.lg)
            ) {
                Text(
                    text = stringResource(R.string.future_self_settings_data_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink900
                )
                Spacer(Modifier.padding(top = Sp.xs))
                Text(
                    text = stringResource(R.string.future_self_settings_data_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink700
                )
                Spacer(Modifier.padding(top = Sp.sm))
                TextLinkButton(
                    text = stringResource(R.string.future_self_settings_data_action),
                    onClick = onShowResetConfirmation
                )
            }
        }

        item {
            Spacer(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(top = Sp.xl)
            )
        }
    }

    LanguageDialog(
        isVisible = uiState.isLanguageDialogVisible,
        languages = uiState.availableLanguages,
        selectedLanguageTag = uiState.selectedLanguageTag,
        onDismiss = onLanguageDismiss,
        onLanguageSelected = onLanguageSelected
    )

    LegalDialog(
        state = uiState.legalDialog,
        onDismiss = onDismissLegalDialog
    )

    ResetConfirmationDialog(
        isVisible = uiState.isResetConfirmVisible,
        onDismiss = onDismissResetConfirmation,
        onConfirm = onConfirmResetAllData
    )
}

@Composable
private fun LanguageDialog(
    isVisible: Boolean,
    languages: List<SupportedLanguage>,
    selectedLanguageTag: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    if (!isVisible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.future_self_language_dialog_title),
                style = MaterialTheme.typography.headlineSmall,
                color = Ink900
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                languages.forEach { language ->
                    LanguageRow(
                        language = language,
                        isSelected = language.tag == selectedLanguageTag,
                        onClick = { onLanguageSelected(language.tag) }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextLinkButton(
                text = stringResource(R.string.future_self_quick_win_cancel),
                onClick = onDismiss,
                color = Ink400
            )
        },
        containerColor = PaperWarm
    )
}

@Composable
private fun LegalDialog(
    state: LegalDialogState?,
    onDismiss: () -> Unit
) {
    if (state == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = state.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Ink900
            )
        },
        text = {
            Text(
                text = state.body,
                style = MaterialTheme.typography.bodySmall,
                color = Ink700
            )
        },
        confirmButton = {
            TextLinkButton(
                text = stringResource(R.string.future_self_quick_win_save),
                onClick = onDismiss
            )
        },
        dismissButton = {},
        containerColor = PaperWarm
    )
}

@Composable
private fun ResetConfirmationDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!isVisible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.future_self_settings_data_confirm_title),
                style = MaterialTheme.typography.headlineSmall,
                color = Ink900
            )
        },
        text = {
            Text(
                text = stringResource(R.string.future_self_settings_data_confirm_body),
                style = MaterialTheme.typography.bodySmall,
                color = Ink700
            )
        },
        confirmButton = {
            TextLinkButton(
                text = stringResource(R.string.future_self_settings_data_confirm_action),
                onClick = onConfirm
            )
        },
        dismissButton = {
            TextLinkButton(
                text = stringResource(R.string.future_self_quick_win_cancel),
                onClick = onDismiss,
                color = Ink400
            )
        },
        containerColor = PaperWarm
    )
}

@Composable
private fun LanguageRow(
    language: SupportedLanguage,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = language.nativeName,
                style = MaterialTheme.typography.bodyMedium,
                color = Ink900
            )
            if (language.englishName != language.nativeName) {
                Text(
                    text = language.englishName,
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink400
                )
            }
        }
        RadioButton(
            selected = isSelected,
            onClick = onClick
        )
    }
}