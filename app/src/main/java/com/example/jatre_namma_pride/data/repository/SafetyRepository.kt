package com.example.jatre_namma_pride.data.repository

import com.example.jatre_namma_pride.data.model.AlertType
import com.example.jatre_namma_pride.data.model.FairLocation
import com.example.jatre_namma_pride.data.model.MapMarker
import com.example.jatre_namma_pride.data.model.SafetyAlert
import com.example.jatre_namma_pride.util.EmojiConstants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Provides safety alerts and fair locations from Firebase Firestore.
 * Alerts updated in the Firebase console propagate to all users instantly.
 * Static safety guidelines remain in-memory (fixed rules, no DB needed).
 */
object SafetyRepository {

    private val db           = FirebaseFirestore.getInstance()
    private val alertsCol    = db.collection("safetyAlerts")
    private val locationsCol = db.collection("fairLocations")
    private val mapMarkersCol = db.collection("map_markers")

    // ── Seed on first launch ─────────────────────────────────────────────────

    suspend fun seedIfNeeded() {
        if (alertsCol.get().await().isEmpty) {
            val batch = db.batch()
            sampleAlerts.forEach { alert ->
                batch.set(alertsCol.document(alert.id.toString()), alert.toMap())
            }
            batch.commit().await()
        }
        if (locationsCol.get().await().isEmpty) {
            val batch = db.batch()
            sampleLocations.forEachIndexed { index, loc ->
                batch.set(locationsCol.document(index.toString()), loc.toMap())
            }
            batch.commit().await()
        }
        // Force refresh map markers to apply new map coordinates
        val mapMarkerSnap = mapMarkersCol.get().await()
        val batchMap = db.batch()
        mapMarkerSnap.documents.forEach { doc ->
            batchMap.delete(doc.reference)
        }
        sampleMapMarkers.forEach { marker ->
            val docRef = mapMarkersCol.document()
            val data = mapOf(
                "id"             to docRef.id,
                "name"           to marker.name,
                "name_kn"        to marker.nameKn,
                "type"           to marker.type,
                "description"    to marker.description,
                "description_kn" to marker.descriptionKn,
                "xPosition"      to marker.xPosition,
                "yPosition"      to marker.yPosition
            )
            batchMap.set(docRef, data)
        }
        batchMap.commit().await()
    }

    // ── Real-time Flows ──────────────────────────────────────────────────────

