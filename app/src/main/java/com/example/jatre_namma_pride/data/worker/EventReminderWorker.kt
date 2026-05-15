package com.example.jatre_namma_pride.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.jatre_namma_pride.data.model.AppDatabase
import com.example.jatre_namma_pride.data.model.AppNotification
import com.example.jatre_namma_pride.ui.components.NotificationHelper

class EventReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val eventId = inputData.getInt("eventId", -1)
        val eventNameEn = inputData.getString("eventNameEn") ?: return Result.failure()
        val eventNameKn = inputData.getString("eventNameKn") ?: ""
        val locationEn = inputData.getString("locationEn") ?: "Event Location"
        val locationKn = inputData.getString("locationKn") ?: ""

        val config = context.resources.configuration
        val eventName = com.example.jatre_namma_pride.util.LocalizationHelper.resolve(eventNameEn, eventNameKn, config)
        val location = com.example.jatre_namma_pride.util.LocalizationHelper.resolve(locationEn, locationKn, config)

        val title = context.getString(com.example.jatre_namma_pride.R.string.notif_event_title, eventName)
        val message = context.getString(com.example.jatre_namma_pride.R.string.notif_event_message, eventName, location)

        // Trigger Mobile Notification
        NotificationHelper.showNotification(context, title, message)

        // Save to Database
        val db = AppDatabase.getDatabase(context)
        val notificationDao = db.notificationDao()
        
        // Prevent duplicate notifications for the same event
        if (eventId != -1) {
            val existing = notificationDao.getNotificationForEvent(eventId)
            if (existing == null) {
                notificationDao.insertNotification(
                    AppNotification(
                        title = title,
                        message = message,
                        timestamp = System.currentTimeMillis(),
                        isRead = false,
                        eventId = eventId
                    )
                )
            }
        }

        return Result.success()
    }
}
