package com.futureself.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.futureself.ui.FutureSelfTab
import com.futureself.ui.theme.Ink050
import com.futureself.ui.theme.Sp
import com.already.app.R

@Composable
fun BottomNav(
    selectedTab: FutureSelfTab,
    onTabSelected: (FutureSelfTab) -> Unit
) {
    Column {
        ThinDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Ink050)
                .navigationBarsPadding()
                .padding(horizontal = Sp.xs),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                label = stringResource(R.string.future_self_nav_home),
                isSelected = selectedTab == FutureSelfTab.HOME,
                onClick = { onTabSelected(FutureSelfTab.HOME) }
            )
            NavItem(
                label = stringResource(R.string.future_self_nav_write),
                isSelected = selectedTab == FutureSelfTab.WRITE,
                onClick = { onTabSelected(FutureSelfTab.WRITE) }
            )
            NavItem(
                label = stringResource(R.string.future_self_nav_timeline),
                isSelected = selectedTab == FutureSelfTab.TIMELINE,
                onClick = { onTabSelected(FutureSelfTab.TIMELINE) }
            )
            NavItem(
                label = stringResource(R.string.future_self_nav_settings),
                isSelected = selectedTab == FutureSelfTab.SETTINGS,
                onClick = { onTabSelected(FutureSelfTab.SETTINGS) }
            )
        }
    }
}
