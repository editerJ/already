package com.futureself.ui

import android.os.Build
import com.futureself.FutureSelfApplication
import com.futureself.data.local.entity.DailyMission
import com.already.app.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

object FutureSelfStrings {
    private val context
        get() = FutureSelfApplication.appContext

    private fun locale(): Locale {
        val configuration = context.resources.configuration
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.locales[0] ?: Locale.getDefault()
        } else {
            @Suppress("DEPRECATION")
            configuration.locale ?: Locale.getDefault()
        }
    }

    fun string(resId: Int, vararg args: Any): String {
        return context.getString(resId, *args)
    }

    fun homeDateText(): String {
        return LocalDate.now().format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale())
        )
    }

    fun monthLabel(): String {
        val currentLocale = locale()
        val pattern = when (currentLocale.language) {
            "ko", "ja", "zh" -> "M月"
            else -> "LLLL"
        }
        return LocalDate.now().format(DateTimeFormatter.ofPattern(pattern, currentLocale))
    }

    fun shortDate(epochMillis: Long): String {
        val currentLocale = locale()
        val formatter = when (currentLocale.language) {
            "ko", "ja", "zh" -> DateTimeFormatter.ofPattern("M.d", currentLocale)
            else -> DateTimeFormatter.ofPattern("MMM d", currentLocale)
        }

        return Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(formatter)
    }

    fun rewardDate(epochMillis: Long): String {
        return Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale()))
    }

    fun defaultMission(
        journalId: String,
        isCompleted: Boolean
    ): DailyMission {
        return DailyMission(
            journalId = journalId,
            mission = string(R.string.future_self_default_mission),
            why = string(R.string.future_self_default_mission_why),
            duration = string(R.string.future_self_default_mission_duration),
            domain = "health",
            isCompleted = isCompleted
        )
    }
}