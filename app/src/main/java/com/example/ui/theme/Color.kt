package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Raw Dark RPG Palette Constants
val RawDeepObsidian = Color(0xFF0A0E17)
val RawDarkSurface = Color(0xFF131B2A)
val RawDarkCardSurface = Color(0xFF1C2638)
val RawDarkCardSurfaceVariant = Color(0xFF243248)

val RawDarkCyan = Color(0xFF06B6D4)
val RawDarkCyanGlow = Color(0xFF22D3EE)
val RawDarkGold = Color(0xFFF59E0B)
val RawDarkGoldGlow = Color(0xFFFBBF24)

val RawDarkEmerald = Color(0xFF10B981)
val RawDarkRose = Color(0xFFF43F5E)
val RawDarkPurple = Color(0xFF8B5CF6)
val RawDarkBlue = Color(0xFF3B82F6)

val RawDarkTextPrimary = Color(0xFFF8FAFC)
val RawDarkTextSecondary = Color(0xFF94A3B8)
val RawDarkTextMuted = Color(0xFF64748B)

val RawDarkBorderSubtle = Color(0xFF334155)
val RawDarkDivider = Color(0xFF1E293B)

// Raw Vibrant Light Palette Constants (Light Blue, Light Pink, White, Grey)
val RawLightBackground = Color(0xFFF8FAFC) // Soft cool grey-white canvas
val RawLightSurface = Color(0xFFFFFFFF) // Crisp pure white surfaces
val RawLightCardSurface = Color(0xFFF1F5F9) // Cool light grey surface
val RawLightCardSurfaceVariant = Color(0xFFE2E8F0) // Refined medium-light grey

val RawLightBlue = Color(0xFF0284C7) // Vibrant light sky blue
val RawLightBlueGlow = Color(0xFF38BDF8) // Vibrant sky blue glow
val RawLightPink = Color(0xFFEC4899) // Vibrant light pink / rose blush
val RawLightPinkContainer = Color(0xFFFCE7F3) // Soft pastel pink container

val RawLightGold = Color(0xFFD97706) // Warm amber quest gold
val RawLightGoldGlow = Color(0xFFF59E0B)

val RawLightEmerald = Color(0xFF059669) // Emerald income
val RawLightRose = Color(0xFFE11D48) // Rose expense
val RawLightPurple = Color(0xFF7C3AED)
val RawLightBlueShield = Color(0xFF2563EB)

val RawLightTextPrimary = Color(0xFF0F172A) // Deep crisp slate navy
val RawLightTextSecondary = Color(0xFF475569) // Modern slate grey
val RawLightTextMuted = Color(0xFF94A3B8) // Muted slate grey

val RawLightBorderSubtle = Color(0xFFE2E8F0)
val RawLightDivider = Color(0xFFE2E8F0)

data class BreezyPalette(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val cardSurface: Color,
    val cardSurfaceVariant: Color,
    val primary: Color,
    val primaryGlow: Color,
    val secondary: Color,
    val secondaryGlow: Color,
    val tertiary: Color,
    val error: Color,
    val purple: Color,
    val blue: Color,
    val lightBlue: Color,
    val lightBlueGlow: Color,
    val lightPink: Color,
    val lightPinkContainer: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val borderSubtle: Color,
    val divider: Color
)

val DarkBreezyPalette = BreezyPalette(
    isDark = true,
    background = RawDeepObsidian,
    surface = RawDarkSurface,
    cardSurface = RawDarkCardSurface,
    cardSurfaceVariant = RawDarkCardSurfaceVariant,
    primary = RawDarkCyan,
    primaryGlow = RawDarkCyanGlow,
    secondary = RawDarkGold,
    secondaryGlow = RawDarkGoldGlow,
    tertiary = RawDarkEmerald,
    error = RawDarkRose,
    purple = RawDarkPurple,
    blue = RawDarkBlue,
    lightBlue = Color(0xFF38BDF8),
    lightBlueGlow = Color(0xFF7DD3FC),
    lightPink = Color(0xFFF472B6),
    lightPinkContainer = Color(0xFF831843),
    textPrimary = RawDarkTextPrimary,
    textSecondary = RawDarkTextSecondary,
    textMuted = RawDarkTextMuted,
    borderSubtle = RawDarkBorderSubtle,
    divider = RawDarkDivider
)

val LightBreezyPalette = BreezyPalette(
    isDark = false,
    background = RawLightBackground,
    surface = RawLightSurface,
    cardSurface = RawLightCardSurface,
    cardSurfaceVariant = RawLightCardSurfaceVariant,
    primary = RawLightBlue,
    primaryGlow = RawLightBlueGlow,
    secondary = RawLightGold,
    secondaryGlow = RawLightGoldGlow,
    tertiary = RawLightEmerald,
    error = RawLightRose,
    purple = RawLightPurple,
    blue = RawLightBlueShield,
    lightBlue = RawLightBlue,
    lightBlueGlow = RawLightBlueGlow,
    lightPink = RawLightPink,
    lightPinkContainer = RawLightPinkContainer,
    textPrimary = RawLightTextPrimary,
    textSecondary = RawLightTextSecondary,
    textMuted = RawLightTextMuted,
    borderSubtle = RawLightBorderSubtle,
    divider = RawLightDivider
)

val LocalBreezyPalette = staticCompositionLocalOf { DarkBreezyPalette }

// Composable Getters to provide full reactive theme switching to all call sites
val DeepObsidian: Color @Composable get() = LocalBreezyPalette.current.background
val DarkSurface: Color @Composable get() = LocalBreezyPalette.current.surface
val CardSurface: Color @Composable get() = LocalBreezyPalette.current.cardSurface
val CardSurfaceVariant: Color @Composable get() = LocalBreezyPalette.current.cardSurfaceVariant

val BreezeCyan: Color @Composable get() = LocalBreezyPalette.current.primary
val BreezeCyanGlow: Color @Composable get() = LocalBreezyPalette.current.primaryGlow
val QuestGold: Color @Composable get() = LocalBreezyPalette.current.secondary
val QuestGoldGlow: Color @Composable get() = LocalBreezyPalette.current.secondaryGlow

val EmeraldIncome: Color @Composable get() = LocalBreezyPalette.current.tertiary
val RoseExpense: Color @Composable get() = LocalBreezyPalette.current.error
val PurpleMagic: Color @Composable get() = LocalBreezyPalette.current.purple
val BlueShield: Color @Composable get() = LocalBreezyPalette.current.blue

// Vibrant Light Mode additions: Light Blue, Light Pink, White, Grey
val VibrantLightBlue: Color @Composable get() = LocalBreezyPalette.current.lightBlue
val VibrantLightBlueGlow: Color @Composable get() = LocalBreezyPalette.current.lightBlueGlow
val VibrantLightPink: Color @Composable get() = LocalBreezyPalette.current.lightPink
val VibrantLightPinkContainer: Color @Composable get() = LocalBreezyPalette.current.lightPinkContainer

val TextPrimary: Color @Composable get() = LocalBreezyPalette.current.textPrimary
val TextSecondary: Color @Composable get() = LocalBreezyPalette.current.textSecondary
val TextMuted: Color @Composable get() = LocalBreezyPalette.current.textMuted

val BorderSubtle: Color @Composable get() = LocalBreezyPalette.current.borderSubtle
val DividerDark: Color @Composable get() = LocalBreezyPalette.current.divider

val ButtonTextOnPrimary: Color @Composable get() = if (LocalBreezyPalette.current.isDark) Color.Black else Color.White

