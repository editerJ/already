package com.futureself.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.futureself.data.local.entity.DailyMission
import com.futureself.data.local.entity.JournalEntry
import com.futureself.ui.CalendarDayUiState
import com.futureself.ui.CoachUiState
import com.futureself.ui.DreamCalendarUiState
import com.futureself.ui.HomeUiState
import com.futureself.ui.ProgressUiState
import com.futureself.ui.QuickWinSheetUiState
import com.futureself.ui.QuickWinUiItem
import com.futureself.ui.component.AccentCard
import com.futureself.ui.component.CircleCheckButton
import com.futureself.ui.component.SectionLabel
import com.futureself.ui.component.TextLinkButton
import com.futureself.ui.component.ThinDivider
import com.futureself.ui.component.domainLabel
import com.futureself.ui.theme.Ink050
import com.futureself.ui.theme.Ink100
import com.futureself.ui.theme.Ink400
import com.futureself.ui.theme.Ink700
import com.futureself.ui.theme.Ink900
import com.futureself.ui.theme.PaperWarm
import com.futureself.ui.theme.Sp
import com.futureself.ui.theme.Terracotta
import com.futureself.ui.theme.TerracottaSoft
import com.futureself.ui.theme.timelineColor
import com.already.app.R

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onMissionCheck: () -> Unit,
    onWriteClick: () -> Unit,
    onShowQuickWinSheet: () -> Unit,
    onDismissQuickWinSheet: () -> Unit,
    onQuickWinDraftChanged: (String) -> Unit,
    onSaveQuickWin: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.background(Ink050),
        contentPadding = PaddingValues(bottom = Sp.xl)
    ) {
        item {
            HomeHeader(
                dateText = uiState.dateText,
                greeting = uiState.greeting,
                streakCount = uiState.streakCount
            )
            ThinDivider(modifier = Modifier.padding(horizontal = Sp.lg, vertical = Sp.lg))
        }

        item {
            SectionLabel(
                text = stringResource(R.string.future_self_today_mission),
                modifier = Modifier.padding(horizontal = Sp.lg)
            )
            Spacer(Modifier.height(Sp.sm))
            MissionCard(
                mission = uiState.mission,
                onCheck = onMissionCheck,
                modifier = Modifier.padding(horizontal = Sp.lg)
            )
            Spacer(Modifier.height(Sp.xxl))
        }

        item {
            SectionLabel(
                text = stringResource(R.string.future_self_coach_section),
                modifier = Modifier.padding(horizontal = Sp.lg)
            )
            Spacer(Modifier.height(Sp.sm))
            CoachBoardCard(
                uiState = uiState.coachUiState,
                onRecordWinClick = onShowQuickWinSheet,
                modifier = Modifier.padding(horizontal = Sp.lg)
            )
            Spacer(Modifier.height(Sp.xxl))
        }

        item {
            SectionLabel(
                text = stringResource(R.string.future_self_calendar_section),
                modifier = Modifier.padding(horizontal = Sp.lg)
            )
            Spacer(Modifier.height(Sp.sm))
            DreamCalendarCard(
                uiState = uiState.dreamCalendarUiState,
                modifier = Modifier.padding(horizontal = Sp.lg)
            )
            Spacer(Modifier.height(Sp.xxl))
        }

        item {
            SectionLabel(
                text = stringResource(R.string.future_self_progress_section),
                modifier = Modifier.padding(horizontal = Sp.lg)
            )
            Spacer(Modifier.height(Sp.sm))
            ProgressBoardCard(
                uiState = uiState.progressUiState,
                modifier = Modifier.padding(horizontal = Sp.lg)
            )
            Spacer(Modifier.height(Sp.xxl))
        }

        item {
            SectionLabel(
                text = stringResource(R.string.future_self_recent_entries),
                modifier = Modifier.padding(horizontal = Sp.lg)
            )
            Spacer(Modifier.height(Sp.sm))
        }

        items(uiState.recentJournals, key = { it.id }) { journal ->
            RecentJournalRow(
                journal = journal,
                modifier = Modifier.padding(horizontal = Sp.lg)
            )
        }

        item {
            Spacer(Modifier.height(Sp.xl))
            TextLinkButton(
                text = stringResource(R.string.future_self_write_today_link),
                onClick = onWriteClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Sp.lg)
                    .wrapContentWidth(Alignment.End)
            )
        }
    }

    QuickWinDialog(
        uiState = uiState.quickWinUiState,
        onDismiss = onDismissQuickWinSheet,
        onDraftChanged = onQuickWinDraftChanged,
        onSave = onSaveQuickWin
    )
}

