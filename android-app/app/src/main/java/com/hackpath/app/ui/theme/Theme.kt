package com.hackpath.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val HackGreen   = Color(0xFF00FF41)
val HackDark    = Color(0xFF0D0D0D)
val HackSurface = Color(0xFF1A1A1A)
val HackCard    = Color(0xFF222222)
val HackRed     = Color(0xFFFF4444)
val HackBlue    = Color(0xFF4FC3F7)
val HackYellow  = Color(0xFFFFD700)
val HackOrange  = Color(0xFFFF8C00)

private val DarkColorScheme = darkColorScheme(
    primary          = HackGreen,
    onPrimary        = Color.Black,
    secondary        = HackBlue,
    onSecondary      = Color.Black,
    tertiary         = HackYellow,
    background       = HackDark,
    onBackground     = Color.White,
    surface          = HackSurface,
    onSurface        = Color.White,
    surfaceVariant   = HackCard,
    onSurfaceVariant = Color(0xFFCCCCCC),
    error            = HackRed,
    outline          = Color(0xFF333333)
)

@Composable
fun HackPathTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = HackTypography,
        content     = content
    )
}
