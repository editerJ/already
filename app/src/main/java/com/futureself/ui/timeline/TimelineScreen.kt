package com.futureself.ui.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import com.futureself.data.local.entity.JournalEntry
import com.futureself.ui.TimelineUiState
import com.futureself.ui.component.AccentCard
import com.futureself.ui.component.domainLabel
import com.futureself.ui.theme.DmMono
import com.futureself.ui.theme.FutureSelfShape
import com.futureself.ui.theme.Ink050
import com.futureself.ui.theme.Ink100
import com.futureself.ui.theme.Ink400
import com.futureself.ui.theme.Ink700
import com.futureself.ui.theme.Ink900
import com.futureself.ui.theme.PaperWarm
import com.futureself.ui.theme.Sp
import com.futureself.ui.theme.timelineColor
import com.already.app.R

@Composable
fun TimelineScreen(
    uiState: TimelineUiState,
    onWriteClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val filledYears = remember(uiState.journals) { uiState.journals.map { it.targetYear }.toSet() }

    LaunchedEffect(uiState.journals.size) {
        if (uiState.journals.isNotEmpty()) {
            listState.scrollToItem(0)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(Ink050),
        contentPadding = PaddingValues(horizontal = Sp.lg, vertical = Sp.xl),
        verticalArrangement = Arrangement.spacedBy(Sp.lg)
    ) {
        item {
            Text(
                text = stringResource(R.string.future_self_timeline_title),
                style = MaterialTheme.typography.headlineLarge,
                color = Ink900
            )
            Spacer(Modifier.height(Sp.xs))
            Text(
                text = stringResource(R.string.future_self_timeline_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = Ink400
            )
        }

        listOf(1, 3, 5, 10).forEach { year ->
            val yearEntries = uiState.journals.filter { it.targetYear == year }
            if (filledYears.contains(year)) {
                items(yearEntries, key = { it.id }) { journal ->
                    TimelineRow(journal = journal)
                }
            } else {
                item(key = "empty_$year") {
                    EmptyTimelineRow(
                        year = year,
                        onClick = { onWriteClick(year) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineRow(journal: JournalEntry) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(42.dp)
        ) {
            Text(
                text = journal.targetYear.toString(),
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = DmMono),
                color = Ink400
            )
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(timelineColor(journal.targetYear))
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(72.dp)
                    .background(Ink100)
            )
        }
        Spacer(Modifier.width(Sp.md))
        AccentCard(
            accentColor = timelineColor(journal.targetYear),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = domainLabel(journal.domain),
                style = MaterialTheme.typography.labelSmall,
                color = Ink400
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = journal.content,
                style = MaterialTheme.typography.bodySmall,
                color = Ink700,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EmptyTimelineRow(
    year: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(42.dp)
        ) {
            Text(
                text = year.toString(),
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = DmMono),
                color = Ink400
            )
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Ink100)
            )
        }
        Spacer(Modifier.width(Sp.md))
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(FutureSelfShape.card)
                .background(PaperWarm)
                .padding(horizontal = Sp.md, vertical = Sp.md)
        ) {
            Text(
                text = stringResource(R.string.future_self_timeline_empty, year),
                style = MaterialTheme.typography.bodySmall,
                color = Ink400
            )
        }
    }
}
