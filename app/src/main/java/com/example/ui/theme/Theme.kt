package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BaseShieldColorScheme = darkColorScheme(
  primary = BaseBlue,
  onPrimary = Color.White,
  primaryContainer = BaseBlueDark,
  onPrimaryContainer = Color.White,
  secondary = CyberCyan,
  onSecondary = Color.Black,
  secondaryContainer = DarkSurfaceVariant,
  onSecondaryContainer = CyberCyan,
  tertiary = SafeGreen,
  onTertiary = Color.Black,
  background = DarkBackground,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  error = AlertRed,
  onError = Color.White,
  errorContainer = AlertRedBg,
  onErrorContainer = AlertRedBright,
  outline = DarkBorder,
  outlineVariant = DarkBorderSubtle
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force modern dark theme by default as specified
  dynamicColor: Boolean = false, // Keep intentional Base brand dark theme
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = BaseShieldColorScheme,
    typography = Typography,
    content = content
  )
}
