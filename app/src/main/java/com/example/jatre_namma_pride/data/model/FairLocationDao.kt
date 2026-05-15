package com.example.jatre_namma_pride.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
@JvmSuppressWildcards
interface FairLocationDao {
    @Query("SELECT * FROM fair_locations")
    fun getAllLocations(): Flow<List<FairLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(locations: List<FairLocation>): List<Long>

    @Query("SELECT COUNT(*) FROM fair_locations")
    suspend fun getLocationCount(): Int
}
