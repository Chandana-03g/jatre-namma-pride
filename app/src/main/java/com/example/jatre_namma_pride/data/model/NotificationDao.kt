package com.example.jatre_namma_pride.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
@JvmSuppressWildcards
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotification>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert
    suspend fun insertNotification(notification: AppNotification): Long

    @Update
    suspend fun updateNotification(notification: AppNotification): Int

    @Query("UPDATE notifications SET isRead = 1 WHERE isRead = 0")
    suspend fun markAllAsRead(): Int
    
    @Query("SELECT * FROM notifications WHERE eventId = :eventId LIMIT 1")
    suspend fun getNotificationForEvent(eventId: Int): AppNotification?
}
