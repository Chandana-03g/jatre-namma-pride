package com.example.jatre_namma_pride

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.Surface
import com.example.jatre_namma_pride.navigation.AppNavigation
import com.example.jatre_namma_pride.ui.theme.JatreNammaPrideTheme
import com.example.jatre_namma_pride.ui.theme.JatreSurface
import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.jatre_namma_pride.data.worker.NotificationScheduler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.jatre_namma_pride.data.PreferencesManager
import com.example.jatre_namma_pride.data.repository.EventRepository
import com.example.jatre_namma_pride.data.repository.LostFoundRepository
import com.example.jatre_namma_pride.data.repository.SafetyRepository
import java.util.Locale
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted.
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferencesManager.init(this)
        
        lifecycleScope.launch {
            EventRepository.seedIfNeeded()
            LostFoundRepository.seedIfNeeded()
            SafetyRepository.seedIfNeeded()
        }

        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        lifecycleScope.launch {
            EventRepository.getAllEvents().collectLatest { events ->
                if (PreferencesManager.isNotificationsEnabled()) {
                    NotificationScheduler.scheduleEventReminders(this@MainActivity, events)
                }
            }
        }
        
        // Apply saved language on startup
        val langCode = PreferencesManager.language.value
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocales(android.os.LocaleList(locale))
        resources.updateConfiguration(config, resources.displayMetrics)

        enableEdgeToEdge()
        setContent {
            JatreNammaPrideTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = JatreSurface
                ) {
                    AppNavigation()
                }
            }
        }
    }
}