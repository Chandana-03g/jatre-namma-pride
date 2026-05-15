package com.example.jatre_namma_pride.data.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.jatre_namma_pride.data.model.JatreEvent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    
    fun scheduleEventReminders(context: Context, events: List<JatreEvent>) {
        val workManager = WorkManager.getInstance(context)
        val timeFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)
        
        for (event in events) {
            try {
                val eventTimeStr = event.time
                val parsedTime = timeFormat.parse(eventTimeStr) ?: continue
                
                // Try to parse exact date, fallback to relative day offset
                val parsedDate = runCatching { SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(event.date) }.getOrNull()
                val eventCalendar = Calendar.getInstance().apply {
                    if (parsedDate != null) time = parsedDate
                    val parsedCalendar = Calendar.getInstance().apply { time = parsedTime }
                    set(Calendar.HOUR_OF_DAY, parsedCalendar.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, parsedCalendar.get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0)
                    if (parsedDate == null) {
                        val dayOffset = (event.day - 1).coerceAtLeast(0)
                        add(Calendar.DAY_OF_YEAR, dayOffset)
                    }
                }
                
                val now = Calendar.getInstance()
                
                // If event time has already passed today, don't schedule
                if (eventCalendar.before(now)) {
                    continue
                }
                
                // We want to trigger 10 minutes before the event
                val notificationTime = eventCalendar.timeInMillis - TimeUnit.MINUTES.toMillis(10)
                val delay = notificationTime - now.timeInMillis
                
                if (delay > 0) {
                    val inputData = Data.Builder()
                        .putInt("eventId", event.id)
                        .putString("eventNameEn", event.nameEn)
                        .putString("eventNameKn", event.nameKn)
                        .putString("locationEn", event.locationEn)
                        .putString("locationKn", event.locationKn)
                        .build()

                    val workRequest = OneTimeWorkRequestBuilder<EventReminderWorker>()
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .build()

                    workManager.enqueueUniqueWork(
                        "event_reminder_${event.id}",
                        ExistingWorkPolicy.REPLACE,
                        workRequest
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
