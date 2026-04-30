package com.futureself.core.i18n

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

data class SupportedLanguage(
    val tag: String,
    val nativeName: String,
    val englishName: String
)

object FutureSelfLanguages {
    const val SYSTEM = "system"

    val supported: List<SupportedLanguage> = listOf(
        SupportedLanguage(SYSTEM, "System default", "System default"),
        SupportedLanguage("en", "English", "English"),
        SupportedLanguage("ko", "한국어", "Korean"),
        SupportedLanguage("ja", "日本語", "Japanese"),
        SupportedLanguage("es", "Español", "Spanish"),
        SupportedLanguage("fr", "Français", "French"),
        SupportedLanguage("de", "Deutsch", "German"),
        SupportedLanguage("pt-BR", "Português (Brasil)", "Portuguese (Brazil)"),
        SupportedLanguage("zh-CN", "简体中文", "Chinese (Simplified)")
    )

    fun apply(languageTag: String?) {
        val locales = if (languageTag.isNullOrBlank() || languageTag == SYSTEM) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(normalize(languageTag))
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }

    fun normalize(languageTag: String?): String {
        if (languageTag.isNullOrBlank()) return Locale.getDefault().toLanguageTag()
        return supported.firstOrNull { it.tag.equals(languageTag, ignoreCase = true) }?.tag
            ?: Locale.forLanguageTag(languageTag).toLanguageTag()
    }

    fun isSupported(languageTag: String?): Boolean {
        return supported.any { it.tag.equals(languageTag, ignoreCase = true) }
    }

    fun displayName(languageTag: String?): String {
        val normalized = normalize(languageTag)
        return supported.firstOrNull { it.tag == normalized }?.nativeName ?: normalized
    }
}
