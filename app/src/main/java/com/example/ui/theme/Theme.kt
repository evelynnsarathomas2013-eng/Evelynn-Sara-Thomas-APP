package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColorScheme =
  lightColorScheme(
    primary = KidsCyanPrimary,
    onPrimary = Color.White,
    secondary = KidsBlueSecondary,
    onSecondary = Color.White,
    tertiary = KidsAmberAccent,
    onTertiary = Color.White,
    background = SoftSkyBackground,
    onBackground = Color(0xFF000000),
    surface = Color.White,
    onSurface = Color(0xFF000000),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF111827),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0)
  )

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = AppColorScheme,
    typography = Typography,
    content = content
  )
}

