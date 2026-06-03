package com.jastin.boveda.utils

import androidx.compose.runtime.Composable

/* =========================================================================
 * CONTRATO DE INTERCEPTOR DE NAVEGACIÓN (EXPECT)
 * Abstracción para el manejo del gesto "Atrás". Permite definir reglas
 * de salida en commonMain sin acoplamientos a la API nativa de Android.
 * ========================================================================= */
@Composable
expect fun BackPressHandler(enabled: Boolean = true, onBack: () -> Unit)