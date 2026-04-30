package com.futureself.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    background = Ink050,
    surface = PaperWarm,
    onBackground = Ink900,
    onSurface = Ink900,
    primary = Terracotta,
    onPrimary = Color.White,
    primaryContainer = TerracottaSoft,
    onPrimaryContainer = Terracotta,
    outline = Ink100,
    outlineVariant = Ink100,
    surfaceVariant = Ink050,
    onSurfaceVariant = Ink400,
    secondary = Ink700,
    onSecondary = Color.White
)

private val DarkColors = darkColorScheme(
    background = Color(0xFF14110E),
    surface = Color(0xFF1E1A17),
    onBackground = Color(0xFFF0EDE8),
    onSurface = Color(0xFFEDEAE4),
    primary = Color(0xFFD4795A),
    onPrimary = Color(0xFF2C1208),
    primaryContainer = Color(0xFF3D1E10),
    onPrimaryContainer = Color(0xFFF5C4B3),
    outline = Color(0xFF3A3530),
    outlineVariant = Color(0xFF2A2520),
    surfaceVariant = Color(0xFF1E1A17),
    onSurfaceVariant = Color(0xFF8C8478),
    secondary = Color(0xFFB4B2A9),
    onSecondary = Color(0xFF1A1714)
)

data class FutureSelfExtendedColors(
    val paperWarm: Color,
    val ink200: Color,
    val ink400: Color,
    val ink700: Color,
    val terracotta: Color,
    val terracottaSoft: Color,
    val timeline1y: Color,
    val timeline3y: Color,
    val timeline5y: Color,
    val timeline10y: Color
)

val LocalFutureSelfColors = staticCompositionLocalOf {
    FutureSelfExtendedColors(
        paperWarm = PaperWarm,
        ink200 = Ink200,
        ink400 = Ink400,
        ink700 = Ink700,
        terracotta = Terracotta,
        terracottaSoft = TerracottaSoft,
        timeline1y = TimelineAmber,
        timeline3y = TimelineGreen,
        timeline5y = TimelineBlue,
        timeline10y = Timeline10Y
    )
}

@Composable
fun FutureSelfTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val extended = if (darkTheme) {
        FutureSelfExtendedColors(
            paperWarm = Color(0xFF1E1A17),
            ink200 = Color(0xFF5F5E5A),
            ink400 = Color(0xFF8C8478),
            ink700 = Color(0xFFB4B2A9),
            terracotta = Color(0xFFD4795A),
            terracottaSoft = Color(0xFF3D1E10),
            timeline1y = Color(0xFFEF9F27),
            timeline3y = Color(0xFF5DCAA5),
            timeline5y = Color(0xFF85B7EB),
            timeline10y = Color(0xFFD3D1C7)
        )
    } else {
        LocalFutureSelfColors.current
    }

    CompositionLocalProvider(LocalFutureSelfColors provides extended) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = FutureSelfTypography,
        content = content
    )
    }
}

val MaterialTheme.ex: FutureSelfExtendedColors
    @Composable get() = LocalFutureSelfColors.current
