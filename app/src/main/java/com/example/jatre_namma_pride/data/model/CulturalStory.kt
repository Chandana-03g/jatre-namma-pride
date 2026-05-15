package com.example.jatre_namma_pride.data.model

/**
 * Represents a cultural story card.
 * Text content is stored in Firestore `culturalStories` collection.
 * Images remain as local drawables (imageResId) bundled with the app.
 */
data class CulturalStory(
    val id: Int = 0,
    val titleEn: String = "",
    val titleKn: String = "",
    val categoryEn: String = "",
    val categoryKn: String = "",
    val readTimeEn: String = "",
    val readTimeKn: String = "",
    val summaryEn: String = "",
    val summaryKn: String = "",
    val fullContentEn: String = "",
    val fullContentKn: String = "",
    val emoji: String = "",
    val imageResId: Int = 0  // local drawable resource id
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id"              to id,
        "title_en"        to titleEn,
        "title_kn"        to titleKn,
        "category_en"     to categoryEn,
        "category_kn"     to categoryKn,
        "readTime_en"     to readTimeEn,
        "readTime_kn"     to readTimeKn,
        "summary_en"      to summaryEn,
        "summary_kn"      to summaryKn,
        "fullContent_en"  to fullContentEn,
        "fullContent_kn"  to fullContentKn,
        "emoji"           to emoji,
        "imageResId"      to imageResId
    )

    companion object {
        fun fromMap(map: Map<String, Any>): CulturalStory = CulturalStory(
            id             = (map["id"] as? Long)?.toInt() ?: 0,
            titleEn        = map["title_en"] as? String
                               ?: map["title"] as? String ?: "",
            titleKn        = map["title_kn"] as? String ?: "",
            categoryEn     = map["category_en"] as? String
                               ?: map["category"] as? String ?: "",
            categoryKn     = map["category_kn"] as? String ?: "",
            readTimeEn     = map["readTime_en"] as? String
                               ?: map["readTime"] as? String ?: "",
            readTimeKn     = map["readTime_kn"] as? String ?: "",
            summaryEn      = map["summary_en"] as? String
                               ?: map["summary"] as? String ?: "",
            summaryKn      = map["summary_kn"] as? String ?: "",
            fullContentEn  = map["fullContent_en"] as? String
                               ?: map["fullContent"] as? String ?: "",
            fullContentKn  = map["fullContent_kn"] as? String ?: "",
            emoji          = map["emoji"] as? String ?: "",
            imageResId     = (map["imageResId"] as? Long)?.toInt() ?: 0
        )
    }
}
