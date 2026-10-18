package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = IslamicGoldLight,
    onPrimary = DarkBackground,
    primaryContainer = IslamicEmeraldDark,
    onPrimaryContainer = IslamicGoldLight,
    secondary = IslamicEmeraldLight,
    onSecondary = DarkBackground,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = TextPrimaryDark,
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = IslamicEmerald,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4EAE2),
    onPrimaryContainer = IslamicEmeraldDark,
    secondary = IslamicGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFAF2E1),
    onSecondaryContainer = IslamicGoldDark,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
