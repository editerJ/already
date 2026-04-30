package com.futureself.ui.write

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import com.futureself.ui.WriteUiState
import com.futureself.ui.component.DomainPill
import com.futureself.ui.component.TextLinkButton
import com.futureself.ui.component.ThinDivider
import com.futureself.ui.component.YearPill
import com.futureself.ui.theme.Ink100
import com.futureself.ui.theme.Ink200
import com.futureself.ui.theme.Ink400
import com.futureself.ui.theme.Ink900
import com.futureself.ui.theme.NotoSerifKR
import com.futureself.ui.theme.PaperWarm
import com.futureself.ui.theme.Sp
import com.futureself.ui.theme.Terracotta
import com.already.app.R
import java.time.LocalDate

@Composable
fun WriteScreen(
    uiState: WriteUiState,
    onYearChange: (Int) -> Unit,
    onDomainChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onAiGuideClick: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasCoachingReward = uiState.isCoachingRewardActive

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PaperWarm)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = Sp.lg, vertical = Sp.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.future_self_write_header),
                style = MaterialTheme.typography.labelMedium,
                color = Ink400
            )
            Text(
                text = stringResource(R.string.future_self_save),
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 15.sp),
                color = if (uiState.isSaveEnabled) Terracotta else Ink200,
                modifier = Modifier.clickableText(onClick = onSave)
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = Sp.lg),
            horizontalArrangement = Arrangement.spacedBy(Sp.xs)
        ) {
            items(listOf(1, 3, 5, 10)) { year ->
                YearPill(
                    label = if (uiState.selectedYear == year) {
                        stringResource(R.string.future_self_year_pill_selected, year, LocalDate.now().year + year)
                    } else {
                        stringResource(R.string.future_self_year_pill_default, year)
                    },
                    isSelected = year == uiState.selectedYear,
                    onClick = { onYearChange(year) }
                )
            }
        }

        Spacer(Modifier.padding(top = Sp.sm))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = Sp.lg),
            horizontalArrangement = Arrangement.spacedBy(Sp.xs)
        ) {
            listOf("work", "health", "family", "finance", "self").forEach { domain ->
                DomainPill(
                    domain = domain,
                    isSelected = domain == uiState.selectedDomain,
                    onClick = { onDomainChange(domain) }
                )
            }
        }

        ThinDivider(
            modifier = Modifier.padding(vertical = Sp.md),
            color = Ink100
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Sp.lg)
        ) {
            BasicTextField(
                value = uiState.content,
                onValueChange = onContentChange,
                modifier = Modifier.fillMaxSize(),
                textStyle = TextStyle(
                    fontFamily = NotoSerifKR,
                    fontSize = 18.sp,
                    lineHeight = 34.sp,
                    color = Ink900
                ),
                cursorBrush = SolidColor(Terracotta),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (uiState.content.isBlank()) {
                            Text(
                                text = stringResource(R.string.future_self_write_placeholder),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Ink200,
                                fontStyle = FontStyle.Italic
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = Sp.lg, vertical = Sp.md)
        ) {
            TextLinkButton(
                text = stringResource(R.string.future_self_ai_guide_link),
                onClick = if (hasCoachingReward) onAiGuideClick else ({}),
                color = if (hasCoachingReward) Terracotta else Ink400
            )
            Spacer(Modifier.padding(top = Sp.xs))
            Text(
                text = if (hasCoachingReward) {
                    stringResource(R.string.future_self_ai_reward_active_status)
                } else {
                    stringResource(R.string.future_self_ai_reward_inactive_status)
                },
                style = MaterialTheme.typography.labelSmall,
                color = if (hasCoachingReward) Terracotta else Ink400
            )
            if (!hasCoachingReward) {
                Spacer(Modifier.padding(top = Sp.xs))
                Text(
                    text = stringResource(R.string.future_self_ai_reward_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink400
                )
            }
            if (hasCoachingReward) {
                Spacer(Modifier.padding(top = Sp.xs))
                Text(
                    text = stringResource(R.string.future_self_write_coach_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = Terracotta
                )
            }
            Spacer(Modifier.padding(top = Sp.xs))
            Text(
                text = stringResource(R.string.future_self_present_tense_hint),
                style = MaterialTheme.typography.labelSmall,
                color = Ink400
            )
        }
    }
}

@Composable
private fun Modifier.clickableText(onClick: () -> Unit): Modifier {
    return this.then(
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    )
}
