package com.futureself.ui.write

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.futureself.ui.component.ThinDivider
import com.futureself.ui.theme.FutureSelfShape
import com.futureself.ui.theme.Ink050
import com.futureself.ui.theme.Ink400
import com.futureself.ui.theme.Ink700
import com.futureself.ui.theme.Ink900
import com.futureself.ui.theme.PaperWarm
import com.futureself.ui.theme.Sp
import com.already.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiGuideSheet(
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PaperWarm
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Sp.lg, vertical = Sp.md)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(Sp.sm)
        ) {
            Text(
                text = stringResource(R.string.future_self_ai_sheet_title),
                style = MaterialTheme.typography.headlineSmall,
                color = Ink900
            )
            Text(
                text = stringResource(R.string.future_self_ai_sheet_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = Ink400
            )
            ThinDivider()
            listOf(
                stringResource(R.string.future_self_ai_prompt_1),
                stringResource(R.string.future_self_ai_prompt_2),
                stringResource(R.string.future_self_ai_prompt_3),
                stringResource(R.string.future_self_ai_prompt_4)
            ).forEach { prompt ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(FutureSelfShape.card)
                        .background(Ink050)
                        .padding(horizontal = Sp.md, vertical = Sp.md)
                ) {
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.bodySmall,
                        color = Ink700
                    )
                }
            }
        }
    }
}
