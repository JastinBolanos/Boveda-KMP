package com.jastin.boveda.utils

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

/* =========================================================================
 * INTERCEPTOR DE NAVEGACIÓN (ANDROID)
 * Implementa la lógica nativa para el manejo del botón "Atrás" (gestos/físico):
 * * Abstracción KMP: Desacopla la lógica de navegación compartida de las
 * APIs específicas de Android.
 * * Control de Estado: Permite gestionar el cierre de modales y menús desde
 * commonMain sin dependencias directas de librerías de terceros.
 * ========================================================================= */
@Composable
actual fun BackPressHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled = enabled, onBack = onBack)
}