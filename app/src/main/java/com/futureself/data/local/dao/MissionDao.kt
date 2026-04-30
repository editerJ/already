package com.futureself.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.futureself.data.local.entity.DailyMission
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionDao {
    @Query("SELECT * FROM daily_missions WHERE journalId = :journalId LIMIT 1")
    suspend fun findByJournalId(journalId: String): DailyMission?

    @Query("SELECT * FROM daily_missions WHERE isCompleted = 0 ORDER BY createdAt DESC")
    fun observeOpenMissions(): Flow<List<DailyMission>>

    @Query("SELECT * FROM daily_missions ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<DailyMission>>

    @Upsert
    suspend fun upsert(mission: DailyMission)
}
