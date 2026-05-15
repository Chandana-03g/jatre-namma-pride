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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.example.jatre_namma_pride.R
import com.example.jatre_namma_pride.util.LocalizationHelper
import com.example.jatre_namma_pride.data.model.CulturalStory
import com.example.jatre_namma_pride.data.repository.EventRepository
import com.example.jatre_namma_pride.ui.components.CategoryBadge
import com.example.jatre_namma_pride.ui.theme.JatreCardBg
import com.example.jatre_namma_pride.ui.theme.JatreCream
import com.example.jatre_namma_pride.ui.theme.JatreDarkBrown
import com.example.jatre_namma_pride.ui.theme.JatreGold
import com.example.jatre_namma_pride.ui.theme.JatreLightGold
import com.example.jatre_namma_pride.ui.theme.JatreMaroon
import com.example.jatre_namma_pride.ui.theme.JatreSaffron
import com.example.jatre_namma_pride.ui.theme.JatreSurface

/**
 * Full-screen list of all cultural stories.
 * Navigable from the "See All" button on the Home Screen.
 */
@Composable
fun CulturalStoriesListScreen(
    onBackClick: () -> Unit,
    onStoryClick: (Int) -> Unit
) {
    val stories by EventRepository.getCulturalStories().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JatreSurface)
    ) {
        // ── Top Bar ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
                    text = stringResource(R.string.cultural_stories),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = JatreCream,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Text(
                    text = stringResource(R.string.cultural_stories_subtitle),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = JatreGold.copy(alpha = 0.7f)
                    )
                )
            }
        }

        // ── Story List ───────────────────────────────────────────────────────
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(stories) { story ->
                StoryListCard(
                    story = story,
                    onClick = { onStoryClick(story.id) }
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

/**
 * Wide card for the stories list screen. Image on top with gradient scrim,
 * category badge, title, summary, and read time.
 */
@Composable
private fun StoryListCard(
    story: CulturalStory,
    onClick: () -> Unit
) {
    val config = LocalConfiguration.current
    val title = LocalizationHelper.resolve(story.titleEn, story.titleKn, config)
    val category = LocalizationHelper.resolve(story.categoryEn, story.categoryKn, config)
    val summary = LocalizationHelper.resolve(story.summaryEn, story.summaryKn, config)
    val readTime = LocalizationHelper.resolve(story.readTimeEn, story.readTimeKn, config)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = JatreCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            // Image with overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                if (story.imageResId != 0) {
                    Image(
                        painter = painterResource(id = story.imageResId),
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        JatreDarkBrown.copy(alpha = 0.7f)
                                    ),
                                    startY = 60f
                                )
                            )
                    )
                } else {
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
                        Text(text = story.emoji, fontSize = 48.sp)
                    }
                }

                // Category badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    CategoryBadge(
                        text = category,
                        backgroundColor = JatreSaffron
                    )
                }
            }

            // Content area
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = JatreCream,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = JatreCream.copy(alpha = 0.7f),
                        lineHeight = 20.sp
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = "Read time",
                        tint = JatreLightGold.copy(alpha = 0.5f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
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
}
