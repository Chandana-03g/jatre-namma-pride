package com.example.jatre_namma_pride.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Schedule
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.example.jatre_namma_pride.R
import com.example.jatre_namma_pride.util.LocalizationHelper
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
 * Full-screen detail view for reading a cultural story.
 * Features a hero banner image, title, metadata, and formatted body content.
 */
@Composable
fun CulturalStoryDetailsScreen(
    storyId: Int,
    onBackClick: () -> Unit
) {
    val story by EventRepository.getStoryById(storyId).collectAsState(initial = null)
    val config = LocalConfiguration.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JatreSurface)
            .verticalScroll(rememberScrollState())
    ) {
        story?.let { s ->
            val title = LocalizationHelper.resolve(s.titleEn, s.titleKn, config)
            val category = LocalizationHelper.resolve(s.categoryEn, s.categoryKn, config)
            val readTime = LocalizationHelper.resolve(s.readTimeEn, s.readTimeKn, config)
            val summary = LocalizationHelper.resolve(s.summaryEn, s.summaryKn, config)
            val fullContent = LocalizationHelper.resolve(s.fullContentEn, s.fullContentKn, config)
            
            // ── Hero Image Banner ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                if (s.imageResId != 0) {
                    Image(
                        painter = painterResource(id = s.imageResId),
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
                                        JatreDarkBrown.copy(alpha = 0.3f),
                                        JatreDarkBrown.copy(alpha = 0.85f)
                                    )
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
                        Text(text = s.emoji, fontSize = 64.sp)
                    }
                }

                // Back button
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(JatreDarkBrown.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = JatreCream,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Title overlay at the bottom of the image
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    CategoryBadge(text = category, backgroundColor = JatreSaffron)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = JatreCream,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 30.sp
                        )
                    )
                }
            }

            // ── Metadata Bar ─────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(JatreCardBg)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = "Read time",
                        tint = JatreSaffron,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = readTime,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = JatreLightGold.copy(alpha = 0.8f),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = JatreGold.copy(alpha = 0.7f),
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // ── Story Content ────────────────────────────────────────────────
            Column(modifier = Modifier.padding(20.dp)) {
                // Summary highlight
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(JatreSaffron.copy(alpha = 0.1f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = JatreSaffron,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 24.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Full body content
                val content = fullContent.ifBlank { summary }
                val paragraphs = content.split("\n\n").filter { it.isNotBlank() }

                paragraphs.forEach { paragraph ->
                    Text(
                        text = paragraph.trim(),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = JatreCream.copy(alpha = 0.85f),
                            lineHeight = 26.sp,
                            letterSpacing = 0.3.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        } ?: run {
            // Loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.loading_story),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = JatreCream.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}