@Composable
private fun HomeHeader(
    dateText: String,
    greeting: String,
    streakCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = Sp.lg, end = Sp.lg, top = Sp.xl),
        verticalArrangement = Arrangement.spacedBy(Sp.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = dateText,
                style = MaterialTheme.typography.labelSmall,
                color = Ink400
            )

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = streakCount.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = Terracotta
                )
                Text(
                    text = stringResource(R.string.future_self_streak_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink400,
                    textAlign = TextAlign.End
                )
            }
        }

        GreetingText(
            greeting = greeting,
            compact = false
        )
    }
}

@Composable
private fun GreetingText(
    greeting: String,
    compact: Boolean
) {
    Text(
        text = greeting,
        modifier = Modifier.fillMaxWidth(),
        style = if (compact) {
            MaterialTheme.typography.headlineMedium.copy(lineHeight = 42.sp)
        } else {
            MaterialTheme.typography.headlineLarge.copy(lineHeight = 48.sp)
        },
        color = Ink900,
        fontStyle = FontStyle.Italic
    )
}

@Composable
private fun MissionCard(
    mission: DailyMission,
    onCheck: () -> Unit,
    modifier: Modifier = Modifier
) {
    AccentCard(
        accentColor = Terracotta,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = domainLabel(mission.domain).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Terracotta
        )
        Spacer(Modifier.height(Sp.xxs + 2.dp))
        Text(
            text = mission.mission,
            style = MaterialTheme.typography.bodyMedium,
            color = Ink900
        )
        Spacer(Modifier.height(Sp.xxs))
        Text(
            text = mission.why,
            style = MaterialTheme.typography.labelMedium,
            color = Ink400
        )
        Spacer(Modifier.height(Sp.md))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = mission.duration,
                style = MaterialTheme.typography.titleSmall,
                color = Ink400
            )
            CircleCheckButton(
                isChecked = mission.isCompleted,
                onClick = onCheck
            )
        }
    }
}

@Composable
private fun CoachBoardCard(
    uiState: CoachUiState,
    onRecordWinClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AccentCard(
        accentColor = Terracotta,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = uiState.title,
            style = MaterialTheme.typography.bodyMedium,
            color = Ink900
        )
        Spacer(Modifier.height(Sp.xs))
        Text(
            text = uiState.rewardGuide,
            style = MaterialTheme.typography.bodySmall,
            color = Ink700
        )
        Spacer(Modifier.height(Sp.sm))
        CoachHighlightCard(
            label = stringResource(R.string.future_self_coach_next_step),
            body = if (uiState.isRewardActive) uiState.nextStep else stringResource(R.string.future_self_coach_locked_body)
        )
        Spacer(Modifier.height(Sp.xs))
        CoachHighlightCard(
            label = stringResource(R.string.future_self_coach_reminder),
            body = if (uiState.isRewardActive) uiState.reminderMessage else stringResource(R.string.future_self_coach_locked_reminder)
        )
        if (uiState.wins.isNotEmpty()) {
            Spacer(Modifier.height(Sp.sm))
            Text(
                text = stringResource(R.string.future_self_coach_recent_wins),
                style = MaterialTheme.typography.labelMedium,
                color = Ink400
            )
            Spacer(Modifier.height(Sp.xs))
            uiState.wins.forEach { win ->
                QuickWinRow(item = win)
                Spacer(Modifier.height(Sp.xs))
            }
        }
        if (uiState.isRewardActive) {
            TextLinkButton(
                text = stringResource(R.string.future_self_quick_win_record),
                onClick = onRecordWinClick
            )
        }
    }
}

@Composable
private fun CoachHighlightCard(
    label: String,
    body: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(TerracottaSoft)
            .padding(Sp.md),
        verticalArrangement = Arrangement.spacedBy(Sp.xs)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Terracotta
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodySmall,
            color = Ink900
        )
    }
}

