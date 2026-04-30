package com.regretdiary.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

val Background = Color(0xFFF5F2EC)
val BackgroundTint = Color(0xFFE7EFEA)
val SurfaceCard = Color(0xFFFFFCF8)
val SurfaceMuted = Color(0xFFF0E8DE)
val Accent = Color(0xFF365E57)
val AccentSoft = Color(0xFFDDE9E4)
val BodyText = Color(0xFF26302D)
val SubText = Color(0xFF6D7972)
val Border = Color(0xFFE4DBCF)
val Quote = Color(0xFFECE1D5)
val Premium = Color(0xFF8A5B42)
val Danger = Color(0xFF9A5A58)

val LocalMindJournalFont = staticCompositionLocalOf<FontFamily> { FontFamily.SansSerif }

@Composable
fun MindJournalTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        background = Background,
        surface = SurfaceCard,
        primary = Accent,
        secondary = Premium,
        onPrimary = Color.White,
        onBackground = BodyText,
        onSurface = BodyText,
        outline = Border
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

@Composable
fun MindJournalFontProvider(fontStyle: AppFontStyle, content: @Composable () -> Unit) {
    val family = fontFamilyFor(fontStyle)
    CompositionLocalProvider(LocalMindJournalFont provides family) {
        ProvideTextStyle(MaterialTheme.typography.bodyMedium.copy(fontFamily = family, color = BodyText)) {
            content()
        }
    }
}

fun fontFamilyFor(fontStyle: AppFontStyle): FontFamily {
    return when (fontStyle) {
        AppFontStyle.SANS -> FontFamily.SansSerif
        AppFontStyle.SERIF -> FontFamily.Serif
        AppFontStyle.MONO -> FontFamily.Monospace
        AppFontStyle.ROUNDED -> FontFamily.Cursive
    }
}
