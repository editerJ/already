package com.futureself.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

data class AppPreferences(
    val preferredLanguageTag: String = "system",
    val morningNotificationsEnabled: Boolean = true,
    val eveningNotificationsEnabled: Boolean = false,
    val aiGuideUnlockedUntil: Long = 0L,
    val timelineUnlockedUntil: Long = 0L,
    val woopUnlockedUntil: Long = 0L,
    val futureLetterUnlockedUntil: Long = 0L
)

object FutureSelfPreferenceKeys {
    val preferredLanguageTag = stringPreferencesKey("preferred_language_tag")
    val morningNotificationsEnabled = booleanPreferencesKey("morning_notifications_enabled")
    val eveningNotificationsEnabled = booleanPreferencesKey("evening_notifications_enabled")
    val aiGuideUnlockedUntil = longPreferencesKey("ai_guide_unlocked_until")
    val timelineUnlockedUntil = longPreferencesKey("timeline_unlocked_until")
    val woopUnlockedUntil = longPreferencesKey("woop_unlocked_until")
    val futureLetterUnlockedUntil = longPreferencesKey("future_letter_unlocked_until")
}
