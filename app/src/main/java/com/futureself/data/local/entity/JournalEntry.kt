package com.futureself.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String = "local_user",
    val content: String,
    val domain: String,
    val targetYear: Int,
    val targetYearLabel: String,
    val createdAt: Long = System.currentTimeMillis(),
    val languageTag: String = "system",
    val obstacle: String? = null,
    val emotion: String? = null,
    val ifThenPlan: String? = null,
    val missionId: String? = null
)
