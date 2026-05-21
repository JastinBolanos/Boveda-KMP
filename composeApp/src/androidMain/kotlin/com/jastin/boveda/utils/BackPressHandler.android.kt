package com.jastin.boveda.utils

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

/*
 * =========================================================================
 * INTERCEPTOR DE NAVEGACIÓN NATIVA (ANDROID)
 * =========================================================================
 * Resuelve el contrato de navegación definido en la capa compartida.
 * El motivo de esta abstracción es que el manejo del botón "Atrás" (físico
 * o por gestos del sistema) es un concepto exclusivo del ecosistema Android.
 * Al aislar esto, permitimos que la lógica compartida (commonMain) pueda
 * cerrar modales, menús o pantallas sin acoplarse a librerías específicas de Google.
 */
@Composable
actual fun BackPressHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled = enabled, onBack = onBack)
}