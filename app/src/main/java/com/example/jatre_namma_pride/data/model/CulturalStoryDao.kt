package com.example.jatre_namma_pride.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
@JvmSuppressWildcards
interface CulturalStoryDao {
    @Query("SELECT * FROM cultural_stories")
    fun getAllStories(): Flow<List<CulturalStory>>

    @Query("SELECT * FROM cultural_stories WHERE id = :storyId")
    fun getStoryById(storyId: Int): Flow<CulturalStory?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stories: List<CulturalStory>): List<Long>

    @Query("SELECT COUNT(*) FROM cultural_stories")
    suspend fun getStoryCount(): Int
}
