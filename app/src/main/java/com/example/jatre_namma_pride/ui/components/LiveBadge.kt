package com.example.jatre_namma_pride.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jatre_namma_pride.ui.theme.JatreDarkBrown
import com.example.jatre_namma_pride.ui.theme.JatreLiveGreen
import com.example.jatre_namma_pride.ui.theme.JatreSaffron
import androidx.compose.ui.res.stringResource
import com.example.jatre_namma_pride.R

/**
 * Animated "LIVE NOW" badge with a pulsing green dot.
 */
@Composable
fun LiveBadge(modifier: Modifier = Modifier) {
    val pulse = rememberInfiniteTransition(label = "live_pulse")
    val dotScale by pulse.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_scale"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(JatreSaffron)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(dotScale)
                    .clip(CircleShape)
                    .background(JatreLiveGreen)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.live_now),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = JatreDarkBrown,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }
    }
}

/**
 * Simple category tag badge (e.g., "Sports", "Cultural", "LOST", "FOUND").
 */
@Composable
fun CategoryBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color = JatreDarkBrown,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
