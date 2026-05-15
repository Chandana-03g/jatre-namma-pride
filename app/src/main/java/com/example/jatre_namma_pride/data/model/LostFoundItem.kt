package com.example.jatre_namma_pride.data.model

/**
 * Represents a lost or found item report. Stored in Firestore `lostFound` collection.
 *
 * Bilingual fields follow the _en / _kn suffix convention in Firestore documents.
 * User-submitted items will have titleKn / descriptionKn / locationKn left blank,
 * which causes the app to fall back to the English value automatically.
 */
data class LostFoundItem(
    val id: Int = 0,
    val titleEn: String = "",
    val titleKn: String = "",
    val descriptionEn: String = "",
    val descriptionKn: String = "",
    val category: String = "",
    val locationEn: String = "",
    val locationKn: String = "",
    val reportedBy: String = "",
    val contactNumber: String = "",
    val time: String = "",
    val type: ItemType = ItemType.LOST,
    val imageResId: Int = 0,   // local drawable for seeded items
    val imageUri: String? = null,
    val isResolved: Boolean = false
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id"             to id,
        "title_en"       to titleEn,
        "title_kn"       to titleKn,
        "description_en" to descriptionEn,
        "description_kn" to descriptionKn,
        "category"       to category,
        "location_en"    to locationEn,
        "location_kn"    to locationKn,
        "reportedBy"     to reportedBy,
        "contactNumber"  to contactNumber,
        "time"           to time,
        "type"           to type.name,
        "imageResId"     to imageResId,
        "imageUri"       to imageUri,
        "isResolved"     to isResolved
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): LostFoundItem = LostFoundItem(
            id            = (map["id"] as? Long)?.toInt() ?: 0,
            titleEn       = map["title_en"] as? String
                              ?: map["title"] as? String ?: "",
            titleKn       = map["title_kn"] as? String
                              ?: map["titleKannada"] as? String ?: "",
            descriptionEn = map["description_en"] as? String
                              ?: map["description"] as? String ?: "",
            descriptionKn = map["description_kn"] as? String ?: "",
            category      = map["category"] as? String ?: "",
            locationEn    = map["location_en"] as? String
                              ?: map["location"] as? String ?: "",
            locationKn    = map["location_kn"] as? String ?: "",
            reportedBy    = map["reportedBy"] as? String ?: "",
            contactNumber = map["contactNumber"] as? String ?: "",
            time          = map["time"] as? String ?: "",
            type          = runCatching { ItemType.valueOf(map["type"] as? String ?: "") }
                                .getOrDefault(ItemType.LOST),
            imageResId    = (map["imageResId"] as? Long)?.toInt() ?: 0,
            imageUri      = map["imageUri"] as? String,
            isResolved    = map["isResolved"] as? Boolean ?: false
        )
    }
}

enum class ItemType { LOST, FOUND }
