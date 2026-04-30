package com.futureself.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.futureself.ui.theme.FutureSelfShape
import com.futureself.ui.theme.Ink050
import com.futureself.ui.theme.Ink100
import com.futureself.ui.theme.Ink400
import com.futureself.ui.theme.Ink900
import com.futureself.ui.theme.PaperWarm
import com.futureself.ui.theme.Sp
import com.futureself.ui.theme.Stroke
import com.futureself.ui.theme.Terracotta
import com.futureself.ui.theme.TerracottaSoft
import com.futureself.ui.theme.domainColor
import com.already.app.R
import com.futureself.ui.theme.ex

@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.ex.ink400,
        modifier = modifier
    )
}

@Composable
fun ThinDivider(
    modifier: Modifier = Modifier,
    color: Color = Ink100
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Stroke.hairline)
            .background(color)
    )
}

@Composable
fun DomainPill(
    domain: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = domainColor(domain)
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) TerracottaSoft else Ink050,
        animationSpec = tween(180),
        label = "domain_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Terracotta else Ink400,
        animationSpec = tween(180),
        label = "domain_text"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(FutureSelfShape.tag)
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = Sp.sm, vertical = Sp.xxs + 2.dp)
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(Stroke.accent)
                    .height(14.dp)
                    .clip(FutureSelfShape.tag)
                    .background(color)
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = domainLabel(domain),
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}

@Composable
fun YearPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Terracotta else Ink100,
        animationSpec = tween(180),
        label = "year_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) PaperWarm else Ink400,
        animationSpec = tween(180),
        label = "year_text"
    )

    Box(
        modifier = modifier
            .clip(FutureSelfShape.yearPill)
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = Sp.md, vertical = Sp.xxs + 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = textColor
        )
    }
}

@Composable
fun CircleCheckButton(
    isChecked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fillColor by animateColorAsState(
        targetValue = if (isChecked) Terracotta else Color.Transparent,
        animationSpec = tween(220),
        label = "check_fill"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .border(Stroke.medium, Terracotta, CircleShape)
            .background(fillColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        if (isChecked) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}

@Composable
fun TextLinkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Terracotta
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = color,
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    )
}

@Composable
fun NavItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = Sp.sm, vertical = Sp.xxs)
    ) {
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(if (isSelected) Terracotta else Ink100)
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Ink900 else Ink400
        )
    }
}

@Composable
fun AccentCard(
    accentColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Row(
        modifier = modifier
            .clip(FutureSelfShape.card)
            .background(PaperWarm)
    ) {
        Box(
            modifier = Modifier
                .width(Stroke.accent)
                .fillMaxHeight()
                .background(accentColor)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Sp.md, vertical = Sp.md),
            content = content
        )
    }
}

@Composable
fun domainLabel(domain: String): String = when (domain) {
    "work" -> stringResource(R.string.future_self_domain_work)
    "health" -> stringResource(R.string.future_self_domain_health)
    "family" -> stringResource(R.string.future_self_domain_family)
    "finance" -> stringResource(R.string.future_self_domain_finance)
    "self" -> stringResource(R.string.future_self_domain_self)
    else -> domain
}
