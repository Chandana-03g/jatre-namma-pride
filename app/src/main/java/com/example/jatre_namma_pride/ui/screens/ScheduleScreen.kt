package com.example.jatre_namma_pride.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.jatre_namma_pride.R
import com.example.jatre_namma_pride.data.model.EventCategory
import com.example.jatre_namma_pride.data.model.EventStatus
import com.example.jatre_namma_pride.data.model.JatreEvent
import com.example.jatre_namma_pride.data.repository.EventRepository
import com.example.jatre_namma_pride.ui.components.EventCard
import com.example.jatre_namma_pride.ui.components.FilterChipRow
import com.example.jatre_namma_pride.ui.components.LiveBadge
import com.example.jatre_namma_pride.ui.components.SectionHeader
import com.example.jatre_namma_pride.ui.theme.JatreCardBg
import com.example.jatre_namma_pride.ui.theme.JatreCream
import com.example.jatre_namma_pride.ui.theme.JatreDarkBrown
import com.example.jatre_namma_pride.ui.theme.JatreGold
import com.example.jatre_namma_pride.ui.theme.JatreLightGold
import com.example.jatre_namma_pride.ui.theme.JatreLiveGreen
import com.example.jatre_namma_pride.ui.theme.JatreMaroon
import com.example.jatre_namma_pride.ui.theme.JatreSaffron
import com.example.jatre_namma_pride.ui.theme.JatreSurface
import com.example.jatre_namma_pride.util.EmojiConstants
import com.example.jatre_namma_pride.util.LocalizationHelper

/**
 * Schedule screen matching the premium UI design.
 * Shows live event highlight, filter chips, and a timeline of events.
 */
@Composable
fun ScheduleScreen() {
    val allEvents by EventRepository.getAllEvents().collectAsState(initial = emptyList())
    val liveEvent = allEvents.firstOrNull { it.status == EventStatus.LIVE }

    val allLabel = stringResource(R.string.all_items)
    val religiousLabel = stringResource(R.string.religious)
    val culturalLabel = stringResource(R.string.cultural)
    val sportsLabel = stringResource(R.string.sports)

    var selectedFilter by remember { mutableStateOf(allLabel) }
    val filterChips = listOf(allLabel, religiousLabel, culturalLabel, sportsLabel)

    val filteredEvents = when (selectedFilter) {
        allLabel -> allEvents.filter { it.status != EventStatus.LIVE }
        else -> {
            val category = when(selectedFilter) {
                religiousLabel -> EventCategory.RELIGIOUS
                culturalLabel -> EventCategory.CULTURAL
                sportsLabel -> EventCategory.SPORTS
                else -> null
            }
            allEvents.filter {
                it.status != EventStatus.LIVE &&
                    (category == null || it.category == category)
            }
        }
    }

    val availableDays = allEvents.map { it.day }.distinct().sorted()
    var selectedDay by remember { mutableStateOf(1) }

    // If availableDays is loaded and selectedDay is not in it, default to the first available day
    if (availableDays.isNotEmpty() && !availableDays.contains(selectedDay)) {
        selectedDay = availableDays.first()
    }

    val dayFilteredEvents = filteredEvents.filter { it.day == selectedDay }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(JatreSurface),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Header ───────────────────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(JatreMaroon.copy(alpha = 0.5f), JatreSurface)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Text(
                    text = stringResource(R.string.live_now_day_2),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = JatreSaffron,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.todays_events),
                    style = MaterialTheme.typography.displayMedium.copy(
                        color = JatreCream,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.date_jatre),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = JatreLightGold.copy(alpha = 0.7f)
                    )
                )
            }
        }

        // ── Live Event Highlight ─────────────────────────────────────────────
        if (liveEvent != null) {
            item {
                LiveEventHighlightCard(event = liveEvent)
            }
        }

        // ── Day Selector ─────────────────────────────────────────────────────
        if (availableDays.size > 1) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                ScrollableTabRow(
                    selectedTabIndex = availableDays.indexOf(selectedDay).coerceAtLeast(0),
                    containerColor = Color.Transparent,
                    contentColor = JatreGold,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        val index = availableDays.indexOf(selectedDay).coerceAtLeast(0)
                        if (index < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[index]),
                                color = JatreSaffron,
                                height = 3.dp
                            )
                        }
                    },
                    divider = {}
                ) {
                    availableDays.forEach { day ->
                        val isSelected = selectedDay == day
                        Tab(
                            selected = isSelected,
                            onClick = { selectedDay = day },
                            text = {
                                Text(
                                    text = stringResource(R.string.day_label, day),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) JatreSaffron else JatreLightGold.copy(alpha = 0.7f)
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }

        // ── Filter Chips ─────────────────────────────────────────────────────
        item {
            Spacer(modifier = Modifier.height(16.dp))
            FilterChipRow(
                chips = filterChips,
                selectedChip = selectedFilter,
                onChipSelected = { selectedFilter = it }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // ── Upcoming & Completed Section ─────────────────────────────────────
        item { SectionHeader(title = stringResource(R.string.events_for_day, selectedDay)) }

        if (dayFilteredEvents.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.no_events_found, selectedDay),
                        style = MaterialTheme.typography.bodyMedium.copy(color = JatreLightGold.copy(alpha = 0.6f))
                    )
                }
            }
        } else {
            items(dayFilteredEvents) { event ->
                EventCard(
                    event = event,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * Expanded card showing the currently live event with full details.
 */
@Composable
private fun LiveEventHighlightCard(event: JatreEvent) {
    val config = LocalConfiguration.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = JatreCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Live badge + time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LiveBadge()
                Text(
                    text = "${event.time} – ${event.endTime}",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = JatreGold,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Event icon + name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(JatreSaffron.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = EmojiConstants.TEMPLE, style = MaterialTheme.typography.titleLarge)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    val displayName = LocalizationHelper.resolve(event.nameEn, event.nameKn, config)
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = JatreCream,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Location
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "Location",
                    tint = JatreSaffron.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = LocalizationHelper.resolve(event.locationEn, event.locationKn, config),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = JatreLightGold.copy(alpha = 0.7f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            Text(
                text = LocalizationHelper.resolve(event.descriptionEn, event.descriptionKn, config),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = JatreCream.copy(alpha = 0.7f)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Start/End time footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.started) + " ${event.time}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = JatreLightGold.copy(alpha = 0.5f)
                    )
                )
                Text(
                    text = stringResource(R.string.ends) + " ${event.endTime}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = JatreLightGold.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}
