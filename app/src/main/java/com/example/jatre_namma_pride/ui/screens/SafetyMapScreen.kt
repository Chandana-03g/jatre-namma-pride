package com.example.jatre_namma_pride.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Place
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.outlined.Call
import com.example.jatre_namma_pride.R
import com.example.jatre_namma_pride.util.EmojiConstants
import com.example.jatre_namma_pride.data.model.AlertType
import com.example.jatre_namma_pride.data.model.MapMarker
import com.example.jatre_namma_pride.data.repository.SafetyRepository
import com.example.jatre_namma_pride.ui.components.SectionHeader
import com.example.jatre_namma_pride.ui.theme.JatreCardBg
import com.example.jatre_namma_pride.ui.theme.JatreCream
import com.example.jatre_namma_pride.ui.theme.JatreDarkBrown
import com.example.jatre_namma_pride.ui.theme.JatreGold
import com.example.jatre_namma_pride.ui.theme.JatreLightGold
import com.example.jatre_namma_pride.ui.theme.JatreLiveGreen
import com.example.jatre_namma_pride.ui.theme.JatreOrange
import com.example.jatre_namma_pride.ui.theme.JatrePurple
import com.example.jatre_namma_pride.ui.theme.JatreSaffron
import com.example.jatre_namma_pride.ui.theme.JatreSurface
import com.example.jatre_namma_pride.ui.theme.JatreTeal
import com.example.jatre_namma_pride.util.LocalizationHelper
import java.util.Locale

/**
 * Fair Map & Info screen — organized as a simple digital fair navigation guide.
 * Fully localized, lightweight, and navigation-focused with scannable layout features.
 */
