package com.example.jatre_namma_pride.data.repository

import com.example.jatre_namma_pride.R
import com.example.jatre_namma_pride.data.model.CulturalStory
import com.example.jatre_namma_pride.data.model.EventCategory
import com.example.jatre_namma_pride.data.model.EventStatus
import com.example.jatre_namma_pride.data.model.JatreEvent
import com.example.jatre_namma_pride.util.EmojiConstants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Provides event schedule and cultural stories from Firebase Firestore.
 * On first launch, seeds Firestore with the default event list if empty.
 */
object EventRepository {

    private val db = FirebaseFirestore.getInstance()
    private val eventsCol = db.collection("events")
    private val storiesCol = db.collection("culturalStories")
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)

    // ── Firestore seeding (runs once if collections are empty) ───────────────

    suspend fun seedIfNeeded() {
        val eventsCount = eventsCol.get().await().size()
        if (eventsCount != sampleEvents.size) {
            val snapshot = eventsCol.get().await()
            val batch = db.batch()
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            sampleEvents.forEach { event ->
                batch.set(eventsCol.document(event.id.toString()), event.toMap())
            }
            batch.commit().await()
        }

        val storiesSnapshot = storiesCol.get().await()
        if (storiesSnapshot.size() != sampleStories.size) {
            val batch = db.batch()
            storiesSnapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            sampleStories.forEach { story ->
                batch.set(storiesCol.document(story.id.toString()), story.toMap())
            }
            batch.commit().await()
        }
    }

    // ── Real-time Flows ──────────────────────────────────────────────────────

    fun getAllEvents(): Flow<List<JatreEvent>> = callbackFlow {
        val listener = eventsCol
            .orderBy("day")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val events = snapshot.documents.mapNotNull { doc ->
                    runCatching { JatreEvent.fromMap(doc.data ?: emptyMap()) }.getOrNull()
                }.map { updateStatus(it) }
                trySend(events)
            }
        awaitClose { listener.remove() }
    }

    fun getLiveEvents(): Flow<List<JatreEvent>> =
        getAllEvents().map { events -> events.filter { it.status == EventStatus.LIVE } }

    fun getNextEvent(): Flow<JatreEvent?> =
        getAllEvents().map { events -> events.firstOrNull { it.status == EventStatus.UPCOMING } }

    fun getEventsByCategory(category: EventCategory): Flow<List<JatreEvent>> =
        getAllEvents().map { events ->
            if (category == EventCategory.ALL) events
            else events.filter { it.category == category }
        }

    fun getCulturalStories(): Flow<List<CulturalStory>> = callbackFlow {
        val listener = storiesCol
            .orderBy("id")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val stories = snapshot.documents.mapNotNull { doc ->
                    runCatching { CulturalStory.fromMap(doc.data ?: emptyMap()) }.getOrNull()
                }.map { story ->
                    // Fix: R.drawable IDs change across builds. Never trust the int from Firestore.
                    val localSeed = sampleStories.find { it.id == story.id }
                    if (localSeed != null) {
                        story.copy(imageResId = localSeed.imageResId)
                    } else {
                        story.copy(imageResId = 0)
                    }
                }
                trySend(stories)
            }
        awaitClose { listener.remove() }
    }

    fun getStoryById(id: Int): Flow<CulturalStory?> =
        getCulturalStories().map { stories -> stories.firstOrNull { it.id == id } }

    // ── Status computation (client-side, based on real-time clock) ───────────

    private fun updateStatus(event: JatreEvent): JatreEvent {
        return try {
            val dateStr = event.date
            val parsedDate = runCatching { SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).parse(dateStr) }.getOrNull()

            val startCal = Calendar.getInstance().apply {
                if (parsedDate != null) time = parsedDate
                val parsed = timeFormat.parse(event.time) ?: return event
                val tCal = Calendar.getInstance().apply { time = parsed }
                set(Calendar.HOUR_OF_DAY, tCal.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, tCal.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                if (parsedDate == null) {
                    val dayOffset = (event.day - 1).coerceAtLeast(0)
                    add(Calendar.DAY_OF_YEAR, dayOffset)
                }
            }
            val endCal = Calendar.getInstance().apply {
                if (parsedDate != null) time = parsedDate
                val parsed = timeFormat.parse(event.endTime) ?: return event
                val tCal = Calendar.getInstance().apply { time = parsed }
                set(Calendar.HOUR_OF_DAY, tCal.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, tCal.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                if (parsedDate == null) {
                    val dayOffset = (event.day - 1).coerceAtLeast(0)
                    add(Calendar.DAY_OF_YEAR, dayOffset)
                }
            }
            val now = Calendar.getInstance()
            val newStatus = when {
                now.before(startCal) -> EventStatus.UPCOMING
                now.after(endCal)    -> EventStatus.PAST
                else                 -> EventStatus.LIVE
            }
            event.copy(status = newStatus)
        } catch (e: Exception) {
            event
        }
    }

    // ── Sample seed data ─────────────────────────────────────────────────────

    private val sampleEvents = listOf(
        // DAY 1
        JatreEvent(
            id = 1, time = "5:30 AM", endTime = "7:00 AM",
            nameEn = "Suprabhata Seva", nameKn = "ಸುಪ್ರಭಾತ ಸೇವೆ",
            descriptionEn = "Opening ceremony and early morning prayers",
            descriptionKn = "ಉದ್ಘಾಟನಾ ಸಮಾರಂಭ ಮತ್ತು ಮುಂಜಾನೆಯ ಪ್ರಾರ್ಥನೆಗಳು",
            locationEn = "Zone A — Temple Area", locationKn = "ವಲಯ A — ದೇವಸ್ಥಾನ ಪ್ರದೇಶ",
            category = EventCategory.RELIGIOUS, status = EventStatus.UPCOMING, day = 1, date = "16 May 2026"
        ),
        JatreEvent(
            id = 2, time = "7:00 AM", endTime = "9:00 AM",
            nameEn = "Maha Pooja", nameKn = "ಮಹಾ ಪೂಜೆ",
            descriptionEn = "Grand morning rituals and prayers",
            descriptionKn = "ಭವ್ಯ ಮುಂಜಾನೆಯ ಆಚರಣೆಗಳು ಮತ್ತು ಪ್ರಾರ್ಥನೆಗಳು",
            locationEn = "Zone A — Temple Area", locationKn = "ವಲಯ A — ದೇವಸ್ಥಾನ ಪ್ರದೇಶ",
            category = EventCategory.RELIGIOUS, status = EventStatus.UPCOMING, day = 1, date = "16 May 2026"
        ),
        JatreEvent(
            id = 3, time = "9:00 AM", endTime = "11:00 AM",
            nameEn = "Annadanam Begins", nameKn = "ಅನ್ನದಾನ ಪ್ರಾರಂಭ",
            descriptionEn = "Free community meal service begins",
            descriptionKn = "ಉಚಿತ ಸಮುದಾಯ ಭೋಜನ ಸೇವೆ ಪ್ರಾರಂಭ",
            locationEn = "Zone B — Food Court", locationKn = "ವಲಯ B — ಆಹಾರ ಮಳಿಗೆ",
            category = EventCategory.CULTURAL, status = EventStatus.UPCOMING, day = 1, date = "16 May 2026"
        ),
        JatreEvent(
            id = 4, time = "11:00 AM", endTime = "2:00 PM",
            nameEn = "Bhajane Program", nameKn = "ಭಜನೆ ಕಾರ್ಯಕ್ರಮ",
            descriptionEn = "Devotional songs and musical performance",
            descriptionKn = "ಭಕ್ತಿಗೀತೆಗಳು ಮತ್ತು ಸಂಗೀತ ಪ್ರದರ್ಶನ",
            locationEn = "Zone D — Main Stage", locationKn = "ವಲಯ D — ಮುಖ್ಯ ವೇದಿಕೆ",
            category = EventCategory.CULTURAL, status = EventStatus.UPCOMING, day = 1, date = "16 May 2026"
        ),
        JatreEvent(
            id = 5, time = "2:00 PM", endTime = "5:00 PM",
            nameEn = "Dollu Kunitha", nameKn = "ಡೊಳ್ಳು ಕುಣಿತ",
            descriptionEn = "Traditional drum dance performance",
            descriptionKn = "ಸಾಂಪ್ರದಾಯಿಕ ಡ್ರಮ್ ನೃತ್ಯ ಪ್ರದರ್ಶನ",
            locationEn = "Zone D — Main Stage", locationKn = "ವಲಯ D — ಮುಖ್ಯ ವೇದಿಕೆ",
            category = EventCategory.CULTURAL, status = EventStatus.UPCOMING, day = 1, date = "16 May 2026"
        ),
        JatreEvent(
            id = 6, time = "5:00 PM", endTime = "7:00 PM",
            nameEn = "Rathotsava Preparation", nameKn = "ರಥೋತ್ಸವ ಸಿದ್ಧತೆ",
            descriptionEn = "Preparation for the grand chariot festival",
            descriptionKn = "ಭವ್ಯ ರಥೋತ್ಸವದ ಸಿದ್ಧತೆ",
            locationEn = "Zone A — Temple Area", locationKn = "ವಲಯ A — ದೇವಸ್ಥಾನ ಪ್ರದೇಶ",
            category = EventCategory.RELIGIOUS, status = EventStatus.UPCOMING, day = 1, date = "16 May 2026"
        ),
        JatreEvent(
            id = 7, time = "7:00 PM", endTime = "9:00 PM",
            nameEn = "Yakshagana Performance", nameKn = "ಯಕ್ಷಗಾನ ಪ್ರದರ್ಶನ",
            descriptionEn = "Traditional theater art form of Karnataka",
            descriptionKn = "ಕರ್ನಾಟಕದ ಸಾಂಪ್ರದಾಯಿಕ ರಂಗಕಲೆ",
            locationEn = "Zone D — Main Stage", locationKn = "ವಲಯ D — ಮುಖ್ಯ ವೇದಿಕೆ",
            category = EventCategory.CULTURAL, status = EventStatus.UPCOMING, day = 1, date = "16 May 2026"
        ),
        JatreEvent(
            id = 8, time = "9:00 PM", endTime = "11:00 PM",
            nameEn = "Deepotsava", nameKn = "ದೀಪೋತ್ಸವ",
            descriptionEn = "Festival of lights illuminating the temple",
            descriptionKn = "ದೇವಸ್ಥಾನವನ್ನು ಬೆಳಗಿಸುವ ದೀಪಗಳ ಹಬ್ಬ",
            locationEn = "Zone A — Temple Area", locationKn = "ವಲಯ A — ದೇವಸ್ಥಾನ ಪ್ರದೇಶ",
            category = EventCategory.RELIGIOUS, status = EventStatus.UPCOMING, day = 1, date = "16 May 2026"
        ),

        // DAY 2
        JatreEvent(
            id = 9, time = "6:00 AM", endTime = "8:00 AM",
            nameEn = "Morning Temple Pooja", nameKn = "ಬೆಳಗಿನ ದೇವಸ್ಥಾನ ಪೂಜೆ",
            descriptionEn = "Early morning temple rituals",
            descriptionKn = "ಮುಂಜಾನೆಯ ದೇವಸ್ಥಾನದ ಆಚರಣೆಗಳು",
            locationEn = "Zone A — Temple Area", locationKn = "ವಲಯ A — ದೇವಸ್ಥಾನ ಪ್ರದೇಶ",
            category = EventCategory.RELIGIOUS, status = EventStatus.UPCOMING, day = 2, date = "17 May 2026"
        ),
        JatreEvent(
            id = 10, time = "8:00 AM", endTime = "10:00 AM",
            nameEn = "Devotional Singing", nameKn = "ಭಕ್ತಿ ಗೀತೆಗಳು",
            descriptionEn = "Morning devotional music sessions",
            descriptionKn = "ಮುಂಜಾನೆಯ ಭಕ್ತಿ ಸಂಗೀತ ಕಾರ್ಯಕ್ರಮ",
            locationEn = "Zone D — Main Stage", locationKn = "ವಲಯ D — ಮುಖ್ಯ ವೇದಿಕೆ",
            category = EventCategory.CULTURAL, status = EventStatus.UPCOMING, day = 2, date = "17 May 2026"
        ),
        JatreEvent(
            id = 11, time = "10:00 AM", endTime = "1:00 PM",
            nameEn = "Village Market Opens", nameKn = "ಗ್ರಾಮದ ಮಾರುಕಟ್ಟೆ ಪ್ರಾರಂಭ",
            descriptionEn = "Traditional rural market and stalls",
            descriptionKn = "ಸಾಂಪ್ರದಾಯಿಕ ಗ್ರಾಮೀಣ ಮಾರುಕಟ್ಟೆ ಮತ್ತು ಮಳಿಗೆಗಳು",
            locationEn = "Zone B — Food Court", locationKn = "ವಲಯ B — ಆಹಾರ ಮಳಿಗೆ",
            category = EventCategory.CULTURAL, status = EventStatus.UPCOMING, day = 2, date = "17 May 2026"
        ),
        JatreEvent(
            id = 12, time = "1:00 PM", endTime = "4:00 PM",
            nameEn = "Traditional Games", nameKn = "ಸಾಂಪ್ರದಾಯಿಕ ಆಟಗಳು",
            descriptionEn = "Local sports and village games",
            descriptionKn = "ಸ್ಥಳೀಯ ಕ್ರೀಡೆಗಳು ಮತ್ತು ಗ್ರಾಮದ ಆಟಗಳು",
            locationEn = "Zone D — Main Stage", locationKn = "ವಲಯ D — ಮುಖ್ಯ ವೇದಿಕೆ",
            category = EventCategory.SPORTS, status = EventStatus.UPCOMING, day = 2, date = "17 May 2026"
        ),
        JatreEvent(
            id = 13, time = "4:00 PM", endTime = "6:00 PM",
            nameEn = "Folk Dance Competition", nameKn = "ಜಾನಪದ ನೃತ್ಯ ಸ್ಪರ್ಧೆ",
            descriptionEn = "Competition showcasing local folk dances",
            descriptionKn = "ಸ್ಥಳೀಯ ಜಾನಪದ ನೃತ್ಯಗಳನ್ನು ಪ್ರದರ್ಶಿಸುವ ಸ್ಪರ್ಧೆ",
            locationEn = "Zone D — Main Stage", locationKn = "ವಲಯ D — ಮುಖ್ಯ ವೇದಿಕೆ",
            category = EventCategory.CULTURAL, status = EventStatus.UPCOMING, day = 2, date = "17 May 2026"
        ),
        JatreEvent(
            id = 14, time = "6:00 PM", endTime = "8:00 PM",
            nameEn = "Cultural Procession", nameKn = "ಸಾಂಸ್ಕೃತಿಕ ಮೆರವಣಿಗೆ",
            descriptionEn = "Grand procession of artists and performers",
            descriptionKn = "ಕಲಾವಿದರು ಮತ್ತು ಪ್ರದರ್ಶಕರ ಭವ್ಯ ಮೆರವಣಿಗೆ",
            locationEn = "Main Pathway", locationKn = "ಮುಖ್ಯ ರಸ್ತೆ",
            category = EventCategory.CULTURAL, status = EventStatus.UPCOMING, day = 2, date = "17 May 2026"
        ),
        JatreEvent(
            id = 15, time = "8:00 PM", endTime = "10:00 PM",
            nameEn = "Yakshagana Night Show", nameKn = "ಯಕ್ಷಗಾನ ರಾತ್ರಿ ಪ್ರದರ್ಶನ",
            descriptionEn = "Nightlong Yakshagana performance",
            descriptionKn = "ರಾತ್ರಿಪೂರ್ತಿ ಯಕ್ಷಗಾನ ಪ್ರದರ್ಶನ",
            locationEn = "Zone D — Main Stage", locationKn = "ವಲಯ D — ಮುಖ್ಯ ವೇದಿಕೆ",
            category = EventCategory.CULTURAL, status = EventStatus.UPCOMING, day = 2, date = "17 May 2026"
        ),
        JatreEvent(
            id = 16, time = "10:00 PM", endTime = "11:30 PM",
            nameEn = "Temple Lighting Ceremony", nameKn = "ದೇವಸ್ಥಾನದ ದೀಪಾಲಂಕಾರ",
            descriptionEn = "Special evening illumination of the temple",
            descriptionKn = "ದೇವಸ್ಥಾನದ ವಿಶೇಷ ಸಂಜೆಯ ದೀಪಾಲಂಕಾರ",
            locationEn = "Zone A — Temple Area", locationKn = "ವಲಯ A — ದೇವಸ್ಥಾನ ಪ್ರದೇಶ",
            category = EventCategory.RELIGIOUS, status = EventStatus.UPCOMING, day = 2, date = "17 May 2026"
        ),

        // DAY 3
        JatreEvent(
            id = 17, time = "5:00 AM", endTime = "8:00 AM",
            nameEn = "Mangala Aarati", nameKn = "ಮಂಗಳಾರತಿ",
            descriptionEn = "Early morning auspicious prayers",
            descriptionKn = "ಮುಂಜಾನೆಯ ಮಂಗಳಕರ ಪ್ರಾರ್ಥನೆಗಳು",
            locationEn = "Zone A — Temple Area", locationKn = "ವಲಯ A — ದೇವಸ್ಥಾನ ಪ್ರದೇಶ",
            category = EventCategory.RELIGIOUS, status = EventStatus.UPCOMING, day = 3, date = "18 May 2026"
        ),
        JatreEvent(
            id = 18, time = "8:00 AM", endTime = "11:00 AM",
            nameEn = "Maha Annadanam", nameKn = "ಮಹಾ ಅನ್ನದಾನ",
            descriptionEn = "Grand community feast for all devotees",
            descriptionKn = "ಎಲ್ಲಾ ಭಕ್ತರಿಗೆ ಭವ್ಯ ಸಮುದಾಯ ಭೋಜನ",
            locationEn = "Zone B — Food Court", locationKn = "ವಲಯ B — ಆಹಾರ ಮಳಿಗೆ",
            category = EventCategory.CULTURAL, status = EventStatus.UPCOMING, day = 3, date = "18 May 2026"
        ),
        JatreEvent(
            id = 19, time = "11:00 AM", endTime = "2:00 PM",
            nameEn = "Devotional Music Concert", nameKn = "ಭಕ್ತಿ ಸಂಗೀತ ಕಛೇರಿ",
            descriptionEn = "Live performance by classical artists",
            descriptionKn = "ಶಾಸ್ತ್ರೀಯ ಕಲಾವಿದರಿಂದ ನೇರ ಪ್ರದರ್ಶನ",
            locationEn = "Zone D — Main Stage", locationKn = "ವಲಯ D — ಮುಖ್ಯ ವೇದಿಕೆ",
            category = EventCategory.CULTURAL, status = EventStatus.UPCOMING, day = 3, date = "18 May 2026"
        ),
        JatreEvent(
            id = 20, time = "2:00 PM", endTime = "5:00 PM",
            nameEn = "Grand Rathotsava", nameKn = "ಮಹಾರಥೋತ್ಸವ",
            descriptionEn = "The main chariot procession of the deity",
            descriptionKn = "ದೇವರ ಮುಖ್ಯ ರಥೋತ್ಸವ",
            locationEn = "Zone A — Temple Area", locationKn = "ವಲಯ A — ದೇವಸ್ಥಾನ ಪ್ರದೇಶ",
            category = EventCategory.RELIGIOUS, status = EventStatus.UPCOMING, day = 3, date = "18 May 2026"
        ),
        JatreEvent(
            id = 21, time = "5:00 PM", endTime = "6:30 PM",
            nameEn = "Fireworks Preparation", nameKn = "ಪಟಾಕಿ ಸಿದ್ಧತೆ",
            descriptionEn = "Arrangements for the grand evening fireworks",
            descriptionKn = "ಸಂಜೆಯ ಭವ್ಯ ಪಟಾಕಿ ಪ್ರದರ್ಶನದ ಸಿದ್ಧತೆ",
            locationEn = "Open Grounds", locationKn = "ಬಯಲು ಪ್ರದೇಶ",
            category = EventCategory.CULTURAL, status = EventStatus.UPCOMING, day = 3, date = "18 May 2026"
        ),
        JatreEvent(
            id = 22, time = "6:30 PM", endTime = "8:00 PM",
            nameEn = "Closing Ceremony", nameKn = "ಸಮಾರೋಪ ಸಮಾರಂಭ",
            descriptionEn = "Official conclusion of the festival",
            descriptionKn = "ಹಬ್ಬದ ಅಧಿಕೃತ ಮುಕ್ತಾಯ",
            locationEn = "Zone D — Main Stage", locationKn = "ವಲಯ D — ಮುಖ್ಯ ವೇದಿಕೆ",
            category = EventCategory.CULTURAL, status = EventStatus.UPCOMING, day = 3, date = "18 May 2026"
        ),
        JatreEvent(
            id = 23, time = "8:00 PM", endTime = "10:00 PM",
            nameEn = "Final Blessing Ceremony", nameKn = "ಅಂತಿಮ ಆಶೀರ್ವಾದ ಸಮಾರಂಭ",
            descriptionEn = "Final prayers and distribution of prasad",
            descriptionKn = "ಅಂತಿಮ ಪ್ರಾರ್ಥನೆ ಮತ್ತು ಪ್ರಸಾದ ವಿತರಣೆ",
            locationEn = "Zone A — Temple Area", locationKn = "ವಲಯ A — ದೇವಸ್ಥಾನ ಪ್ರದೇಶ",
            category = EventCategory.RELIGIOUS, status = EventStatus.UPCOMING, day = 3, date = "18 May 2026"
        ),

        // NEW SPORTS EVENTS
        JatreEvent(
            id = 24, time = "3:00 PM", endTime = "5:00 PM",
            nameEn = "Kusti Competition", nameKn = "ಕುಸ್ತಿ ಸ್ಪರ್ಧೆ",
            descriptionEn = "Traditional village wrestling match",
            descriptionKn = "ಸಾಂಪ್ರದಾಯಿಕ ಗ್ರಾಮ ಕುಸ್ತಿ ಪಂದ್ಯ",
            locationEn = "Open Grounds", locationKn = "ಬಯಲು ಪ್ರದೇಶ",
            category = EventCategory.SPORTS, status = EventStatus.UPCOMING, day = 1, date = "16 May 2026"
        ),
        JatreEvent(
            id = 25, time = "2:00 PM", endTime = "4:00 PM",
            nameEn = "Traditional Tug of War", nameKn = "ಹಗ್ಗಜಗ್ಗಾಟ",
            descriptionEn = "Inter-village tug of war championship",
            descriptionKn = "ಗ್ರಾಮಗಳ ನಡುವಿನ ಹಗ್ಗಜಗ್ಗಾಟ ಚಾಂಪಿಯನ್‌ಶಿಪ್",
            locationEn = "Open Grounds", locationKn = "ಬಯಲು ಪ್ರದೇಶ",
            category = EventCategory.SPORTS, status = EventStatus.UPCOMING, day = 2, date = "17 May 2026"
        ),
        JatreEvent(
            id = 26, time = "4:00 PM", endTime = "6:00 PM",
            nameEn = "Village Kabaddi Match", nameKn = "ಗ್ರಾಮ ಕಬಡ್ಡಿ ಪಂದ್ಯ",
            descriptionEn = "Local teams compete for the festival cup",
            descriptionKn = "ಸ್ಥಳೀಯ ತಂಡಗಳ ಕಬಡ್ಡಿ ಪಂದ್ಯಾವಳಿ",
            locationEn = "Open Grounds", locationKn = "ಬಯಲು ಪ್ರದೇಶ",
            category = EventCategory.SPORTS, status = EventStatus.UPCOMING, day = 2, date = "17 May 2026"
        ),
        JatreEvent(
            id = 27, time = "9:00 AM", endTime = "11:00 AM",
            nameEn = "Rural Sports Events", nameKn = "ಗ್ರಾಮೀಣ ಕ್ರೀಡಾಕೂಟ",
            descriptionEn = "Various traditional village games and races",
            descriptionKn = "ವಿವಿಧ ಸಾಂಪ್ರದಾಯಿಕ ಗ್ರಾಮೀಣ ಆಟಗಳು ಮತ್ತು ಓಟಗಳು",
            locationEn = "Open Grounds", locationKn = "ಬಯಲು ಪ್ರದೇಶ",
            category = EventCategory.SPORTS, status = EventStatus.UPCOMING, day = 3, date = "18 May 2026"
        )
    )

    private val sampleStories = listOf(
        CulturalStory(
            id = 1,
            titleEn = "The Story of Kadri Manjunatha Temple",
            titleKn = "ಕದ್ರಿ ಮಂಜುನಾಥ ದೇವಸ್ಥಾನದ ಇತಿಹಾಸ",
            categoryEn = "Temple History",
            categoryKn = "ದೇವಸ್ಥಾನದ ಇತಿಹಾಸ",
            readTimeEn = "5 min read",
            readTimeKn = "5 ನಿಮಿಷ ಓದು",
            summaryEn = "Discover the ancient history and cultural significance of the Kadri Manjunatha Temple.",
            summaryKn = "ಕದ್ರಿ ಮಂಜುನಾಥ ದೇವಸ್ಥಾನದ ಪ್ರಾಚೀನ ಇತಿಹಾಸ ಮತ್ತು ಸಾಂಸ್ಕೃತಿಕ ಮಹತ್ವವನ್ನು ತಿಳಿಯಿರಿ.",
            fullContentEn = """Kadri Manjunatha Temple is one of the oldest and most respected temples in Mangaluru, located in the Kadri hills area of coastal Karnataka. The temple is dedicated to Lord Manjunatha, a form of Lord Shiva worshipped with deep devotion across the region.

The history of the temple is believed to date back many centuries and reflects a unique blend of Hindu and Buddhist traditions. Historians and local traditions suggest that Kadri was once an important center of Buddhist learning before gradually becoming a major Shaiva pilgrimage site.

One of the most special features of the temple is its ancient bronze idol of Lord Manjunatha, which is considered among the oldest bronze idols in South India. Devotees from different parts of Karnataka visit the temple during the annual Jatre to seek blessings and participate in the spiritual celebrations.

The temple is also famous for its natural holy water springs, locally known as “Gomukha Theertha.” Water continuously flows from stone cow-faced outlets, and devotees believe the water has sacred and healing importance.

During the annual Kadri Jatre, the temple becomes the cultural heart of the community. Rituals such as Maha Pooja, Rathotsava, Deepotsava, Bhajane, and Yakshagana performances bring together thousands of devotees, artists, and families from across coastal Karnataka.

The Jatre is not only a religious event but also a celebration of local culture, unity, and tradition. Food stalls, cultural performances, devotional music, and community gatherings create a vibrant festive atmosphere around the temple.

Even today, Kadri Manjunatha Temple remains one of the most important spiritual and cultural landmarks of Mangaluru and continues to preserve the rich traditions of coastal Karnataka.

RELATED MAP ZONES:
• Zone A — Temple Area
• Zone D — Main Stage

KEY FESTIVAL EVENTS:
• Maha Pooja
• Rathotsava
• Deepotsava
• Yakshagana Performance""",
            fullContentKn = "",
            emoji = EmojiConstants.TEMPLE,
            imageResId = R.drawable.img_kadri_temple_story
        ),
        CulturalStory(
            id = 2,
            titleEn = "The Sacred Waters of Gomukha Theertha",
            titleKn = "ಗೋಮುಖ ತೀರ್ಥದ ಪವಿತ್ರ ನೀರು",
            categoryEn = "Temple Heritage",
            categoryKn = "ದೇವಸ್ಥಾನದ ಪರಂಪರೆ",
            readTimeEn = "4 min read",
            readTimeKn = "4 ನಿಮಿಷ ಓದು",
            summaryEn = "Learn about the spiritual and healing significance of the ancient Gomukha Theertha water springs.",
            summaryKn = "ಪ್ರಾಚೀನ ಗೋಮುಖ ತೀರ್ಥದ ನೀರಿನ ಬುಗ್ಗೆಗಳ ಆಧ್ಯಾತ್ಮಿಕ ಮತ್ತು ಗುಣಪಡಿಸುವ ಮಹತ್ವದ ಬಗ್ಗೆ ತಿಳಿಯಿರಿ.",
            fullContentEn = """One of the most unique and sacred parts of the Kadri Manjunatha Temple is the ancient water springs known as “Gomukha Theertha.”

Near the temple, water continuously flows from stone structures shaped like cow faces, called “Gomukha.” For generations, devotees have believed that these natural springs hold spiritual and healing significance.

According to local belief, the water from Gomukha Theertha is considered holy and is used by devotees before entering the temple for worship. Many people wash their hands and feet in the sacred water as a symbol of purification and respect before participating in poojas and rituals.

The springs are also historically important because Kadri was once associated with early spiritual traditions and meditation practices in coastal Karnataka. The continuous natural flow of water through the stone outlets has remained one of the temple’s most respected features for centuries.

During the annual Kadri Jatre, thousands of devotees visit the Gomukha Theertha area before attending Maha Pooja, Rathotsava, and Deepotsava celebrations. The peaceful sound of flowing water and temple bells creates a calm spiritual atmosphere around the temple complex.

Even today, Gomukha Theertha remains an important symbol of purity, devotion, and the deep spiritual traditions preserved within Kadri Manjunatha Temple.

RELATED MAP ZONES:
• Zone A — Temple Area

KEY FESTIVAL EVENTS:
• Maha Pooja
• Morning Temple Rituals
• Devotional Activities""",
            fullContentKn = "",
            emoji = EmojiConstants.WATER,
            imageResId = R.drawable.img_gomukha_theertha_story
        ),
        CulturalStory(
            id = 3,
            titleEn = "Yakshagana — The Night Theatre of Coastal Karnataka",
            titleKn = "ಯಕ್ಷಗಾನ — ಕರಾವಳಿ ಕರ್ನಾಟಕದ ರಾತ್ರಿ ರಂಗಭೂಮಿ",
            categoryEn = "Folk Art & Culture",
            categoryKn = "ಜಾನಪದ ಕಲೆ ಮತ್ತು ಸಂಸ್ಕೃತಿ",
            readTimeEn = "5 min read",
            readTimeKn = "5 ನಿಮಿಷ ಓದು",
            summaryEn = "Discover Yakshagana, the vibrant night theatre tradition that has been the soul of coastal Karnataka festivals for centuries.",
            summaryKn = "ಯಕ್ಷಗಾನ — ಕರಾವಳಿ ಕರ್ನಾಟಕದ ಜಾತ್ರೆಗಳ ಆತ್ಮವಾಗಿ ಶತಮಾನಗಳಿಂದ ಉಳಿದಿರುವ ರಾತ್ರಿ ರಂಗಭೂಮಿ ಸಂಪ್ರದಾಯ.",
            fullContentEn = """Yakshagana is one of the most famous traditional art forms of coastal Karnataka and has been deeply connected to temple festivals and village Jatres for centuries.

This unique folk theatre combines storytelling, dance, music, dialogue, colorful costumes, and dramatic facial makeup into a single unforgettable performance experience.

Most Yakshagana performances are based on stories from the Ramayana, Mahabharata, and ancient Hindu epics. Artists perform through the night using powerful expressions, energetic dance movements, and live musical narration.

In the coastal regions around Mangaluru, Yakshagana is not treated only as entertainment. It is considered a cultural and devotional tradition closely connected with temple rituals and community celebrations.

During the Kadri Jatre, large crowds gather near the Main Stage to watch overnight Yakshagana performances. Families, children, elders, and visitors from nearby villages sit together for hours enjoying the storytelling, music, and traditional performances.

The costumes used in Yakshagana are one of its most recognizable features. Performers wear large decorative headgear, bright clothing, ornaments, and detailed facial makeup representing kings, warriors, demons, and mythological characters.

The performance is accompanied by traditional instruments including the Chende, Maddale, Harmonium, and Cymbals. The loud drum beats and energetic music create a powerful festival atmosphere during the Jatre nights.

Even today, Yakshagana remains one of the strongest cultural identities of coastal Karnataka and continues to preserve local language, mythology, and artistic traditions across generations.

RELATED MAP ZONES:
• Zone D — Main Stage

KEY FESTIVAL EVENTS:
• Yakshagana Performance
• Yakshagana Night Show
• Cultural Programs""",
            fullContentKn = "",
            emoji = EmojiConstants.PERFORMING,
            imageResId = R.drawable.img_yakshagana_story
        )
    )
}
