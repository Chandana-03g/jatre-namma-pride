package com.example.jatre_namma_pride.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
@JvmSuppressWildcards
interface LostFoundDao {
    @Query("SELECT * FROM lost_found_items ORDER BY id DESC")
    fun getAllItems(): Flow<List<LostFoundItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: LostFoundItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<LostFoundItem>): List<Long>

    @Update
    suspend fun updateItem(item: LostFoundItem): Int

    @Query("SELECT COUNT(*) FROM lost_found_items")
    suspend fun getCount(): Int
}
