package com.example.jatre_namma_pride.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.jatre_namma_pride.R
import com.example.jatre_namma_pride.data.model.ItemType
import com.example.jatre_namma_pride.data.model.LostFoundItem
import com.example.jatre_namma_pride.ui.theme.JatreCardBg
import com.example.jatre_namma_pride.ui.theme.JatreCream
import com.example.jatre_namma_pride.ui.theme.JatreDarkBrown
import com.example.jatre_namma_pride.ui.theme.JatreGold
import com.example.jatre_namma_pride.ui.theme.JatreLightGold
import com.example.jatre_namma_pride.ui.theme.JatreLiveGreen
import com.example.jatre_namma_pride.ui.theme.JatreSaffron
import com.example.jatre_namma_pride.ui.theme.JatreSurface
import com.example.jatre_namma_pride.util.EmojiConstants
import com.example.jatre_namma_pride.util.LocalizationHelper

/**
 * Vertical card for displaying a Lost or Found item.
 * Image on top, localized details in middle, localized action buttons at bottom.
 * All locale resolution is done via [LocalizationHelper] — no inline if/else locale checks.
 */
@Composable
fun LostFoundItemCard(
    item: LostFoundItem,
    onResolve: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val config  = LocalConfiguration.current
    val context = LocalContext.current

    val isLost = item.type == ItemType.LOST
    val accent = if (isLost) JatreSaffron else JatreLiveGreen

    // Resolve localized display values before rendering
    val displayTitle       = LocalizationHelper.resolve(item.titleEn, item.titleKn, config)
    val displayDescription = LocalizationHelper.resolve(item.descriptionEn, item.descriptionKn, config)
    val displayLocation    = LocalizationHelper.resolve(item.locationEn, item.locationKn, config)

    // Localized status labels from string resources
    val statusText    = if (isLost) stringResource(R.string.status_lost)
                        else        stringResource(R.string.status_found)
    val resolvedText  = stringResource(R.string.status_resolved)
    val contactText   = stringResource(R.string.btn_contact)
    val markResolvedText = stringResource(R.string.btn_mark_resolved)
    val reporterText  = stringResource(R.string.label_by_reporter, item.time, item.reportedBy)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isResolved) JatreSurface.copy(alpha = 0.8f) else JatreCardBg
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (item.isResolved) 1.dp else 4.dp
        )
    ) {
        Column {
            // ── Image Area (top) ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                val hasUri = !item.imageUri.isNullOrBlank()
                val hasRes = item.imageResId != 0

                if (hasUri) {
                    val bitmap = remember(item.imageUri) {
                        try {
                            context.contentResolver
                                .openInputStream(Uri.parse(item.imageUri))
                                ?.use { BitmapFactory.decodeStream(it) }
                        } catch (_: Exception) { null }
                    }
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = displayTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        FallbackImageBox(isLost = isLost, category = item.category)
                    }
                } else if (hasRes) {
                    Image(
                        painter = painterResource(id = item.imageResId),
                        contentDescription = displayTitle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    FallbackImageBox(isLost = isLost, category = item.category)
                }

                // Gradient scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, JatreDarkBrown.copy(alpha = 0.5f)),
                                startY = 100f
                            )
                        )
                )

                // Status badge (top-left) — localized
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    CategoryBadge(text = statusText, backgroundColor = accent)
                }

                // Resolved badge (top-right) — localized
                if (item.isResolved) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(JatreLiveGreen.copy(alpha = 0.9f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = JatreDarkBrown,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = resolvedText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = JatreDarkBrown,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }
                    }
                }
            }

            // ── Content Area ─────────────────────────────────────────────────
            Column(modifier = Modifier.padding(14.dp)) {
                // Localized title
                Text(
                    text = displayTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (item.isResolved) JatreCream.copy(alpha = 0.5f) else JatreCream,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Localized description
                Text(
                    text = displayDescription,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = JatreCream.copy(alpha = if (item.isResolved) 0.35f else 0.65f)
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Localized location row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = accent.copy(alpha = if (item.isResolved) 0.4f else 1f),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = displayLocation,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = JatreLightGold.copy(alpha = if (item.isResolved) 0.35f else 0.7f)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Contact number row
                if (item.contactNumber.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Call,
                            contentDescription = null,
                            tint = JatreGold.copy(alpha = if (item.isResolved) 0.3f else 0.6f),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.contactNumber,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = JatreLightGold.copy(alpha = if (item.isResolved) 0.3f else 0.6f)
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // Localized reporter / time row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = JatreGold.copy(alpha = if (item.isResolved) 0.25f else 0.5f),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = reporterText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = JatreLightGold.copy(alpha = if (item.isResolved) 0.3f else 0.5f)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // ── Localized Action Buttons ──────────────────────────────────
                if (!item.isResolved) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${item.contactNumber}")
                                }
                                context.startActivity(intent)
                            },
                            border = BorderStroke(1.dp, accent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = contactText,
                                style = MaterialTheme.typography.labelLarge.copy(color = accent)
                            )
                        }
                        Button(
                            onClick = { onResolve(item.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = JatreLiveGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = JatreDarkBrown,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = markResolvedText,
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
}

/**
 * Fallback gradient box with category emoji when no image is available.
 */
@Composable
private fun FallbackImageBox(isLost: Boolean, category: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isLost)
                        listOf(JatreSaffron.copy(alpha = 0.3f), JatreDarkBrown)
                    else
                        listOf(JatreLiveGreen.copy(alpha = 0.2f), JatreDarkBrown)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when (category) {
                "Person"    -> EmojiConstants.PERSON
                "Jewellery" -> EmojiConstants.JEWELLERY
                "Bag"       -> EmojiConstants.BAG
                else        -> EmojiConstants.PACKAGE
            },
            style = MaterialTheme.typography.displayLarge
        )
    }
}
