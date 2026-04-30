package com.futureself.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.futureself.data.local.entity.JournalEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeAll(userId: String = "local_user"): Flow<List<JournalEntry>>

    @Query(
        "SELECT * FROM journal_entries WHERE userId = :userId AND targetYear IN (:years) ORDER BY targetYear ASC, createdAt DESC"
    )
    fun observeTimeline(
        userId: String = "local_user",
        years: List<Int> = listOf(1, 3, 5, 10)
    ): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_entries WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): JournalEntry?

    @Upsert
    suspend fun upsert(entry: JournalEntry)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteById(id: String)
}
