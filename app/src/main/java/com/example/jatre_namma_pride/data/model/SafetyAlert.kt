package com.example.jatre_namma_pride.data.model

/**
 * Represents a live safety alert. Stored in Firestore `safetyAlerts` collection.
 * Admin can update alerts from the Firebase console and all users receive them instantly.
 *
 * Bilingual fields follow the _en / _kn suffix convention in Firestore documents.
 */
data class SafetyAlert(
    val id: Int = 0,
    val titleEn: String = "",
    val titleKn: String = "",
    val messageEn: String = "",
    val messageKn: String = "",
    val type: AlertType = AlertType.GUIDANCE,
    val priority: Int = 3,
    val isActive: Boolean = true,
    val emoji: String = "⚠️",
    val timestamp: String = ""
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id"         to id,
        "title_en"   to titleEn,
        "title_kn"   to titleKn,
        "message_en" to messageEn,
        "message_kn" to messageKn,
        "type"       to type.name,
        "priority"   to priority,
        "isActive"   to isActive,
        "emoji"      to emoji,
        "timestamp"  to timestamp
    )

    companion object {
        fun fromMap(map: Map<String, Any>): SafetyAlert = SafetyAlert(
            id        = (map["id"] as? Long)?.toInt() ?: 0,
            titleEn   = map["title_en"] as? String
                          ?: map["title"] as? String ?: "",
            titleKn   = map["title_kn"] as? String
                          ?: map["titleKannada"] as? String ?: "",
            messageEn = map["message_en"] as? String
                          ?: map["message"] as? String ?: "",
            messageKn = map["message_kn"] as? String
                          ?: map["messageKannada"] as? String ?: "",
            type      = runCatching { AlertType.valueOf(map["type"] as? String ?: "") }
                            .getOrDefault(AlertType.GUIDANCE),
            priority  = (map["priority"] as? Long)?.toInt() ?: 3,
            isActive  = map["isActive"] as? Boolean ?: true,
            emoji     = map["emoji"] as? String ?: "⚠️",
            timestamp = map["timestamp"] as? String ?: ""
        )
    }
}

enum class AlertType {
    EMERGENCY,
    CROWD,
    PARKING,
    WEATHER,
    GUIDANCE
}
