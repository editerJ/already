package com.futureself.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import org.json.JSONArray
import org.json.JSONObject

data class PersistedQuickWin(
    val id: String,
    val text: String,
    val createdAt: Long
)

data class PersistedRewardState(
    val preferredLanguageTag: String = "system",
    val coachingRewardEndsAt: Long = 0L,
    val completedActionDates: Set<String> = emptySet(),
    val quickWins: List<PersistedQuickWin> = emptyList()
)

class FutureSelfRewardStore(context: Context) {
    private val preferences = securePreferences(context)

    fun load(): PersistedRewardState {
        return PersistedRewardState(
            preferredLanguageTag = preferences.getString(KEY_PREFERRED_LANGUAGE_TAG, "system") ?: "system",
            coachingRewardEndsAt = preferences.getLong(KEY_COACHING_REWARD_ENDS_AT, 0L),
            completedActionDates = preferences.getStringSet(KEY_COMPLETED_ACTION_DATES, emptySet()).orEmpty(),
            quickWins = decodeQuickWins(preferences.getString(KEY_QUICK_WINS, null))
        )
    }

    fun save(state: PersistedRewardState) {
        preferences.edit()
            .putString(KEY_PREFERRED_LANGUAGE_TAG, state.preferredLanguageTag)
            .putLong(KEY_COACHING_REWARD_ENDS_AT, state.coachingRewardEndsAt)
            .putStringSet(KEY_COMPLETED_ACTION_DATES, state.completedActionDates)
            .putString(KEY_QUICK_WINS, encodeQuickWins(state.quickWins))
            .apply()
    }

    fun resetCoachingData(preserveLanguageTag: String) {
        save(PersistedRewardState(preferredLanguageTag = preserveLanguageTag))
    }

    private fun encodeQuickWins(items: List<PersistedQuickWin>): String {
        val array = JSONArray()
        items.forEach { item ->
            array.put(
                JSONObject()
                    .put("id", item.id)
                    .put("text", item.text)
                    .put("createdAt", item.createdAt)
            )
        }
        return array.toString()
    }

    private fun decodeQuickWins(raw: String?): List<PersistedQuickWin> {
        if (raw.isNullOrBlank()) return emptyList()
        val array = JSONArray(raw)
        return buildList(array.length()) {
            repeat(array.length()) { index ->
                val item = array.getJSONObject(index)
                add(
                    PersistedQuickWin(
                        id = item.optString("id"),
                        text = item.optString("text"),
                        createdAt = item.optLong("createdAt")
                    )
                )
            }
        }
    }

    private companion object {
        const val PREFS_NAME = "future_self_reward_store_secure"
        const val LEGACY_PREFS_NAME = "future_self_reward_store"
        const val KEY_PREFERRED_LANGUAGE_TAG = "preferred_language_tag"
        const val KEY_COACHING_REWARD_ENDS_AT = "coaching_reward_ends_at"
        const val KEY_COMPLETED_ACTION_DATES = "completed_action_dates"
        const val KEY_QUICK_WINS = "quick_wins"
    }

    private fun securePreferences(context: Context): SharedPreferences {
        val encryptedPrefs = runCatching {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                PREFS_NAME,
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
        copyStringIfMissing(legacyPrefs, targetPrefs, editor, KEY_PREFERRED_LANGUAGE_TAG)
        copyLongIfMissing(legacyPrefs, targetPrefs, editor, KEY_COACHING_REWARD_ENDS_AT)
        copyStringSetIfMissing(legacyPrefs, targetPrefs, editor, KEY_COMPLETED_ACTION_DATES)
        copyStringIfMissing(legacyPrefs, targetPrefs, editor, KEY_QUICK_WINS)
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

    private fun copyStringSetIfMissing(
        from: SharedPreferences,
        to: SharedPreferences,
        editor: SharedPreferences.Editor,
        key: String
    ) {
        if (!to.contains(key) && from.contains(key)) {
            editor.putStringSet(key, from.getStringSet(key, emptySet()))
        }
    }
}
