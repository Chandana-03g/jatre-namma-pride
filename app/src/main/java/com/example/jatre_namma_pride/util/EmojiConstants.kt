package com.example.jatre_namma_pride.util

/**
 * Centralised emoji constants for the entire application.
 *
 * Standard practice: Always use Unicode escape sequences (\uXXXX or surrogate pairs)
 * rather than raw emoji characters embedded in source files.  This avoids encoding
 * issues across editors, CI systems, and different OS locales.
 *
 * Surrogate pair reference:
 *   Code-point above U+FFFF needs TWO Kotlin Char values (\uD83X\uDXXX).
 */
object EmojiConstants {

    // ── Emergency / Safety ────────────────────────────────────────────────────
    /** 🚨 Emergency siren */
    const val EMERGENCY     = "\uD83D\uDEA8"
    /** 🚑 Ambulance */
    const val AMBULANCE     = "\uD83D\uDE91"
    /** 👮 Police officer */
    const val POLICE        = "\uD83D\uDC6E"
    /** 🔥 Fire */
    const val FIRE          = "\uD83D\uDD25"
    /** 🏥 Hospital */
    const val HOSPITAL      = "\uD83C\uDFE5"
    /** 👩 Woman (women helpline) */
    const val WOMAN         = "\uD83D\uDC69"
    /** 🧒 Child */
    const val CHILD         = "\uD83E\uDDD2"

    // ── Navigation / Map ──────────────────────────────────────────────────────
    /** 🅿️ Parking */
    const val PARKING       = "\uD83C\uDD7F\uFE0F"
    /** ⛑️ First-aid helmet */
    const val FIRST_AID     = "\u26D1\uFE0F"
    /** 🍔 Food / burger */
    const val FOOD          = "\uD83C\uDF54"
    /** ℹ️ Information */
    const val INFO          = "\u2139\uFE0F"
    /** 📍 Pin / location */
    const val PIN           = "\uD83D\uDCCD"

    // ── Alerts ────────────────────────────────────────────────────────────────
    /** 🚧 Construction / crowd */
    const val CROWD         = "\uD83D\uDEA7"
    /** 🌧️ Rain / weather */
    const val WEATHER       = "\uD83C\uDF27\uFE0F"
    /** 👨‍👧‍👦 Family / guidance */
    const val FAMILY        = "\uD83D\uDC68\u200D\uD83D\uDC67\u200D\uD83D\uDC66"

    // ── Cultural Stories ─────────────────────────────────────────────────────
    /** 🛕 Hindu temple */
    const val TEMPLE        = "\uD83D\uDED5"
    /** 💧 Water droplet */
    const val WATER         = "\uD83D\uDCA7"
    /** 🎭 Performing arts / Yakshagana */
    const val PERFORMING    = "\uD83C\uDFAD"
    /** 🙏 Folded hands / prayer */
    const val PRAYER        = "\uD83D\uDE4F"
    /** 🥁 Drum */
    const val DRUM          = "\uD83E\uDD41"

    // ── Lost & Found ─────────────────────────────────────────────────────────
    /** 👤 Person silhouette */
    const val PERSON        = "\uD83D\uDC64"
    /** 💍 Ring / Jewellery */
    const val JEWELLERY     = "\uD83D\uDC8D"
    /** 👜 Handbag */
    const val BAG           = "\uD83D\uDC5C"
    /** 📦 Package / Box */
    const val PACKAGE       = "\uD83D\uDCE6"

    // ── Settings ─────────────────────────────────────────────────────────────
    /** 🛕 Temple help desk (reuses TEMPLE) */
    val HELP_DESK get()     = TEMPLE
}
