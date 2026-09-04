package com.eliranrp.score421.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CafeScheme = lightColorScheme(
    primary = Burgundy,
    onPrimary = ChipInk,
    secondary = Oak,
    onSecondary = ChipInk,
    background = Cream,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = ChipIdle,
    onSurfaceVariant = DarkOak,
    outline = Oak,
    error = StampRed,
)

@Composable
fun Score421Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CafeScheme,
        typography = CafeTypography,
        content = content,
    )
}
