package com.lurkbd.agent.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LurkColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = SandSurface,
    primaryContainer = TealSoft,
    onPrimaryContainer = SandSurface,
    secondary = GoldAccent,
    onSecondary = Ink,
    secondaryContainer = GoldSoft,
    onSecondaryContainer = Ink,
    background = SandSurface,
    onBackground = Ink,
    surface = SandCard,
    onSurface = Ink,
    surfaceVariant = SandSurface,
    onSurfaceVariant = InkMuted,
    error = Danger,
    onError = SandSurface
)

@Composable
fun LurkBDTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LurkColorScheme,
        typography = LurkTypography,
        content = content
    )
}
