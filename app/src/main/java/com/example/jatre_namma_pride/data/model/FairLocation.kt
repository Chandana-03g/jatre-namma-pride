package com.example.jatre_namma_pride.data.model

/**
 * Represents a physical location within the Jatre fair grounds.
 * Stored in Firestore `fairLocations` collection.
 */
data class FairLocation(
    val id: Int = 0,
    val nameEn: String = "",
    val nameKn: String = "",
    val zone: String = "",
    val descriptionEn: String = "",
    val descriptionKn: String = "",
    val iconName: String = "",
    val iconColorHex: String = "",
    val category: String = ""
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id"              to id,
        "name_en"         to nameEn,
        "name_kn"         to nameKn,
        "zone"            to zone,
        "description_en"  to descriptionEn,
        "description_kn"  to descriptionKn,
        "iconName"        to iconName,
        "iconColorHex"    to iconColorHex,
        "category"        to category
    )

    companion object {
        fun fromMap(map: Map<String, Any>): FairLocation = FairLocation(
            id             = (map["id"] as? Long)?.toInt() ?: 0,
            nameEn         = map["name_en"] as? String
                               ?: map["name"] as? String ?: "",
            nameKn         = map["name_kn"] as? String
                               ?: map["nameKn"] as? String ?: "",
            zone           = map["zone"] as? String ?: "",
            descriptionEn  = map["description_en"] as? String
                               ?: map["description"] as? String ?: "",
            descriptionKn  = map["description_kn"] as? String ?: "",
            iconName       = map["iconName"] as? String ?: "",
            iconColorHex   = map["iconColorHex"] as? String ?: "",
            category       = map["category"] as? String ?: ""
        )
    }
}
