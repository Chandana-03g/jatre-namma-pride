package com.example.jatre_namma_pride.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jatre_namma_pride.R
import com.example.jatre_namma_pride.data.model.EventCategory
import com.example.jatre_namma_pride.data.model.EventStatus
import com.example.jatre_namma_pride.data.model.JatreEvent
import com.example.jatre_namma_pride.ui.theme.JatreCardBg
import com.example.jatre_namma_pride.ui.theme.JatreCream
import com.example.jatre_namma_pride.ui.theme.JatreDivider
import com.example.jatre_namma_pride.ui.theme.JatreGold
import com.example.jatre_namma_pride.ui.theme.JatreLightGold
import com.example.jatre_namma_pride.ui.theme.JatreLiveGreen
import com.example.jatre_namma_pride.ui.theme.JatreMaroon
import com.example.jatre_namma_pride.ui.theme.JatreSaffron
import com.example.jatre_namma_pride.ui.theme.JatreSurface
import com.example.jatre_namma_pride.ui.theme.JatreTeal
import com.example.jatre_namma_pride.util.LocalizationHelper

/**
 * Event card for the Schedule screen timeline.
 * Shows time, category badge, localized event name, localized location, and a live badge.
 * All locale resolution is done via [LocalizationHelper] — no inline if/else locale checks.
 */
@Composable
fun EventCard(
    event: JatreEvent,
    modifier: Modifier = Modifier
) {
    val config = LocalConfiguration.current
    val isLive = event.status == EventStatus.LIVE
    val isPast = event.status == EventStatus.PAST
    val textAlpha = if (isPast) 0.45f else 1f

    // Resolve localized values before rendering
    val displayName     = LocalizationHelper.resolve(event.nameEn, event.nameKn, config)
    val displayLocation = LocalizationHelper.resolve(event.locationEn, event.locationKn, config)

    val cardBg = when (event.status) {
        EventStatus.LIVE     -> JatreMaroon.copy(alpha = 0.85f)
        EventStatus.UPCOMING -> JatreCardBg
        EventStatus.PAST     -> JatreSurface
    }

    val categoryColor = when (event.category) {
        EventCategory.RELIGIOUS -> JatreSaffron
        EventCategory.CULTURAL  -> JatreTeal
        EventCategory.SPORTS    -> JatreLiveGreen
        EventCategory.TRADE     -> JatreGold
        EventCategory.ALL       -> JatreSaffron
    }

    val categoryText = when (event.category) {
        EventCategory.RELIGIOUS -> stringResource(R.string.religious)
        EventCategory.CULTURAL  -> stringResource(R.string.cultural)
        EventCategory.SPORTS    -> stringResource(R.string.sports)
        EventCategory.TRADE     -> stringResource(R.string.trade)
        EventCategory.ALL       -> stringResource(R.string.all_items)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isLive) 10.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Time column
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.width(60.dp)
            ) {
                Text(
                    text = event.time,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = JatreCream.copy(alpha = textAlpha),
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Divider line
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(70.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(JatreDivider.copy(alpha = 0.4f))
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                // Category badge + live indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryBadge(
                        text = categoryText,
                        backgroundColor = categoryColor
                    )
                    if (isLive) {
                        LiveBadge()
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Localized event name
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = JatreCream.copy(alpha = textAlpha),
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Localized location
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = JatreSaffron.copy(alpha = textAlpha * 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = displayLocation,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = JatreLightGold.copy(alpha = textAlpha * 0.6f)
                        )
                    )
                }
            }
        }
    }
}
