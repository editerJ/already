package com.futureself.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.futureself.data.local.entity.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = :id LIMIT 1")
    fun observe(id: String = "local_user"): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = :id LIMIT 1")
    suspend fun findById(id: String = "local_user"): UserProfile?

    @Upsert
    suspend fun upsert(profile: UserProfile)
}
