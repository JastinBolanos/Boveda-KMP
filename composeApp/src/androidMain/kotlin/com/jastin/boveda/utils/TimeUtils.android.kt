package com.jastin.boveda.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/* =========================================================================
 * PROVEEDOR DE TIEMPO (ANDROID / JVM)
 * Implementa la captura de tiempo nativa para la plataforma Android:
 * * Abstracción (Expect/Actual): Evita colisiones de nombres de librerías
 * y reduce la huella del binario.
 * * Optimización: Accede directamente al reloj del sistema, garantizando
 * latencia mínima y eliminando dependencias externas innecesarias.
 * ========================================================================= */
actual fun getCurrentTimeMillis(): Long {
    return System.currentTimeMillis()
}

actual fun getCurrentTimeFormatted(): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(Date())
}

actual fun getCurrentDateFormatted(): String {
    return "Hoy"
}