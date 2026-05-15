package com.example.jatre_namma_pride.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
@JvmSuppressWildcards
interface SafetyAlertDao {
    @Query("SELECT * FROM safety_alerts WHERE isActive = 1 ORDER BY priority ASC, id DESC")
    fun getActiveAlerts(): Flow<List<SafetyAlert>>

    @Query("SELECT * FROM safety_alerts WHERE isActive = 1 ORDER BY priority ASC LIMIT 1")
    fun getHighestPriorityAlert(): Flow<SafetyAlert?>

    @Query("SELECT * FROM safety_alerts ORDER BY priority ASC, id DESC")
    fun getAllAlerts(): Flow<List<SafetyAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alerts: List<SafetyAlert>): List<Long>

    @Query("SELECT COUNT(*) FROM safety_alerts")
    suspend fun getAlertCount(): Int
}