@Composable
fun SafetyMapScreen() {
    val highestAlert by SafetyRepository.getHighestPriorityAlert().collectAsState(initial = null)
    val mapMarkers by SafetyRepository.getMapMarkers().collectAsState(initial = emptyList())
    var selectedMarker by remember { mutableStateOf<MapMarker?>(null) }
    val config = LocalConfiguration.current

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
                            colors = listOf(JatrePurple.copy(alpha = 0.4f), JatreSurface)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Text(
                    text = stringResource(R.string.fair_map),
                    style = MaterialTheme.typography.displayMedium.copy(
                        color = JatreCream,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Text(
                    text = stringResource(R.string.fair_map_subtitle),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = JatreLightGold.copy(alpha = 0.7f)
                    )
                )
            }
        }

        // ── Live Safety Alert Banner (Only Top Priority Alert) ───────────────
        highestAlert?.let { alert ->
            item {
                val accentColor = when (alert.type) {
                    AlertType.EMERGENCY -> JatreSaffron
                    AlertType.CROWD -> JatreSaffron
                    AlertType.PARKING -> JatreTeal
                    AlertType.WEATHER -> JatreLightGold
                    AlertType.GUIDANCE -> JatreLiveGreen
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = JatreCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Row {
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(72.dp)
                                .background(accentColor)
                        )
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = alert.emoji,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = LocalizationHelper.resolve(alert.titleEn, alert.titleKn, config),
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = accentColor,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )
                                    if (alert.timestamp.isNotBlank()) {
                                        Text(
                                            text = alert.timestamp,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = JatreLightGold.copy(alpha = 0.4f)
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = LocalizationHelper.resolve(alert.messageEn, alert.messageKn, config),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = JatreCream.copy(alpha = 0.8f),
                                        lineHeight = 18.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── How to Use the Map Guidance Section (Compact & Secondary Priority) ──
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = JatreSurface.copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = EmojiConstants.INFO, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.map_guidance_title),
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = JatreLightGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.map_guidance_desc),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = JatreCream.copy(alpha = 0.85f),
                                lineHeight = 16.sp,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // ── Interactive Fair Map (With Integrated Legend) ────────────────────
        item {
            InteractiveMapSection(
                mapMarkers = mapMarkers,
                selectedMarker = selectedMarker,
                onMarkerSelect = { selectedMarker = it }
            )
        }

        // ── Quick Access Navigation Buttons (Find Locations) ─────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.find_locations),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = JatreLightGold.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionButton(
                        text = stringResource(R.string.btn_find_parking),
                        icon = EmojiConstants.PARKING,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedMarker = mapMarkers.firstOrNull { it.type.equals("parking", ignoreCase = true) }
                        }
                    )
                    QuickActionButton(
                        text = stringResource(R.string.btn_first_aid),
                        icon = EmojiConstants.FIRST_AID,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedMarker = mapMarkers.firstOrNull { it.type.equals("firstAid", ignoreCase = true) }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionButton(
                        text = stringResource(R.string.btn_food_court),
                        icon = EmojiConstants.FOOD,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedMarker = mapMarkers.firstOrNull { it.type.equals("stall", ignoreCase = true) }
                        }
                    )
                    QuickActionButton(
                        text = stringResource(R.string.btn_help_desk),
                        icon = EmojiConstants.POLICE,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedMarker = mapMarkers.firstOrNull { it.type.equals("helpDesk", ignoreCase = true) }
                        }
                    )
                }
            }
        }

        // ── Emergency Help Section ───────────────────────────────────────────
        item {
            SectionHeader(title = stringResource(R.string.emergency_help_title))
        }
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = JatreCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val context = LocalContext.current
                    EmergencyContactRow(
                        emoji = EmojiConstants.EMERGENCY,
                        label = stringResource(R.string.emergency_helpline_label),
                        number = stringResource(R.string.emergency_helpline_value),
                        onCall = { context.startActivity(android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:112"))) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    EmergencyContactRow(
                        emoji = EmojiConstants.AMBULANCE,
                        label = stringResource(R.string.first_aid_station_label),
                        number = stringResource(R.string.first_aid_station_value),
                        onCall = { context.startActivity(android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:108"))) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    EmergencyContactRow(
                        emoji = EmojiConstants.POLICE,
                        label = stringResource(R.string.police_help_desk_label),
                        number = stringResource(R.string.police_help_desk_value),
                        onCall = { context.startActivity(android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:100"))) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    EmergencyContactRow(
                        emoji = EmojiConstants.WOMAN,
                        label = stringResource(R.string.women_helpline_label),
                        number = stringResource(R.string.women_helpline_value),
                        onCall = { context.startActivity(android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:1091"))) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    EmergencyContactRow(
                        emoji = EmojiConstants.CHILD,
                        label = stringResource(R.string.child_helpline_label),
                        number = stringResource(R.string.child_helpline_value),
                        onCall = { context.startActivity(android.content.Intent(android.content.Intent.ACTION_DIAL, android.net.Uri.parse("tel:1098"))) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendPill(emoji: String, text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(JatreCardBg)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 15.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = JatreCream.copy(alpha = 0.9f),
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
private fun QuickActionButton(
    text: String,
    icon: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = JatreCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = JatreGold,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

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
            .padding(vertical = 4.dp),
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

@Composable
fun InteractiveMapSection(
    mapMarkers: List<MapMarker>,
    selectedMarker: MapMarker?,
    onMarkerSelect: (MapMarker?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Lightweight subtle instruction helper text above map
        Text(
            text = stringResource(R.string.map_tap_instruction),
            style = MaterialTheme.typography.labelSmall.copy(
                color = JatreLightGold.copy(alpha = 0.65f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        // Map Container
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Matches square layout perfectly
                .clip(RoundedCornerShape(16.dp))
                .background(JatreCardBg)
        ) {
            val mapWidth = maxWidth
            val mapHeight = maxHeight

            Image(
                painter = painterResource(R.drawable.img_jatre_map_layout),
                contentDescription = stringResource(R.string.fair_map),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 1. Minimal Walking Routes Overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                val startX = size.width * 0.5f
                val startY = size.height * 0.92f
                val centerX = size.width * 0.5f
                val centerY = size.height * 0.65f // Stage area
                val templeX = size.width * 0.75f
                val templeY = size.height * 0.25f
                val foodX = size.width * 0.85f
                val foodY = size.height * 0.5f

                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                val routeColor = Color(0x99FFFFFF) // Soft semi-transparent white/cream

                // Main central path from entrance to stage
                drawLine(
                    color = routeColor,
                    start = androidx.compose.ui.geometry.Offset(startX, startY),
                    end = androidx.compose.ui.geometry.Offset(centerX, centerY),
                    strokeWidth = 6f,
                    pathEffect = pathEffect
                )
                // Branch to Temple
                drawLine(
                    color = routeColor,
                    start = androidx.compose.ui.geometry.Offset(centerX, centerY),
                    end = androidx.compose.ui.geometry.Offset(templeX, templeY),
                    strokeWidth = 6f,
                    pathEffect = pathEffect
                )
                // Branch to Food Court
                drawLine(
                    color = routeColor,
                    start = androidx.compose.ui.geometry.Offset(centerX, centerY),
                    end = androidx.compose.ui.geometry.Offset(foodX, foodY),
                    strokeWidth = 6f,
                    pathEffect = pathEffect
                )
            }

            // Overlay Uniform Consistent Markers
            mapMarkers.forEach { marker ->
                val isSelected = selectedMarker?.id == marker.id
                val markerColor = when (marker.type) {
                    "parking" -> JatreTeal
                    "firstAid" -> Color(0xFFE53935)
                    "stall" -> JatreOrange
                    "temple" -> JatreSaffron
                    "stage" -> JatrePurple
                    "entry" -> JatreLiveGreen
                    else -> JatreGold
                }

                // Uniform marker size (36.dp) centered perfectly
                Box(
                    modifier = Modifier
                        .offset(
                            x = (mapWidth * marker.xPosition) - 18.dp,
                            y = (mapHeight * marker.yPosition) - 18.dp
                        )
                        .size(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) JatreCream else markerColor)
                        .clickable { onMarkerSelect(marker) },
                    contentAlignment = Alignment.Center
                ) {
                    val iconVector = when (marker.type) {
                        "parking" -> Icons.Filled.DirectionsCar
                        "firstAid" -> Icons.Filled.LocalHospital
                        "stall" -> Icons.Filled.Fastfood
                        "temple" -> Icons.Filled.Place
                        "entry" -> Icons.Filled.MeetingRoom
                        else -> Icons.Filled.Place
                    }
                    Icon(
                        imageVector = iconVector,
                        contentDescription = marker.name,
                        tint = if (isSelected) markerColor else JatreCream,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 2. Compact Floating Zone Labels
            FloatingZoneLabel(stringResource(R.string.zone_a_temple), 0.75f, 0.32f, mapWidth, mapHeight)
            FloatingZoneLabel(stringResource(R.string.zone_b_food), 0.85f, 0.58f, mapWidth, mapHeight)
            FloatingZoneLabel(stringResource(R.string.zone_c_parking), 0.15f, 0.58f, mapWidth, mapHeight)
            FloatingZoneLabel(stringResource(R.string.zone_d_stage), 0.5f, 0.72f, mapWidth, mapHeight)
            FloatingZoneLabel(stringResource(R.string.zone_e_help), 0.8f, 0.92f, mapWidth, mapHeight)

            // 3. Subtle "You Are Here" Indicator near Main Entrance
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 0.9f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseScale"
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = (mapWidth * 0.5f) - 45.dp,
                        y = (mapHeight * 0.92f) + 20.dp
                    )
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                    }
                    .clip(RoundedCornerShape(12.dp))
                    .background(JatreLiveGreen)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.you_are_here),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = JatreCream,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp
                    )
                )
            }
        }

        // Map Legend (Visually snug against the map container)
        Spacer(modifier = Modifier.height(6.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            // Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                LegendPill(EmojiConstants.PARKING, stringResource(R.string.legend_parking))
                LegendPill(EmojiConstants.FIRST_AID, stringResource(R.string.legend_first_aid))
                LegendPill(EmojiConstants.FOOD, stringResource(R.string.legend_food_court))
            }
            Spacer(modifier = Modifier.height(6.dp))
            // Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                LegendPill(EmojiConstants.TEMPLE, stringResource(R.string.legend_temple))
                LegendPill(EmojiConstants.POLICE, stringResource(R.string.legend_help_desk))
            }
        }

        // Marker Detail Scannable Localized Popup
        if (selectedMarker != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = JatreDarkBrown),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    val markerColor = when (selectedMarker.type) {
                        "parking" -> JatreTeal
                        "firstAid" -> Color(0xFFE53935)
                        "stall" -> JatreOrange
                        "temple" -> JatreSaffron
                        "stage" -> JatrePurple
                        "entry" -> JatreLiveGreen
                        else -> JatreGold
                    }

                    // Map strings natively to ensure 100% full localization matching
                    val titleRes = when (selectedMarker.type) {
                        "parking" -> R.string.popup_title_parking
                        "stage" -> R.string.popup_title_stage
                        "temple" -> R.string.popup_title_temple
                        "stall" -> R.string.popup_title_stall
                        "firstAid" -> R.string.popup_title_first_aid
                        "helpDesk" -> R.string.popup_title_help_desk
                        "entry" -> R.string.popup_title_entry
                        else -> null
                    }
                    val descRes = when (selectedMarker.type) {
                        "parking" -> R.string.popup_desc_parking
                        "stage" -> R.string.popup_desc_stage
                        "temple" -> R.string.popup_desc_temple
                        "stall" -> R.string.popup_desc_stall
                        "firstAid" -> R.string.popup_desc_first_aid
                        "helpDesk" -> R.string.popup_desc_help_desk
                        "entry" -> R.string.popup_desc_entry
                        else -> null
                    }
                    val typeRes = when (selectedMarker.type) {
                        "entry" -> R.string.popup_type_entry
                        "parking" -> R.string.category_parking
                        "stage" -> R.string.cultural
                        "temple" -> R.string.category_attraction
                        "stall" -> R.string.category_amenities
                        "firstAid" -> R.string.category_safety
                        "helpDesk" -> R.string.help_desk
                        else -> null
                    }

                    val config = LocalConfiguration.current
                    val localizedTitle = titleRes?.let { stringResource(it) } ?: LocalizationHelper.resolve(selectedMarker.name, selectedMarker.nameKn, config)
                    val localizedDesc = descRes?.let { stringResource(it) } ?: LocalizationHelper.resolve(selectedMarker.description, selectedMarker.descriptionKn, config)
                    val localizedType = typeRes?.let { stringResource(it) } ?: selectedMarker.type.uppercase()

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(markerColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = localizedType,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = markerColor,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = localizedTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = JatreCream,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = localizedDesc,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = JatreCream.copy(alpha = 0.8f),
                                lineHeight = 18.sp
                            )
                        )
                    }

                    // Close Button
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(JatreSurface.copy(alpha = 0.5f))
                            .clickable { onMarkerSelect(null) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✕", color = JatreLightGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingZoneLabel(
    text: String,
    xRatio: Float,
    yRatio: Float,
    mapWidth: androidx.compose.ui.unit.Dp,
    mapHeight: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .offset(
                x = (mapWidth * xRatio) - 42.dp,
                y = (mapHeight * yRatio)
            )
            .clip(RoundedCornerShape(8.dp))
            .background(JatreCardBg.copy(alpha = 0.85f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = JatreLightGold,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp
            )
        )
    }
}
