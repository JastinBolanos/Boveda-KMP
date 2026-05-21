package com.jastin.boveda.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/* =========================================================================
 * MATERIAL THEME WRAPPER (SISTEMA GLOBAL DE DISEÑO)
 * Conecta nuestros 'Tokens de Color' crudos con el motor de Jetpack Compose.
 * Al mapear nuestros colores a roles semánticos (Primary, Background, Surface),
 * permitimos que los componentes estándar de Material3 reaccionen automáticamente
 * a nuestra identidad visual de marca sin tener que estilizarlos uno por uno.
 * ========================================================================= */
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