package com.example.jatre_namma_pride.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.LocaleList
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jatre_namma_pride.data.PreferencesManager
import com.example.jatre_namma_pride.ui.theme.JatreCardBg
import com.example.jatre_namma_pride.ui.theme.JatreCream
import com.example.jatre_namma_pride.ui.theme.JatreDarkBrown
import com.example.jatre_namma_pride.ui.theme.JatreDivider
import com.example.jatre_namma_pride.ui.theme.JatreGold
import com.example.jatre_namma_pride.ui.theme.JatreLightGold
import com.example.jatre_namma_pride.ui.theme.JatreLiveGreen
import com.example.jatre_namma_pride.ui.theme.JatreSaffron
import com.example.jatre_namma_pride.ui.theme.JatreSurface
import androidx.compose.ui.res.stringResource
import com.example.jatre_namma_pride.R
import com.example.jatre_namma_pride.util.EmojiConstants
import java.util.Locale

/**
 * Updates the app's locale and restarts the activity to apply changes.
 */
private fun updateLocale(activity: Activity, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = activity.resources.configuration
    config.setLocales(LocaleList(locale))
    activity.resources.updateConfiguration(config, activity.resources.displayMetrics)
    // Restart the activity to apply the new locale everywhere
    val intent = activity.intent
    activity.finish()
    activity.startActivity(intent)
}

/**
 * Settings screen with Language, Notification, and Emergency Contacts sections.
 * All preferences are persisted via PreferencesManager and take effect immediately.
 */
@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val notificationsEnabled by PreferencesManager.notificationsEnabled.collectAsState()
    val currentLanguage by PreferencesManager.language.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JatreSurface)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top Bar ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(JatreDarkBrown)
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = JatreCream
                )
            }
            Column {
                Text(
                    text = stringResource(R.string.settings),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = JatreCream,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Text(
                    text = stringResource(R.string.preferences),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = JatreGold.copy(alpha = 0.7f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── 1. Language ──────────────────────────────────────────────────────
        SettingsSectionHeader(icon = Icons.Outlined.Language, title = stringResource(R.string.language_title))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = JatreCardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
                LanguageOption(
                    label = "English",
                    sublabel = stringResource(R.string.language_default),
                    isSelected = currentLanguage == "en",
                    onClick = {
                        PreferencesManager.setLanguage("en")
                        updateLocale(context as Activity, "en")
                    }
                )
                HorizontalDivider(color = JatreDivider.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                LanguageOption(
                    label = "ಕನ್ನಡ",
                    sublabel = stringResource(R.string.kannada),
                    isSelected = currentLanguage == "kn",
                    onClick = {
                        PreferencesManager.setLanguage("kn")
                        updateLocale(context as Activity, "kn")
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── 2. Notifications ─────────────────────────────────────────────────
        SettingsSectionHeader(icon = Icons.Outlined.Notifications, title = stringResource(R.string.notifications_title))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = JatreCardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.event_reminders),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = JatreCream,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = if (notificationsEnabled) stringResource(R.string.reminders_enabled_desc)
                               else stringResource(R.string.reminders_disabled_desc),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = JatreLightGold.copy(alpha = 0.6f)
                        )
                    )
                }
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { PreferencesManager.setNotificationsEnabled(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = JatreSaffron,
                        checkedTrackColor = JatreSaffron.copy(alpha = 0.3f),
                        uncheckedThumbColor = JatreDivider,
                        uncheckedTrackColor = JatreDivider.copy(alpha = 0.2f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── 3. Emergency Contacts ────────────────────────────────────────────
        SettingsSectionHeader(icon = Icons.Outlined.Call, title = stringResource(R.string.emergency_contacts))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = JatreCardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
                EmergencyContactRow(
                    emoji = EmojiConstants.POLICE,
                    label = stringResource(R.string.police),
                    number = "100",
                    onCall = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:100"))
                        context.startActivity(intent)
                    }
                )
                HorizontalDivider(color = JatreDivider.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                EmergencyContactRow(
                    emoji = EmojiConstants.AMBULANCE,
                    label = stringResource(R.string.ambulance),
                    number = "108",
                    onCall = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:108"))
                        context.startActivity(intent)
                    }
                )
                HorizontalDivider(color = JatreDivider.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                EmergencyContactRow(
                    emoji = EmojiConstants.FIRE,
                    label = stringResource(R.string.fire_station),
                    number = "101",
                    onCall = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:101"))
                        context.startActivity(intent)
                    }
                )
                HorizontalDivider(color = JatreDivider.copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 16.dp))
                EmergencyContactRow(
                    emoji = EmojiConstants.HELP_DESK,
                    label = stringResource(R.string.jatre_help_desk),
                    number = "9876543210",
                    onCall = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:9876543210"))
                        context.startActivity(intent)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── App Info ─────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_name_display),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = JatreLightGold.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = stringResource(R.string.app_version_info),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = JatreLightGold.copy(alpha = 0.3f)
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ── Section Header ───────────────────────────────────────────────────────────
@Composable
private fun SettingsSectionHeader(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = JatreSaffron,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                color = JatreSaffron,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )
        )
    }
}

// ── Language Option Row ──────────────────────────────────────────────────────
@Composable
private fun LanguageOption(
    label: String,
    sublabel: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = if (isSelected) JatreSaffron else JatreCream,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal
                )
            )
            Text(
                text = sublabel,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = JatreLightGold.copy(alpha = 0.5f)
                )
            )
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(50))
                    .background(JatreSaffron),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = JatreDarkBrown,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(50))
                    .background(JatreDivider.copy(alpha = 0.3f))
            )
        }
    }
}

// ── Emergency Contact Row ────────────────────────────────────────────────────
@Composable
private fun EmergencyContactRow(
    emoji: String,
    label: String,
    number: String,
    onCall: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCall() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = JatreCream,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        // Tappable phone number
        Text(
            text = number,
            style = MaterialTheme.typography.titleSmall.copy(
                color = JatreLiveGreen,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            imageVector = Icons.Outlined.Call,
            contentDescription = "Call $label",
            tint = JatreLiveGreen,
            modifier = Modifier.size(18.dp)
        )
    }
}
