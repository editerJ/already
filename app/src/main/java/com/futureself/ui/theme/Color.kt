package com.futureself.ui.theme

import androidx.compose.ui.graphics.Color

val Ink050 = Color(0xFFF7F4EF)
val Ink100 = Color(0xFFEDEAE4)
val Ink200 = Color(0xFFC4BDB6)
val Ink400 = Color(0xFF8C8478)
val Ink700 = Color(0xFF3D3830)
val Ink900 = Color(0xFF1A1714)
val PaperWarm = Color(0xFFFFF9F2)

val Terracotta = Color(0xFFB85C3A)
val TerracottaSoft = Color(0xFFF5E6DF)

val TimelineBlue = Color(0xFF2D4A6B)
val TimelineGreen = Color(0xFF3A5C42)
val TimelineAmber = Color(0xFF8B6914)
val Timeline10Y = Ink900

val DomainWork = Color(0xFF2D4A6B)
val DomainHealth = Color(0xFF3A5C42)
val DomainFamily = Color(0xFF8B6914)
val DomainFinance = Color(0xFF712B13)
val DomainSelf = Color(0xFF534AB7)

val Paper = Ink050
val Accent = Terracotta
val AccentSoft = TerracottaSoft

fun domainColor(domain: String): Color = when (domain) {
    "work" -> DomainWork
    "health" -> DomainHealth
    "family" -> DomainFamily
    "finance" -> DomainFinance
    "self" -> DomainSelf
    else -> Ink400
}

fun timelineColor(year: Int): Color = when (year) {
    1 -> TimelineAmber
    3 -> TimelineGreen
    5 -> TimelineBlue
    10 -> Timeline10Y
    else -> Ink400
}
