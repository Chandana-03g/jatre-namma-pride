package com.example.jatre_namma_pride.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jatre_namma_pride.R
import com.example.jatre_namma_pride.data.repository.EventRepository
import com.example.jatre_namma_pride.ui.components.CulturalStoryCard
import com.example.jatre_namma_pride.ui.components.JatreTopBar
import com.example.jatre_namma_pride.ui.components.LiveBadge
import com.example.jatre_namma_pride.ui.components.QuickAccessCard
import com.example.jatre_namma_pride.ui.components.SafetyReminderBanner
import com.example.jatre_namma_pride.ui.components.SectionHeader
import com.example.jatre_namma_pride.ui.theme.JatreCardBg
import com.example.jatre_namma_pride.ui.theme.JatreCream
import com.example.jatre_namma_pride.ui.theme.JatreDarkBrown
import com.example.jatre_namma_pride.ui.theme.JatreDarkGreen
import com.example.jatre_namma_pride.ui.theme.JatreGold
import com.example.jatre_namma_pride.ui.theme.JatreLightGold
import com.example.jatre_namma_pride.ui.theme.JatreLiveGreen
import com.example.jatre_namma_pride.ui.theme.JatreMaroon
import com.example.jatre_namma_pride.ui.theme.JatreSaffron
import com.example.jatre_namma_pride.ui.theme.JatreSurface
import com.example.jatre_namma_pride.ui.theme.JatreTeal
import com.example.jatre_namma_pride.util.LocalizationHelper

/**
 * Home screen matching the premium UI design.
 * Uses reusable components from ui/components/.
 */
@Composable
fun HomeScreen(
    onGoToSchedule: () -> Unit,
    onGoToLostFound: () -> Unit,
    onGoToSafety: () -> Unit,
    onGoToNotifications: () -> Unit,
    onGoToStories: () -> Unit = {},
    onGoToStoryDetails: (Int) -> Unit = {},
    onGoToSettings: () -> Unit = {}
) {
    val liveEvents by EventRepository.getLiveEvents().collectAsState(initial = emptyList())
    val nextEvent by EventRepository.getNextEvent().collectAsState(initial = null)
    val stories by EventRepository.getCulturalStories().collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(JatreSurface),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Top Bar ──────────────────────────────────────────────────────────
        item { JatreTopBar(onNotificationClick = onGoToNotifications, onSettingsClick = onGoToSettings) }

        // ── Greeting ─────────────────────────────────────────────────────────
        item {
            Text(
                text = stringResource(R.string.welcome_jatre),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = JatreSaffron,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        // ── Hero Banner Card ─────────────────────────────────────────────────
        item { HeroBannerCard(onViewSchedule = onGoToSchedule) }

        // ── Happening Now / Next Event ───────────────────────────────────────
        item {
            if (liveEvents.isNotEmpty()) {
                SectionHeader(title = stringResource(R.string.happening_now))
                val event = liveEvents.first()
                val config = LocalConfiguration.current
                HappeningNowCard(
                    eventName = LocalizationHelper.resolve(event.nameEn, event.nameKn, config),
                    time = "${event.time} – ${event.endTime}",
                    location = LocalizationHelper.resolve(event.locationEn, event.locationKn, config),
                    isLive = true
                )
            } else if (nextEvent != null) {
                SectionHeader(title = stringResource(R.string.next_event))
                val event = nextEvent!!
                val config = LocalConfiguration.current
                HappeningNowCard(
                    eventName = LocalizationHelper.resolve(event.nameEn, event.nameKn, config),
                    time = "${event.time} – ${event.endTime}",
                    location = LocalizationHelper.resolve(event.locationEn, event.locationKn, config),
                    isLive = false
                )
            }
        }

        // ── Quick Access Grid ────────────────────────────────────────────────
        item { SectionHeader(title = stringResource(R.string.quick_access)) }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAccessCard(
                    icon = Icons.Filled.CalendarMonth,
                    label = stringResource(R.string.live_schedule),
                    statText = stringResource(R.string.events_today),
                    gradientColors = listOf(JatreMaroon, Color(0xFF4A1500)),
                    onClick = onGoToSchedule,
                    modifier = Modifier.weight(1f)
                )
                QuickAccessCard(
                    icon = Icons.Filled.Map,
                    label = stringResource(R.string.fair_map),
                    statText = stringResource(R.string.zones_8),
                    gradientColors = listOf(JatreTeal, JatreDarkGreen),
                    onClick = onGoToSafety,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAccessCard(
                    icon = Icons.Filled.Search,
                    label = stringResource(R.string.lost_found),
                    statText = stringResource(R.string.active_3),
                    gradientColors = listOf(Color(0xFF5C3317), Color(0xFF3E1C00)),
                    onClick = onGoToLostFound,
                    modifier = Modifier.weight(1f)
                )
                QuickAccessCard(
                    icon = Icons.Filled.Shield,
                    label = stringResource(R.string.safety_parking),
                    statText = stringResource(R.string.zones_2),
                    gradientColors = listOf(JatreDarkGreen, Color(0xFF0D2B1A)),
                    onClick = onGoToSafety,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── Cultural Stories ─────────────────────────────────────────────────
        item {
            SectionHeader(
                title = stringResource(R.string.cultural_stories),
                onSeeAllClick = { onGoToStories() }
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(stories) { story ->
                    CulturalStoryCard(
                        story = story,
                        onClick = { onGoToStoryDetails(story.id) }
                    )
                }
            }
        }

        // ── Safety Reminder ──────────────────────────────────────────────────
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item { SafetyReminderBanner() }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Private Sub-Composables (specific to HomeScreen)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HeroBannerCard(onViewSchedule: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            // Hero background image
            Image(
                painter = painterResource(id = R.drawable.img_hero_jatre_banner),
                contentDescription = "Jatre Festival",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Gradient scrim overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                JatreMaroon.copy(alpha = 0.7f),
                                JatreDarkBrown.copy(alpha = 0.85f)
                            )
                        )
                    )
            )
            // Decorative background pattern
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row: Live badge + Day indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LiveBadge()
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(JatreDarkBrown.copy(alpha = 0.6f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.day_2_of_3),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JatreCream,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // Event info
                Column {
                    Text(
                        text = stringResource(R.string.yellamma_jatre),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = JatreCream,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(
                        text = stringResource(R.string.yellamma_location_date),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = JatreLightGold.copy(alpha = 0.8f)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // View Schedule button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(JatreSaffron)
                            .clickable { onViewSchedule() }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.view_schedule),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = JatreDarkBrown,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HappeningNowCard(
    eventName: String,
    time: String,
    location: String,
    isLive: Boolean = true
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = JatreCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(JatreSaffron.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Event",
                    tint = JatreSaffron,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Live + time row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLive) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(JatreLiveGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.live),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JatreLiveGreen,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.status_upcoming).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JatreSaffron,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = time,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = JatreLightGold.copy(alpha = 0.6f)
                        )
                    )
                }

                Text(
                    text = eventName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = JatreCream,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = location,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = JatreLightGold.copy(alpha = 0.6f)
                    )
                )
            }

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "Details",
                tint = JatreGold.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
