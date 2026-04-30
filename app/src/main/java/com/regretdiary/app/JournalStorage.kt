package com.regretdiary.app

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import org.json.JSONArray
import org.json.JSONObject

private const val SECURE_PREFS_NAME = "mind_journal_secure_prefs"
private const val LEGACY_PREFS_NAME = "regret_diary_prefs"
private const val PREFS_ENTRIES_KEY = "entries_json"
private const val PREFS_PREMIUM_KEY = "premium_enabled"
private const val PREFS_LOCK_ENABLED_KEY = "lock_enabled"
private const val PREFS_SORT_DESC_KEY = "sort_desc"
private const val PREFS_ONBOARDING_DONE_KEY = "onboarding_done"
private const val PREFS_LANGUAGE_KEY = "app_language"
private const val PREFS_FONT_STYLE_KEY = "font_style"
private const val PREFS_AI_COACH_UNLOCK_KEY = "ai_coach_unlocked_until"
private const val PREFS_TREND_UNLOCK_KEY = "trend_unlocked_until"
private const val PREFS_PDF_EXPORT_CREDITS_KEY = "pdf_export_credits"

fun loadEntries(context: Context): List<Entry> {
    val prefs = appPreferences(context)
    val raw = prefs.getString(PREFS_ENTRIES_KEY, null) ?: return emptyList()
    return runCatching {
        val array = JSONArray(raw)
        buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                val timestamp = item.optLong("timestamp", System.currentTimeMillis() - (index * 60_000L))
                val entry = Entry(
                    id = item.optString("id").ifBlank { legacyId(timestamp, item.optString("title")) },
                    title = item.optString("title").ifBlank { deriveEntryTitle(item.optString("situation"), AppLanguage.KO) },
                    situation = item.optString("situation"),
                    feeling = item.optString("feeling"),
                    mentalState = item.optString("mentalState"),
                    lesson = item.optString("lesson"),
                    aiReflection = item.optString("aiReflection"),
                    tag = item.optString("tag"),
                    timestamp = timestamp
                )
                if (!isLegacySampleEntry(entry)) {
                    add(entry)
                }
            }
        }
    }.getOrElse { emptyList() }
}

fun saveEntries(context: Context, entries: List<Entry>) {
    val prefs = appPreferences(context)
    val array = JSONArray()
    entries.forEach { entry ->
        array.put(
            JSONObject().apply {
                put("id", entry.id)
                put("title", entry.title)
                put("situation", entry.situation)
                put("feeling", entry.feeling)
                put("mentalState", entry.mentalState)
                put("lesson", entry.lesson)
                put("aiReflection", entry.aiReflection)
                put("tag", entry.tag)
                put("timestamp", entry.timestamp)
            }
        )
    }
    prefs.edit().putString(PREFS_ENTRIES_KEY, array.toString()).apply()
}

fun loadProductState(context: Context): ProductState {
    val prefs = appPreferences(context)
    val legacyPremium = prefs.getBoolean(PREFS_PREMIUM_KEY, false)
    val hasAiUnlock = prefs.contains(PREFS_AI_COACH_UNLOCK_KEY)
    val hasTrendUnlock = prefs.contains(PREFS_TREND_UNLOCK_KEY)
    val hasPdfCredits = prefs.contains(PREFS_PDF_EXPORT_CREDITS_KEY)
    return ProductState(
        lockEnabled = prefs.getBoolean(PREFS_LOCK_ENABLED_KEY, false),
        sortDescending = prefs.getBoolean(PREFS_SORT_DESC_KEY, true),
        onboardingDone = prefs.getBoolean(PREFS_ONBOARDING_DONE_KEY, false),
        language = runCatching {
            AppLanguage.valueOf(prefs.getString(PREFS_LANGUAGE_KEY, AppLanguage.KO.name) ?: AppLanguage.KO.name)
        }.getOrDefault(AppLanguage.KO),
        fontStyle = runCatching {
            AppFontStyle.valueOf(prefs.getString(PREFS_FONT_STYLE_KEY, AppFontStyle.SANS.name) ?: AppFontStyle.SANS.name)
        }.getOrDefault(AppFontStyle.SANS),
        aiCoachUnlockedUntil = when {
            hasAiUnlock -> prefs.getLong(PREFS_AI_COACH_UNLOCK_KEY, 0L)
            legacyPremium -> Long.MAX_VALUE
            else -> 0L
        },
        trendUnlockedUntil = when {
            hasTrendUnlock -> prefs.getLong(PREFS_TREND_UNLOCK_KEY, 0L)
            legacyPremium -> Long.MAX_VALUE
            else -> 0L
        },
        pdfExportCredits = when {
            hasPdfCredits -> prefs.getInt(PREFS_PDF_EXPORT_CREDITS_KEY, 0)
            legacyPremium -> 99
            else -> 0
        }
    )
}

