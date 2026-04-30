package com.futureself.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.futureself.data.local.dao.JournalDao
import com.futureself.data.local.dao.MissionDao
import com.futureself.data.local.dao.UserProfileDao
import com.futureself.data.local.entity.DailyMission
import com.futureself.data.local.entity.JournalEntry
import com.futureself.data.local.entity.UserProfile

@Database(
    entities = [JournalEntry::class, DailyMission::class, UserProfile::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
    abstract fun missionDao(): MissionDao
    abstract fun userProfileDao(): UserProfileDao
}
