package com.futureself.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "daily_missions")
data class DailyMission(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val journalId: String,
    val mission: String,
    val why: String,
    val duration: String,
    val domain: String,
    val rewardSource: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
