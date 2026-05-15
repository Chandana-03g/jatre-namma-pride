package com.example.jatre_namma_pride.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jatre_namma_pride.data.model.AlertType
import com.example.jatre_namma_pride.data.repository.SafetyRepository
import com.example.jatre_namma_pride.util.LocalizationHelper
import com.example.jatre_namma_pride.ui.theme.JatreCardBg
import com.example.jatre_namma_pride.ui.theme.JatreCream
import com.example.jatre_namma_pride.ui.theme.JatreDarkBrown
import com.example.jatre_namma_pride.ui.theme.JatreLightGold
import com.example.jatre_namma_pride.ui.theme.JatreLiveGreen
import com.example.jatre_namma_pride.ui.theme.JatreSaffron
import com.example.jatre_namma_pride.ui.theme.JatreTeal
import kotlinx.coroutines.delay

/**
 * Live safety alert banner that AUTO-ROTATES through all active alerts every 5 seconds.
 * Works like a real public-safety announcement board at a fair — always showing the
 * latest, most relevant information to visitors with smooth slide-up animations.
 */
@Composable
fun SafetyReminderBanner(modifier: Modifier = Modifier) {
    val config = LocalConfiguration.current
    val alerts by SafetyRepository.getActiveAlerts().collectAsState(initial = emptyList())

    if (alerts.isEmpty()) return

    var currentIndex by remember { mutableIntStateOf(0) }

    // Auto-rotate every 5 seconds
    LaunchedEffect(alerts.size) {
        if (alerts.size > 1) {
            while (true) {
                delay(5000L)
                currentIndex = (currentIndex + 1) % alerts.size
            }
        }
    }

    // Safety check for index bounds
    val safeIndex = currentIndex.coerceIn(0, (alerts.size - 1).coerceAtLeast(0))
    val currentAlert = alerts[safeIndex]

    val accentColor = when (currentAlert.type) {
        AlertType.EMERGENCY -> JatreSaffron
        AlertType.CROWD -> JatreSaffron
        AlertType.PARKING -> JatreTeal
        AlertType.WEATHER -> JatreLightGold
        AlertType.GUIDANCE -> JatreLiveGreen
    }

    val typeLabel = when (currentAlert.type) {
        AlertType.EMERGENCY -> "EMERGENCY"
        AlertType.CROWD -> "CROWD ALERT"
        AlertType.PARKING -> "PARKING UPDATE"
        AlertType.WEATHER -> "WEATHER ALERT"
        AlertType.GUIDANCE -> "SAFETY TIP"
    }

    // Pulsing animation for the live dot
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = JatreCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top bar: LIVE indicator + type label + page indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Pulsing live dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Red.copy(alpha = pulseAlpha))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.Red,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = JatreLightGold.copy(alpha = 0.4f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = typeLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = accentColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    )
                }

                // Page indicator: "2 / 5"
                Text(
                    text = "${safeIndex + 1} / ${alerts.size}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = JatreLightGold.copy(alpha = 0.4f),
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(JatreDarkBrown.copy(alpha = 0.4f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Animated alert content — slides up on change
            AnimatedContent(
                targetState = safeIndex,
                transitionSpec = {
                    ContentTransform(
                        targetContentEnter = slideInVertically { it } + fadeIn(tween(300)),
                        initialContentExit = slideOutVertically { -it } + fadeOut(tween(300))
                    )
                },
                label = "alertRotation"
            ) { index ->
                val alert = alerts[index.coerceIn(0, (alerts.size - 1).coerceAtLeast(0))]
                val displayTitle = LocalizationHelper.resolve(alert.titleEn, alert.titleKn, config)
                val displayMessage = LocalizationHelper.resolve(alert.messageEn, alert.messageKn, config)
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = alert.emoji, fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = displayTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = JatreCream,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = displayMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = JatreCream.copy(alpha = 0.85f),
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }
    }
}
