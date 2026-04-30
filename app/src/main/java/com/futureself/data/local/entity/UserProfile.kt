package com.futureself.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Locale
import java.util.TimeZone

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: String = "local_user",
    val focusArea: String = "",
    val changeWish: String = "",
    val futureImage5y: String = "",
    val mainObstacle: String = "",
    val strength: String = "",
    val priorityDomains: String = "[]",
    val preferredLanguageTag: String = "system",
    val countryCode: String = Locale.getDefault().country,
    val timeZoneId: String = TimeZone.getDefault().id,
    val onboardingCompleted: Boolean = false,
    val totalJournalCount: Int = 0,
    val currentStreak: Int = 0,
    val lastActiveDate: Long = 0L,
    val cloudSyncEnabled: Boolean = false
)
