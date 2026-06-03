package com.jastin.boveda.utils

/* =========================================================================
 * CONTRATO DE TIEMPO (EXPECT)
 * Abstracción para captura de timestamps. Delegación nativa al motor
 * del hardware para minimizar el peso del binario y evitar dependencias.
 * ========================================================================= */
expect fun getCurrentTimeMillis(): Long
expect fun getCurrentTimeFormatted(): String
expect fun getCurrentDateFormatted(): String