package com.example.jatre_namma_pride.data.model

/**
 * Represents an interactive marker on the custom Jatre map layout.
 * Directly maps to Firestore documents in the 'map_markers' collection.
 *
 * nameKn / descriptionKn are the Kannada display values.
 * The UI resolves the correct value via LocalizationHelper.resolve().
 */
data class MapMarker(
    val id: String = "",
    val name: String = "",          // English name (name_en in Firestore)
    val nameKn: String = "",        // Kannada name  (name_kn in Firestore)
    val type: String = "",          // e.g. parking, stall, firstAid, temple, stage
    val description: String = "",   // English description
    val descriptionKn: String = "", // Kannada description
    val xPosition: Float = 0f,
    val yPosition: Float = 0f
)