    fun getFairLocations(): Flow<List<FairLocation>> = callbackFlow {
        val listener = locationsCol
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) { trySend(emptyList()); return@addSnapshotListener }
                val locations = snapshot.documents.mapNotNull { doc ->
                    runCatching { FairLocation.fromMap(doc.data ?: emptyMap()) }.getOrNull()
                }
                trySend(locations)
            }
        awaitClose { listener.remove() }
    }

    fun getActiveAlerts(): Flow<List<SafetyAlert>> = callbackFlow {
        val listener = alertsCol
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) { trySend(emptyList()); return@addSnapshotListener }
                val alerts = snapshot.documents.mapNotNull { doc ->
                    runCatching { SafetyAlert.fromMap(doc.data ?: emptyMap()) }.getOrNull()
                }.sortedBy { it.priority }
                trySend(alerts)
            }
        awaitClose { listener.remove() }
    }

    fun getHighestPriorityAlert(): Flow<SafetyAlert?> =
        getActiveAlerts().map { alerts -> alerts.minByOrNull { it.priority } }

    fun getMapMarkers(): Flow<List<MapMarker>> = callbackFlow {
        val listener = mapMarkersCol
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) { trySend(emptyList()); return@addSnapshotListener }
                val markers = snapshot.documents.mapNotNull { doc ->
                    runCatching {
                        MapMarker(
                            id            = doc.id,
                            name          = doc.getString("name") ?: "",
                            nameKn        = doc.getString("name_kn") ?: "",
                            type          = doc.getString("type") ?: "",
                            description   = doc.getString("description") ?: "",
                            descriptionKn = doc.getString("description_kn") ?: "",
                            xPosition     = doc.getDouble("xPosition")?.toFloat() ?: 0f,
                            yPosition     = doc.getDouble("yPosition")?.toFloat() ?: 0f
                        )
                    }.getOrNull()
                }
                trySend(markers)
            }
        awaitClose { listener.remove() }
    }

    // ── Static safety guidelines ─────────────────────────────────────────────

    data class SafetyGuideline(val id: Int, val emoji: String, val titleEn: String, val titleKn: String)

    fun getGuidelines(): List<SafetyGuideline> = listOf(
        SafetyGuideline(1, EmojiConstants.EMERGENCY, "Emergency: Call 112", "ತುರ್ತು: 112 ಗೆ ಕರೆ ಮಾಡಿ"),
        SafetyGuideline(2, EmojiConstants.HOSPITAL,  "First Aid post near Temple Gate", "ಪ್ರಥಮ ಚಿಕಿತ್ಸೆ ದೇವಸ್ಥಾನ ಹತ್ತಿರ"),
        SafetyGuideline(3, EmojiConstants.PARKING,   "Parking zones A & B — avoid road parking", "ಪಾರ್ಕಿಂಗ್ A & B ಮಾತ್ರ"),
        SafetyGuideline(4, EmojiConstants.POLICE,    "Police helpdesk at main gate", "ಮುಖ್ಯ ದ್ವಾರದಲ್ಲಿ ಪೊಲೀಸ್ ಸಹಾಯ"),
        SafetyGuideline(5, EmojiConstants.FIRE,      "No firecrackers inside fairground", "ಮೇಳ ಆವರಣದಲ್ಲಿ ಪಟಾಕಿ ನಿಷಿದ್ಧ"),
        SafetyGuideline(6, EmojiConstants.WATER,     "Free drinking water at 3 points", "3 ಸ್ಥಳಗಳಲ್ಲಿ ಉಚಿತ ಕುಡಿಯುವ ನೀರು"),
    )

    // ── Sample seed data ─────────────────────────────────────────────────────

    private val sampleAlerts = listOf(
        SafetyAlert(
            id = 1,
            titleEn = "Crowd Alert", titleKn = "ಜನಸಂದಣಿ ಎಚ್ಚರಿಕೆ",
            messageEn = "North entrance near Zone E — Help Center is heavily crowded. Use East Gate.",
            messageKn = "ಉತ್ತರ ಪ್ರವೇಶದ್ವಾರದಲ್ಲಿ ತೀವ್ರ ಜನಸಂದಣಿ. ಪೂರ್ವ ದ್ವಾರ ಬಳಸಿ.",
            type = AlertType.CROWD, priority = 1, emoji = EmojiConstants.CROWD, timestamp = "10 min ago"
        ),
        SafetyAlert(
            id = 2,
            titleEn = "Parking Zone C Full", titleKn = "ಪಾರ್ಕಿಂಗ್ ವಲಯ C ತುಂಬಿದೆ",
            messageEn = "Parking available in Zone C — Parking is filling up quickly. Follow guidance.",
            messageKn = "ಪಾರ್ಕಿಂಗ್ ವಲಯ C ವೇಗವಾಗಿ ತುಂಬುತ್ತಿದೆ. ಸೂಚನೆಗಳನ್ನು ಪಾಲಿಸಿ.",
            type = AlertType.PARKING, priority = 2, emoji = EmojiConstants.PARKING, timestamp = "25 min ago"
        ),
        SafetyAlert(
            id = 3,
            titleEn = "Rain Expected", titleKn = "ಮಳೆ ನಿರೀಕ್ಷಿತ",
            messageEn = "Light rain expected after 6 PM. Carry umbrellas and stay near sheltered areas.",
            messageKn = "ಸಂಜೆ 6 ಗಂಟೆ ನಂತರ ಸಣ್ಣ ಮಳೆ ನಿರೀಕ್ಷಿತ. ಛತ್ರಿ ತನ್ನಿ.",
            type = AlertType.WEATHER, priority = 3, emoji = EmojiConstants.WEATHER, timestamp = "1 hr ago"
        ),
        SafetyAlert(
            id = 4,
            titleEn = "First Aid Available", titleKn = "ಪ್ರಥಮ ಚಿಕಿತ್ಸೆ ಲಭ್ಯ",
            messageEn = "First Aid station is active in Zone A — Temple Area. Medical team available 24/7.",
            messageKn = "ದೇವಸ್ಥಾನದ ಬಳಿ ಪ್ರಥಮ ಚಿಕಿತ್ಸಾ ಕೇಂದ್ರ ಸಕ್ರಿಯ. 24/7 ವೈದ್ಯಕೀಯ ತಂಡ.",
            type = AlertType.EMERGENCY, priority = 2, emoji = EmojiConstants.HOSPITAL, timestamp = "Active"
        ),
        SafetyAlert(
            id = 5,
            titleEn = "Keep Children Close", titleKn = "ಮಕ್ಕಳ ಮೇಲೆ ನಿಗಾ ಇರಲಿ",
            messageEn = "Crowded areas ahead. Use the Lost & Found desk at Zone E — Help Center.",
            messageKn = "ಜನಸಂದಣಿ ಪ್ರದೇಶಗಳಿವೆ. ಸಹಾಯ ಕೇಂದ್ರದಲ್ಲಿ ಕಳೆದ ಮತ್ತು ಸಿಕ್ಕಿತು ಕೇಂದ್ರವಿದೆ.",
            type = AlertType.GUIDANCE, priority = 3, emoji = EmojiConstants.FAMILY, timestamp = "Active"
        ),
    )

    private val sampleLocations = listOf(
        FairLocation(id = 0, nameEn = "Main Entry Gate", nameKn = "ಮುಖ್ಯ ಪ್ರವೇಶ ದ್ವಾರ", zone = "Zone E — Help Center",
            descriptionEn = "Primary entrance. Ticket counter & bag check available here.",
            descriptionKn = "ಪ್ರಮುಖ ಪ್ರವೇಶ ದ್ವಾರ. ಟಿಕೆಟ್ ಕೌಂಟರ್ ಮತ್ತು ಬ್ಯಾಗ್ ತಪಾಸಣೆ ಇಲ್ಲಿ ಲಭ್ಯ.",
            iconName = "MeetingRoom", iconColorHex = "0xFFFFC107", category = "Entry"),
        FairLocation(id = 1, nameEn = "East Gate", nameKn = "ಪೂರ್ವ ದ್ವಾರ", zone = "Zone B — Food Court",
            descriptionEn = "Alternate entry point. Use this when Main Gate is crowded.",
            descriptionKn = "ಪರ್ಯಾಯ ಪ್ರವೇಶ ದ್ವಾರ. ಮುಖ್ಯ ದ್ವಾರ ಜನಸಂದಣಿಯಾದಾಗ ಇದನ್ನು ಬಳಸಿ.",
            iconName = "MeetingRoom", iconColorHex = "0xFFFFC107", category = "Entry"),
        FairLocation(id = 2, nameEn = "Temple & Ratha Yatra", nameKn = "ದೇವಸ್ಥಾನ ಮತ್ತು ರಥ", zone = "Zone A — Temple Area",
            descriptionEn = "Main temple area. Ratha procession starts here at 5:00 PM.",
            descriptionKn = "ಮುಖ್ಯ ದೇವಸ್ಥಾನ ಪ್ರದೇಶ. ರಥೋತ್ಸವ ಸಂಜೆ 5:00 ಗಂಟೆಗೆ ಇಲ್ಲಿ ಪ್ರಾರಂಭ.",
            iconName = "Place", iconColorHex = "0xFFFF6B00", category = "Attraction"),
        FairLocation(id = 3, nameEn = "First Aid Station", nameKn = "ಪ್ರಥಮ ಚಿಕಿತ್ಸಾ ಕೇಂದ್ರ", zone = "Zone A — Temple Area",
            descriptionEn = "24/7 medical team. Free first aid, medicines, and stretcher service.",
            descriptionKn = "24/7 ವೈದ್ಯಕೀಯ ತಂಡ. ಉಚಿತ ಪ್ರಥಮ ಚಿಕಿತ್ಸೆ, ಔಷಧಗಳು ಮತ್ತು ಸ್ಟ್ರೆಚರ್ ಸೇವೆ.",
            iconName = "LocalHospital", iconColorHex = "0xFFE53935", category = "Safety"),
        FairLocation(id = 4, nameEn = "Police Help Desk", nameKn = "ಪೊಲೀಸ್ ಸಹಾಯ ಕೇಂದ್ರ", zone = "Zone E — Help Center",
            descriptionEn = "Report issues, lost children, or suspicious activity. Open 24 hours.",
            descriptionKn = "ಸಮಸ್ಯೆಗಳು, ಕಳೆದುಹೋದ ಮಕ್ಕಳು ಅಥವಾ ಅನುಮಾನಾಸ್ಪದ ಚಟುವಟಿಕೆ ವರದಿ ಮಾಡಿ. 24 ಗಂಟೆ ತೆರೆದಿರುತ್ತದೆ.",
            iconName = "PhoneInTalk", iconColorHex = "0xFF673AB7", category = "Safety"),
        FairLocation(id = 5, nameEn = "Parking Zone A (2-Wheelers)", nameKn = "ಪಾರ್ಕಿಂಗ್ A (ದ್ವಿಚಕ್ರ)", zone = "Zone C — Parking",
            descriptionEn = "Dedicated 2-wheeler parking. 200+ spots. ₹20 per vehicle.",
            descriptionKn = "ದ್ವಿಚಕ್ರ ವಾಹನ ನಿಲ್ದಾಣ. 200+ ಸ್ಥಳಗಳು. ₹20 ಪ್ರತಿ ವಾಹನಕ್ಕೆ.",
            iconName = "DirectionsCar", iconColorHex = "0xFF009688", category = "Parking"),
        FairLocation(id = 6, nameEn = "Parking Zone B (Cars & Autos)", nameKn = "ಪಾರ್ಕಿಂಗ್ B (ಕಾರು)", zone = "Zone C — Parking",
            descriptionEn = "4-wheeler parking with security guard. ₹50 per vehicle.",
            descriptionKn = "ಭದ್ರತಾ ಕಾವಲುಗಾರರೊಂದಿಗೆ 4-ಚಕ್ರ ವಾಹನ ನಿಲ್ದಾಣ. ₹50 ಪ್ರತಿ ವಾಹನಕ್ಕೆ.",
            iconName = "DirectionsCar", iconColorHex = "0xFF009688", category = "Parking"),
        FairLocation(id = 7, nameEn = "Food Court & Stalls", nameKn = "ಆಹಾರ ಅಂಗಡಿಗಳು", zone = "Zone B — Food Court",
            descriptionEn = "30+ food stalls. Local cuisine, sweets, snacks, and beverages.",
            descriptionKn = "30+ ಆಹಾರ ಮಳಿಗೆಗಳು. ಸ್ಥಳೀಯ ಅಡುಗೆ, ಸಿಹಿತಿಂಡಿ, ತಿನಿಸುಗಳು ಮತ್ತು ಪಾನೀಯಗಳು.",
            iconName = "Fastfood", iconColorHex = "0xFFFF9800", category = "Amenities"),
        FairLocation(id = 8, nameEn = "Drinking Water Points", nameKn = "ಕುಡಿಯುವ ನೀರು", zone = "Zone D — Main Stage",
            descriptionEn = "Free RO water at 3 locations. Look for blue taps near each zone entrance.",
            descriptionKn = "3 ಸ್ಥಳಗಳಲ್ಲಿ ಉಚಿತ RO ನೀರು. ಪ್ರತಿ ವಲಯ ಪ್ರವೇಶದ್ವಾರದ ಬಳಿ ನೀಲಿ ನಲ್ಲಿಗಳನ್ನು ನೋಡಿ.",
            iconName = "WaterDrop", iconColorHex = "0xFF2196F3", category = "Amenities"),
    )

    private val sampleMapMarkers = listOf(
        MapMarker(name = "Parking Zone A",      nameKn = "ಪಾರ್ಕಿಂಗ್ ವಲಯ A",
            type = "parking",
            description = "Near West Entrance\n200+ Vehicles", descriptionKn = "ಪಶ್ಚಿಮ ದ್ವಾರದ ಬಳಿ\n200+ ವಾಹನಗಳು",
            xPosition = 0.15f, yPosition = 0.5f),
        MapMarker(name = "Main Stage",          nameKn = "ಮುಖ್ಯ ವೇದಿಕೆ",
            type = "stage",
            description = "Cultural Center\nLive Performances", descriptionKn = "ಸಾಂಸ್ಕೃತಿಕ ಕೇಂದ್ರ\nನೇರ ಪ್ರದರ್ಶನಗಳು",
            xPosition = 0.5f, yPosition = 0.65f),
        MapMarker(name = "Kadri Manjunatha Temple", nameKn = "ಕದ್ರಿ ಮಂಜುನಾಥ ದೇವಸ್ಥಾನ",
            type = "temple",
            description = "Main Darshan Queue\nRatha Yatra Start", descriptionKn = "ಮುಖ್ಯ ದರ್ಶನ ಸಾಲು\nರಥೋತ್ಸವ ಪ್ರಾರಂಭ",
            xPosition = 0.75f, yPosition = 0.25f),
        MapMarker(name = "Food Court",          nameKn = "ಆಹಾರ ಮಳಿಗೆಗಳು",
            type = "stall",
            description = "East Side Area\n30+ Food Stalls", descriptionKn = "ಪೂರ್ವ ಬದಿಯ ಪ್ರದೇಶ\n30+ ಮಳಿಗೆಗಳು",
            xPosition = 0.85f, yPosition = 0.5f),
        MapMarker(name = "First Aid Post",      nameKn = "ಪ್ರಥಮ ಚಿಕಿತ್ಸಾ ಕೇಂದ್ರ",
            type = "firstAid",
            description = "Near Temple Gate\n24/7 Medical Staff", descriptionKn = "ದೇವಸ್ಥಾನದ ಗೇಟ್ ಬಳಿ\n24/7 ವೈದ್ಯಕೀಯ ಸಿಬ್ಬಂದಿ",
            xPosition = 0.70f, yPosition = 0.35f),
        MapMarker(name = "Police Help Desk",    nameKn = "ಪೊಲೀಸ್ ಸಹಾಯ ಕೇಂದ್ರ",
            type = "helpDesk",
            description = "Main Gate Booth\nLost & Found Guidance", descriptionKn = "ಮುಖ್ಯ ದ್ವಾರದ ಬೂತ್\nಮಾಹಿತಿ ಮತ್ತು ಭದ್ರತೆ",
            xPosition = 0.8f, yPosition = 0.85f),
        MapMarker(name = "Main Entrance",       nameKn = "ಮುಖ್ಯ ಪ್ರವೇಶ ದ್ವಾರ",
            type = "entry",
            description = "South Gate\nTicket & Security Check", descriptionKn = "ದಕ್ಷಿಣ ದ್ವಾರ\nಟಿಕೆಟ್ ಮತ್ತು ಭದ್ರತಾ ತಪಾಸಣೆ",
            xPosition = 0.5f, yPosition = 0.92f)
    )
}
