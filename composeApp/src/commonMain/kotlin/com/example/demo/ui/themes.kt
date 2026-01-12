package com.example.demo.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors: ColorScheme = lightColorScheme(
    primary = Color(0xFF3B82F6),
    onPrimary = Color.White,

    secondary = Color(0xFF10B981),
    onSecondary = Color.White,

    background = Color(0xFFF7F7F8),
    onBackground = Color(0xFF111827),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111827),

    surfaceVariant = Color(0xFFE5E7EB),
    onSurfaceVariant = Color(0xFF374151),

    outline = Color(0xFFCBD5E1),
    error = Color(0xFFDC2626),
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Color(0xFF0B1220),

    secondary = Color(0xFF34D399),
    onSecondary = Color(0xFF052014),

    background = Color(0xFF0B0F19),
    onBackground = Color(0xFFE5E7EB),

    surface = Color(0xFF0F172A),
    onSurface = Color(0xFFE5E7EB),

    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFFCBD5E1),

    outline = Color(0xFF334155),
    error = Color(0xFFF87171),
)

@Composable
fun EnglishFixerTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val scheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = scheme,
        content = content
    )
}
