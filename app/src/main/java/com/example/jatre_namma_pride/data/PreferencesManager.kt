package com.example.jatre_namma_pride.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Manages user preferences using SharedPreferences.
 * Provides reactive StateFlows so the UI updates instantly when a setting changes.
 */
object PreferencesManager {

    private const val PREFS_NAME = "jatre_preferences"
    private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    private const val KEY_LANGUAGE = "language"

    private lateinit var prefs: SharedPreferences

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    private val _language = MutableStateFlow("en")
    val language: StateFlow<String> = _language

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _notificationsEnabled.value = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        _language.value = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun setLanguage(langCode: String) {
        _language.value = langCode
        prefs.edit().putString(KEY_LANGUAGE, langCode).apply()
    }

    fun isNotificationsEnabled(): Boolean = _notificationsEnabled.value
}