@Composable
private fun QuickWinRow(item: QuickWinUiItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Ink050)
            .padding(horizontal = Sp.sm, vertical = Sp.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.text,
            style = MaterialTheme.typography.bodySmall,
            color = Ink900,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.width(Sp.sm))
        Text(
            text = item.dateText,
            style = MaterialTheme.typography.labelSmall,
            color = Ink400
        )
    }
}

@Composable
private fun DreamCalendarCard(
    uiState: DreamCalendarUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(PaperWarm)
            .padding(Sp.md),
        verticalArrangement = Arrangement.spacedBy(Sp.sm)
    ) {
        Text(
            text = uiState.monthLabel,
            style = MaterialTheme.typography.headlineSmall,
            color = Ink900
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            uiState.days.chunked(7).forEach { week ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    week.forEach { day ->
                        CalendarDayChip(day = day)
                    }
                    repeat(7 - week.size) {
                        Spacer(Modifier.size(38.dp))
                    }
                }
            }
        }
        Text(
            text = stringResource(R.string.future_self_calendar_hint),
            style = MaterialTheme.typography.labelSmall,
            color = Ink400
        )
    }
}

@Composable
private fun CalendarDayChip(day: CalendarDayUiState) {
    val background = when {
        day.isCompleted -> Terracotta
        day.isToday -> TerracottaSoft
        else -> Ink050
    }
    val textColor = when {
        day.isCompleted -> PaperWarm
        day.isToday -> Terracotta
        else -> Ink700
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .border(
                width = if (day.isToday) 1.dp else 0.dp,
                color = if (day.isToday) Terracotta else background,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}

@Composable
private fun ProgressBoardCard(
    uiState: ProgressUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(PaperWarm)
            .padding(Sp.md),
        verticalArrangement = Arrangement.spacedBy(Sp.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Sp.sm)
        ) {
            ProgressMetric(
                label = stringResource(R.string.future_self_progress_actions),
                value = uiState.completedActions.toString(),
                modifier = Modifier.weight(1f)
            )
            ProgressMetric(
                label = stringResource(R.string.future_self_progress_weekly),
                value = uiState.weeklyActionCount.toString(),
                modifier = Modifier.weight(1f)
            )
            ProgressMetric(
                label = stringResource(R.string.future_self_progress_growth),
                value = "${uiState.growthScore}%",
                modifier = Modifier.weight(1f)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(TerracottaSoft)
                .padding(Sp.md)
        ) {
            Text(
                text = uiState.motivationMessage,
                style = MaterialTheme.typography.bodySmall,
                color = Ink900
            )
        }
    }
}

@Composable
private fun ProgressMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Ink050)
            .padding(horizontal = Sp.sm, vertical = Sp.md),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Ink400
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = Ink900
        )
    }
}

@Composable
private fun RecentJournalRow(
    journal: JournalEntry,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Sp.md),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = journal.targetYear.toString(),
            style = MaterialTheme.typography.titleSmall,
            color = Ink400,
            modifier = Modifier.width(36.dp)
        )
        Spacer(Modifier.width(Sp.md))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(timelineColor(journal.targetYear))
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(Ink100)
            )
        }
        Spacer(Modifier.width(Sp.md))
        Column {
            Text(
                text = domainLabel(journal.domain),
                style = MaterialTheme.typography.labelSmall,
                color = Ink400
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = journal.content,
                style = MaterialTheme.typography.bodySmall,
                color = Ink700,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    ThinDivider(modifier = modifier)
}

@Composable
private fun QuickWinDialog(
    uiState: QuickWinSheetUiState,
    onDismiss: () -> Unit,
    onDraftChanged: (String) -> Unit,
    onSave: () -> Unit
) {
    if (!uiState.isVisible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.future_self_quick_win_dialog_title),
                style = MaterialTheme.typography.headlineSmall,
                color = Ink900
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
                Text(
                    text = stringResource(R.string.future_self_quick_win_dialog_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink400
                )
                OutlinedTextField(
                    value = uiState.draft,
                    onValueChange = onDraftChanged,
                    placeholder = {
                        Text(text = stringResource(R.string.future_self_quick_win_placeholder))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextLinkButton(
                text = stringResource(R.string.future_self_quick_win_save),
                onClick = if (uiState.canSave) onSave else ({}),
                color = if (uiState.canSave) Terracotta else Ink400
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