fun saveProductState(context: Context, productState: ProductState) {
    appPreferences(context).edit()
        .putBoolean(PREFS_PREMIUM_KEY, false)
        .putBoolean(PREFS_LOCK_ENABLED_KEY, productState.lockEnabled)
        .putBoolean(PREFS_SORT_DESC_KEY, productState.sortDescending)
        .putBoolean(PREFS_ONBOARDING_DONE_KEY, productState.onboardingDone)
        .putString(PREFS_LANGUAGE_KEY, productState.language.name)
        .putString(PREFS_FONT_STYLE_KEY, productState.fontStyle.name)
        .putLong(PREFS_AI_COACH_UNLOCK_KEY, productState.aiCoachUnlockedUntil)
        .putLong(PREFS_TREND_UNLOCK_KEY, productState.trendUnlockedUntil)
        .putInt(PREFS_PDF_EXPORT_CREDITS_KEY, productState.pdfExportCredits)
        .apply()
}

private fun appPreferences(context: Context): SharedPreferences {
    return securePreferences(context)
}

private fun securePreferences(context: Context): SharedPreferences {
    val encryptedPrefs = runCatching {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            SECURE_PREFS_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }.getOrNull()

    if (encryptedPrefs != null) {
        migrateLegacyPreferences(context, encryptedPrefs)
        return encryptedPrefs
    }

    return context.getSharedPreferences(LEGACY_PREFS_NAME, Context.MODE_PRIVATE)
}

private fun migrateLegacyPreferences(context: Context, targetPrefs: SharedPreferences) {
    val legacyPrefs = context.getSharedPreferences(LEGACY_PREFS_NAME, Context.MODE_PRIVATE)
    if (legacyPrefs.all.isEmpty()) return

    val editor = targetPrefs.edit()
    copyStringIfMissing(legacyPrefs, targetPrefs, editor, PREFS_ENTRIES_KEY)
    copyBooleanIfMissing(legacyPrefs, targetPrefs, editor, PREFS_PREMIUM_KEY)
    copyBooleanIfMissing(legacyPrefs, targetPrefs, editor, PREFS_LOCK_ENABLED_KEY)
    copyBooleanIfMissing(legacyPrefs, targetPrefs, editor, PREFS_SORT_DESC_KEY)
    copyBooleanIfMissing(legacyPrefs, targetPrefs, editor, PREFS_ONBOARDING_DONE_KEY)
    copyStringIfMissing(legacyPrefs, targetPrefs, editor, PREFS_LANGUAGE_KEY)
    copyStringIfMissing(legacyPrefs, targetPrefs, editor, PREFS_FONT_STYLE_KEY)
    copyLongIfMissing(legacyPrefs, targetPrefs, editor, PREFS_AI_COACH_UNLOCK_KEY)
    copyLongIfMissing(legacyPrefs, targetPrefs, editor, PREFS_TREND_UNLOCK_KEY)
    copyIntIfMissing(legacyPrefs, targetPrefs, editor, PREFS_PDF_EXPORT_CREDITS_KEY)
    editor.apply()
}

private fun copyStringIfMissing(
    from: SharedPreferences,
    to: SharedPreferences,
    editor: SharedPreferences.Editor,
    key: String
) {
    if (!to.contains(key) && from.contains(key)) {
        editor.putString(key, from.getString(key, null))
    }
}

private fun copyBooleanIfMissing(
    from: SharedPreferences,
    to: SharedPreferences,
    editor: SharedPreferences.Editor,
    key: String
) {
    if (!to.contains(key) && from.contains(key)) {
        editor.putBoolean(key, from.getBoolean(key, false))
    }
}

private fun copyLongIfMissing(
    from: SharedPreferences,
    to: SharedPreferences,
    editor: SharedPreferences.Editor,
    key: String
) {
    if (!to.contains(key) && from.contains(key)) {
        editor.putLong(key, from.getLong(key, 0L))
    }
}

private fun copyIntIfMissing(
    from: SharedPreferences,
    to: SharedPreferences,
    editor: SharedPreferences.Editor,
    key: String
) {
    if (!to.contains(key) && from.contains(key)) {
        editor.putInt(key, from.getInt(key, 0))
    }
}

private fun legacyId(timestamp: Long, title: String): String {
    return "$timestamp-${title.hashCode()}"
}

private fun isLegacySampleEntry(entry: Entry): Boolean {
    return entry.title in setOf(
        "급하게 말해서 친구를 상처 줌",
        "회의에서 방어적으로 대답함",
        "해야 할 연락을 또 미룸"
    )
}
