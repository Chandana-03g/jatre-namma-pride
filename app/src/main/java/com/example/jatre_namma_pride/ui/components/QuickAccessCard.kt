package com.example.jatre_namma_pride.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jatre_namma_pride.ui.theme.JatreCream
import com.example.jatre_namma_pride.ui.theme.JatreGold
import com.example.jatre_namma_pride.ui.theme.JatreLightGold
import com.example.jatre_namma_pride.ui.theme.JatreSaffron

/**
 * Premium gradient card for the Quick Access grid on the Home screen.
 * Each card has a unique gradient background, icon, label, Kannada subtitle, and a stat badge.
 */
@Composable
fun QuickAccessCard(
    icon: ImageVector,
    label: String,
    statText: String,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors))
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                // Icon
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = JatreCream,
                    modifier = Modifier.size(32.dp)
                )

                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = JatreCream,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    // Stat badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = JatreSaffron.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = statText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JatreGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}
