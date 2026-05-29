package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = CorporateGold,
    onPrimary = RichBlack,
    secondary = NeonTeal,
    onSecondary = RichBlack,
    tertiary = SuccessGreen,
    background = RichBlack,
    onBackground = TextLight,
    surface = DeepCharcoal,
    onSurface = TextLight,
    surfaceVariant = SoftCardGray,
    onSurfaceVariant = TextMuted,
    error = AlertRed
  )

private val LightColorScheme = DarkColorScheme

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}
