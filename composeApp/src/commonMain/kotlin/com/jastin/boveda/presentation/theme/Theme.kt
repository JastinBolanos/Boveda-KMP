package com.jastin.boveda.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BovedaColorScheme = lightColorScheme(
    primary = Emerald500,
    onPrimary = Slate950,
    secondary = Teal400,
    background = Slate50,
    surface = Color.White,
    onSurface = Slate900
)

@Composable
fun BovedaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BovedaColorScheme,
        content = content
    )
}