package com.example.jatre_namma_pride.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jatre_namma_pride.ui.theme.JatreDarkBrown
import com.example.jatre_namma_pride.ui.theme.JatreGold
import com.example.jatre_namma_pride.ui.theme.JatreLightGold
import com.example.jatre_namma_pride.ui.theme.JatreMaroon
import com.example.jatre_namma_pride.ui.theme.JatreSaffron
import com.example.jatre_namma_pride.util.EmojiConstants
import kotlinx.coroutines.delay

/**
 * Splash screen with pulsing temple icon, app branding, and decorative elements.
 * Auto-navigates after a delay.
 */
@Composable
fun SplashScreen(onSplashDone: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2800)
        onSplashDone()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "splash_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "chariot_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(JatreDarkBrown, JatreMaroon, JatreDarkBrown)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Chariot emoji as decorative focal point (pulsing)
            Text(
                text = EmojiConstants.TEMPLE,
                fontSize = 90.sp,
                modifier = Modifier.scale(scale)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // App name in Kannada + English
            Text(
                text = "ನಮ್ಮ ಜಾತ್ರೆ",
                style = MaterialTheme.typography.displayMedium.copy(
                    color = JatreGold,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Namma Jatre",
                style = MaterialTheme.typography.titleLarge.copy(color = JatreLightGold),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Decorative rangoli dots
            Text(
                text = "✦  ●  ✦  ●  ✦",
                fontSize = 14.sp,
                color = JatreSaffron,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ಕರ್ನಾಟಕ ಸಂಸ್ಕೃತಿ · Karnataka Culture",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = JatreLightGold.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}
