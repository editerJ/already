package com.futureself.data.repository

import com.futureself.core.model.FutureSelfFeature
import com.futureself.core.model.RewardWindow
import com.futureself.data.local.entity.DailyMission
import com.futureself.data.local.entity.JournalEntry
import com.futureself.data.local.entity.UserProfile
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    fun observeTimeline(): Flow<List<JournalEntry>>
    suspend fun save(entry: JournalEntry): Result<Unit>
}

interface MissionRepository {
    fun observeCurrentMissions(): Flow<List<DailyMission>>
    suspend fun save(mission: DailyMission): Result<Unit>
}

interface UserRepository {
    fun observeProfile(): Flow<UserProfile?>
    suspend fun save(profile: UserProfile): Result<Unit>
}

interface RewardAccessRepository {
    fun isUnlocked(feature: FutureSelfFeature): Flow<Boolean>
    suspend fun grant(feature: FutureSelfFeature, window: RewardWindow): Result<Unit>
}

interface AiCoachGateway {
    suspend fun callClaude(systemPrompt: String, userMessage: String, maxTokens: Int = 512): Result<String>
}
