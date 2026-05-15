package com.example.jatre_namma_pride.data.model

/**
 * Represents a Jatre event. Stored in Firestore `events` collection.
 * Status is computed client-side from the device clock; it is not persisted.
 *
 * Bilingual fields use _en / _kn suffix convention in Firestore documents.
 * Kotlin properties use camelCase: nameEn, nameKn, locationEn, locationKn.
 */
data class JatreEvent(
    val id: Int = 0,
    val time: String = "",
    val endTime: String = "",
    val nameEn: String = "",
    val nameKn: String = "",
    val descriptionEn: String = "",
    val descriptionKn: String = "",
    val locationEn: String = "",
    val locationKn: String = "",
    val category: EventCategory = EventCategory.RELIGIOUS,
    val status: EventStatus = EventStatus.UPCOMING,
    val day: Int = 1,
    val date: String = "",
    val isFavorite: Boolean = false
) {
    /** Converts to a plain map for Firestore writes. Uses snake_case keys. */
    fun toMap(): Map<String, Any> = mapOf(
        "id"          to id,
        "time"        to time,
        "endTime"     to endTime,
        "name_en"     to nameEn,
        "name_kn"     to nameKn,
        "description_en" to descriptionEn,
        "description_kn" to descriptionKn,
        "location_en" to locationEn,
        "location_kn" to locationKn,
        "category"    to category.name,
        "status"      to status.name,
        "day"         to day,
        "date"        to date,
        "isFavorite"  to isFavorite
    )

    companion object {
        /** Creates a JatreEvent from a Firestore document map.
         *  Falls back to legacy field names (name, nameKannada, location) for
         *  backward compatibility with documents seeded before the _en/_kn migration. */
        fun fromMap(map: Map<String, Any>): JatreEvent = JatreEvent(
            id          = (map["id"] as? Long)?.toInt() ?: 0,
            time        = map["time"] as? String ?: "",
            endTime     = map["endTime"] as? String ?: "",
            nameEn      = map["name_en"] as? String
                            ?: map["name"] as? String ?: "",
            nameKn      = map["name_kn"] as? String
                            ?: map["nameKannada"] as? String ?: "",
            descriptionEn = map["description_en"] as? String
                            ?: map["description"] as? String ?: "",
            descriptionKn = map["description_kn"] as? String ?: "",
            locationEn  = map["location_en"] as? String
                            ?: map["location"] as? String ?: "",
            locationKn  = map["location_kn"] as? String ?: "",
            category    = runCatching { EventCategory.valueOf(map["category"] as? String ?: "") }
                              .getOrDefault(EventCategory.RELIGIOUS),
            status      = runCatching { EventStatus.valueOf(map["status"] as? String ?: "") }
                              .getOrDefault(EventStatus.UPCOMING),
            day         = (map["day"] as? Long)?.toInt() ?: 1,
            date        = map["date"] as? String ?: "",
            isFavorite  = map["isFavorite"] as? Boolean ?: false
        )
    }
}

enum class EventStatus { PAST, LIVE, UPCOMING }

enum class EventCategory {
    RELIGIOUS,
    CULTURAL,
    SPORTS,
    TRADE,
    ALL
}

/**
 * TypeConverters kept for AppNotification Room entity (which still uses Room).
 */
class Converters {
    @androidx.room.TypeConverter
    fun fromItemType(value: ItemType): String = value.name

    @androidx.room.TypeConverter
    fun toItemType(value: String): ItemType = enumValueOf(value)

    @androidx.room.TypeConverter
    fun fromAlertType(value: AlertType): String = value.name

    @androidx.room.TypeConverter
    fun toAlertType(value: String): AlertType = enumValueOf(value)
}
