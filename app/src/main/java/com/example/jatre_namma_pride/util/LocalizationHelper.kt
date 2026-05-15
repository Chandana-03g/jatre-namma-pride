package com.example.jatre_namma_pride.util

import android.content.res.Configuration

/**
 * Centralized localization helper for the Jatre-Namma Pride app.
 *
 * Use [resolve] to pick the correct language value instead of
 * scattering `if (locale == "kn")` checks across UI components.
 * All composables should obtain config via [LocalConfiguration.current].
 */
object LocalizationHelper {

    /**
     * Returns true when the active configuration language is Kannada (kn).
     */
    fun isKannada(config: Configuration): Boolean =
        config.locales[0].language == "kn"

    /**
     * Returns [knValue] when the locale is Kannada and [knValue] is non-blank;
     * otherwise returns [enValue].
     *
     * @param enValue  English (default) display value
     * @param knValue  Kannada display value from Firebase or seed data
     * @param config   Current [Configuration] from LocalConfiguration.current
     */
    fun resolve(enValue: String, knValue: String, config: Configuration): String =
        if (isKannada(config) && knValue.isNotBlank()) knValue else enValue
}
