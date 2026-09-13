package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

enum class AppThemeStyle(val displayName: String, val icon: String, val subtitle: String, val isDark: Boolean) {
    BREEZY_LIGHT("Breezy Light", "☀️", "Clean white + blue + gold", false),
    NEON_QUEST("Neon Quest", "🌙", "Obsidian + cyan + purple + gold", true),
    SAKURA_BREEZE("Sakura Breeze", "🌸", "Pink + lavender + cream", false),
    FOREST_FOCUS("Forest Focus", "🌲", "Forest green + emerald + gold", true),
    SUNSET_ENERGY("Sunset Energy", "⚡", "Purple + orange + coral", true)
}

val SakuraBreezyPalette = BreezyPalette(
    isDark = false,
    background = Color(0xFFFFF1F2),
    surface = Color(0xFFFFFFFF),
    cardSurface = Color(0xFFFFE4E6),
    cardSurfaceVariant = Color(0xFFFCE7F3),
    primary = Color(0xFFEC4899),
    primaryGlow = Color(0xFFF472B6),
    secondary = Color(0xFFA855F7),
    secondaryGlow = Color(0xFFC084FC),
    tertiary = Color(0xFF10B981),
    error = Color(0xFFE11D48),
    purple = Color(0xFFA855F7),
    blue = Color(0xFF0284C7),
    lightBlue = Color(0xFF0284C7),
    lightBlueGlow = Color(0xFF38BDF8),
    lightPink = Color(0xFFEC4899),
    lightPinkContainer = Color(0xFFFFD1DC),
    textPrimary = Color(0xFF881337),
    textSecondary = Color(0xFF9F1239),
    textMuted = Color(0xFFBE123C),
    borderSubtle = Color(0xFFFECDD3),
    divider = Color(0xFFFECDD3)
)

val ForestBreezyPalette = BreezyPalette(
    isDark = true,
    background = Color(0xFF061A14),
    surface = Color(0xFF0D281E),
    cardSurface = Color(0xFF16382C),
    cardSurfaceVariant = Color(0xFF1F4739),
    primary = Color(0xFF10B981),
    primaryGlow = Color(0xFF34D399),
    secondary = Color(0xFFF59E0B),
    secondaryGlow = Color(0xFFFBBF24),
    tertiary = Color(0xFF34D399),
    error = Color(0xFFF43F5E),
    purple = Color(0xFF8B5CF6),
    blue = Color(0xFF06B6D4),
    lightBlue = Color(0xFF34D399),
    lightBlueGlow = Color(0xFF6EE7B7),
    lightPink = Color(0xFFF472B6),
    lightPinkContainer = Color(0xFF064E3B),
    textPrimary = Color(0xFFECFDF5),
    textSecondary = Color(0xFFA7F3D0),
    textMuted = Color(0xFF6EE7B7),
    borderSubtle = Color(0xFF1F4739),
    divider = Color(0xFF16382C)
)

val SunsetBreezyPalette = BreezyPalette(
    isDark = true,
    background = Color(0xFF180E29),
    surface = Color(0xFF23143B),
    cardSurface = Color(0xFF301B50),
    cardSurfaceVariant = Color(0xFF3F2366),
    primary = Color(0xFFF97316),
    primaryGlow = Color(0xFFFB923C),
    secondary = Color(0xFFFBBF24),
    secondaryGlow = Color(0xFFFCD34D),
    tertiary = Color(0xFF10B981),
    error = Color(0xFFEF4444),
    purple = Color(0xFFA855F7),
    blue = Color(0xFF3B82F6),
    lightBlue = Color(0xFFFB923C),
    lightBlueGlow = Color(0xFFFDBA74),
    lightPink = Color(0xFFFB7185),
    lightPinkContainer = Color(0xFF4C0519),
    textPrimary = Color(0xFFFFF1F2),
    textSecondary = Color(0xFFFBCFE8),
    textMuted = Color(0xFFE879F9),
    borderSubtle = Color(0xFF3F2366),
    divider = Color(0xFF301B50)
)

@Composable
fun BreezyQuestTheme(
    darkTheme: Boolean = true,
    appThemeStyle: AppThemeStyle = AppThemeStyle.NEON_QUEST,
    content: @Composable () -> Unit
) {
    val palette = if (!darkTheme) {
        // Light mode: always use light palette
        when (appThemeStyle) {
            AppThemeStyle.SAKURA_BREEZE -> SakuraBreezyPalette.copy(isDark = false)
            else -> LightBreezyPalette
        }
    } else {
        // Dark mode: use the selected dark theme style
        when (appThemeStyle) {
            AppThemeStyle.BREEZY_LIGHT -> LightBreezyPalette
            AppThemeStyle.NEON_QUEST -> DarkBreezyPalette
            AppThemeStyle.SAKURA_BREEZE -> SakuraBreezyPalette
            AppThemeStyle.FOREST_FOCUS -> ForestBreezyPalette
            AppThemeStyle.SUNSET_ENERGY -> SunsetBreezyPalette
        }
    }

    val colorScheme = if (palette.isDark) {
        darkColorScheme(
            primary = palette.primary,
            onPrimary = if (palette.isDark) Color.Black else Color.White,
            primaryContainer = palette.cardSurfaceVariant,
            onPrimaryContainer = palette.primaryGlow,
            secondary = palette.secondary,
            onSecondary = Color.Black,
            secondaryContainer = palette.cardSurfaceVariant,
            onSecondaryContainer = palette.secondaryGlow,
            tertiary = palette.tertiary,
            onTertiary = Color.Black,
            background = palette.background,
            onBackground = palette.textPrimary,
            surface = palette.surface,
            onSurface = palette.textPrimary,
            surfaceVariant = palette.cardSurface,
            onSurfaceVariant = palette.textSecondary,
            error = palette.error,
            onError = Color.White
        )
    } else {
        lightColorScheme(
            primary = palette.primary,
            onPrimary = Color.White,
            primaryContainer = palette.cardSurfaceVariant,
            onPrimaryContainer = palette.primary,
            secondary = palette.secondary,
            onSecondary = Color.White,
            secondaryContainer = palette.cardSurfaceVariant,
            onSecondaryContainer = palette.secondary,
            tertiary = palette.tertiary,
            onTertiary = Color.White,
            background = palette.background,
            onBackground = palette.textPrimary,
            surface = palette.surface,
            onSurface = palette.textPrimary,
            surfaceVariant = palette.cardSurface,
            onSurfaceVariant = palette.textSecondary,
            error = palette.error,
            onError = Color.White
        )
    }

    CompositionLocalProvider(LocalBreezyPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    BreezyQuestTheme(darkTheme = darkTheme, content = content)
}

