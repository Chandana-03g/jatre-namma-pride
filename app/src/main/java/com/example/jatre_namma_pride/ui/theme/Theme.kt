package com.example.jatre_namma_pride.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Always use the warm festive dark theme — no dynamic color, no light/dark toggle
private val JatreDarkColorScheme = darkColorScheme(
    primary          = JatreSaffron,
    onPrimary        = JatreDarkBrown,
    primaryContainer = JatreMaroon,
    onPrimaryContainer = JatreLightGold,

    secondary        = JatreGold,
    onSecondary      = JatreDarkBrown,
    secondaryContainer = JatreCardBg,
    onSecondaryContainer = JatreCream,

    tertiary         = JatrePurple,
    onTertiary       = JatreWhite,

    background       = JatreSurface,
    onBackground     = JatreCream,

    surface          = JatreCardBg,
    onSurface        = JatreCream,
    surfaceVariant   = Color(0xFF2A1200),
    onSurfaceVariant = JatreSubtext,

    error            = Color(0xFFCF6679),
    onError          = JatreWhite,

    outline          = JatreDivider,
)

@Composable
fun JatreNammaPrideTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = JatreDarkColorScheme,
        typography  = JatreTypography,
        content     = content
    )
}