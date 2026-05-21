package com.jastin.boveda.utils

import androidx.compose.runtime.Composable

/* =========================================================================
 * CONTRATO DE INTERCEPTOR DE NAVEGACIÓN (EXPECT)
 * Define la abstracción para el manejo del botón "Atrás" físico o por gestos.
 * Al aislarlo, permitimos que commonMain defina reglas de salida seguras
 * sin acoplarse al ecosistema específico de Android (BackHandler).
 * ========================================================================= */
@Composable
expect fun BackPressHandler(enabled: Boolean = true, onBack: () -> Unit)