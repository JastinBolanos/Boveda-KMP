package com.jastin.boveda.utils

import androidx.compose.runtime.Composable

/* =========================================================================
 * INTERCEPTOR DE RETROCESO (IOS ACTUAL)
 * Implementación de la plataforma iOS para el manejo del gesto "Swipe-to-back".
 * ========================================================================= */

// --- 1. GESTIÓN NATIVA DE GESTOS ---
@Composable
actual fun BackPressHandler(enabled: Boolean, onBack: () -> Unit) {
    // No-Op. La navegación nativa de iOS maneja los gestos de retroceso.
}