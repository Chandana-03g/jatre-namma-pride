package com.example.jatre_namma_pride.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
@JvmSuppressWildcards
interface EventDao {
    @Query("SELECT * FROM events")
    fun getAllEvents(): Flow<List<JatreEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<JatreEvent>): List<Long>

    @Query("SELECT COUNT(*) FROM events")
    suspend fun getEventCount(): Int
}
