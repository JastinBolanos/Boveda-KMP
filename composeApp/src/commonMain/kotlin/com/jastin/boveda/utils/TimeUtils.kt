package com.jastin.boveda.utils

/* =========================================================================
 * CONTRATO DE TIEMPO DEL SISTEMA (EXPECT)
 * Abstracción ligera para captura de timestamps.
 * Decisión Técnica: Se prescinde de librerías externas pesadas (como kotlinx.datetime)
 * para reducir el tamaño del binario y delegar la precisión directamente al
 * motor nativo del hardware (iOS/Android).
 * ========================================================================= */
expect fun getCurrentTimeMillis(): Long