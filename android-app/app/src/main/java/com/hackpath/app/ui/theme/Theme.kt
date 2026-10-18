package com.hackpath.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Hacker palette
val HackGreen   = Color(0xFF00FF41)
val HackDark    = Color(0xFF0D0D0D)
val HackSurface = Color(0xFF1A1A1A)
val HackCard    = Color(0xFF222222)
val HackRed     = Color(0xFFFF4444)
val HackBlue    = Color(0xFF4FC3F7)
val HackYellow  = Color(0xFFFFD700)
val HackOrange  = Color(0xFFFF8C00)

// RPG gothic palette
val RPGPurple   = Color(0xFF7B2FBE)
val RPGGold     = Color(0xFFFFD700)
val RPGBlood    = Color(0xFFCC2200)
val RPGShadow   = Color(0xFF0A0A14)
val RPGMist     = Color(0xFF1A1428)
val RPGAncient  = Color(0xFF2A2040)

private val DarkColorScheme = darkColorScheme(
    primary          = HackGreen,
    onPrimary        = Color.Black,
    secondary        = RPGPurple,
    onSecondary      = Color.White,
    tertiary         = RPGGold,
    background       = RPGShadow,
    onBackground     = Color.White,
    surface          = RPGMist,
    onSurface        = Color.White,
    surfaceVariant   = RPGAncient,
    onSurfaceVariant = Color(0xFFCCCCCC),
    error            = HackRed,
    outline          = Color(0xFF333344)
)

@Composable
fun HackPathTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = HackTypography,
        content     = content
    )
}
