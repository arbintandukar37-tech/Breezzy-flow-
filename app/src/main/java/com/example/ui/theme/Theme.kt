package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BreezyLightColorScheme = lightColorScheme(
  primary = BreezySky,
  onPrimary = Color.White,
  primaryContainer = BreezySkyLight,
  onPrimaryContainer = BreezySky,
  secondary = BreezyMint,
  onSecondary = Color.White,
  secondaryContainer = BreezyMintLight,
  onSecondaryContainer = BreezyMint,
  tertiary = BreezyAmber,
  onTertiary = Color.White,
  background = BreezyBg,
  onBackground = TextPrimary,
  surface = BreezySurface,
  onSurface = TextPrimary,
  surfaceVariant = BreezySurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = BreezyBorder,
  error = BreezySunset,
  onError = Color.White
)

private val BreezyDarkColorScheme = darkColorScheme(
  primary = BreezyCyan,
  onPrimary = Color.Black,
  primaryContainer = BreezySky,
  onPrimaryContainer = Color.White,
  secondary = BreezyMint,
  onSecondary = Color.Black,
  background = Color(0xFF0F172A),
  onBackground = Color(0xFFF8FAFC),
  surface = Color(0xFF1E293B),
  onSurface = Color(0xFFF8FAFC),
  surfaceVariant = Color(0xFF334155),
  onSurfaceVariant = Color(0xFF94A3B8),
  outline = Color(0xFF475569),
  error = BreezySunset,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Set to light and vibrant by default per user prompt!
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) BreezyDarkColorScheme else BreezyLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
