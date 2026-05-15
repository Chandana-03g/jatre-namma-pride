package com.example.jatre_namma_pride.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import com.example.jatre_namma_pride.util.LocalizationHelper
import com.example.jatre_namma_pride.data.model.CulturalStory
import com.example.jatre_namma_pride.ui.theme.JatreCardBg
import com.example.jatre_namma_pride.ui.theme.JatreCream
import com.example.jatre_namma_pride.ui.theme.JatreDarkBrown
import com.example.jatre_namma_pride.ui.theme.JatreLightGold
import com.example.jatre_namma_pride.ui.theme.JatreMaroon
import com.example.jatre_namma_pride.ui.theme.JatreSaffron

/**
 * Card for the horizontal Cultural Stories carousel on the Home screen.
 * Shows a real image with category badge, title, and read time.
 */
@Composable
fun CulturalStoryCard(
    story: CulturalStory,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val config = LocalConfiguration.current
    val title = LocalizationHelper.resolve(story.titleEn, story.titleKn, config)
    val category = LocalizationHelper.resolve(story.categoryEn, story.categoryKn, config)
    val readTime = LocalizationHelper.resolve(story.readTimeEn, story.readTimeKn, config)

    Card(
        modifier = modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = JatreCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Image area with actual photo or fallback
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
            ) {
                if (story.imageResId != 0) {
                    Image(
                        painter = painterResource(id = story.imageResId),
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient scrim for badge readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        JatreDarkBrown.copy(alpha = 0.5f)
                                    ),
                                    startY = 40f
                                )
                            )
                    )
                } else {
                    // Fallback gradient + emoji
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        JatreMaroon.copy(alpha = 0.7f),
                                        JatreDarkBrown
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = story.emoji, fontSize = 36.sp)
                    }
                }

                // Category badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    CategoryBadge(
                        text = category,
                        backgroundColor = JatreSaffron
                    )
                }
            }

            // Text content
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = JatreCream,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = readTime,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = JatreLightGold.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}
