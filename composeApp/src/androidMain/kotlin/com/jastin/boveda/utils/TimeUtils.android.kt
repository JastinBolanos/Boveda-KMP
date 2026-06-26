package com.jastin.boveda.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/* =========================================================================
 * PROVEEDOR DE TIEMPO (ANDROID / JVM)
 * Implementa la captura de tiempo nativa para la plataforma Android.
 * ========================================================================= */
actual fun getCurrentTimeMillis(): Long {
    return System.currentTimeMillis()
}

actual fun getCurrentTimeFormatted(): String {
    // SEGURO: Usamos Locale.US para garantizar que los números siempre
    // sean arábigos occidentales (0-9), sin importar el idioma del dispositivo.
    val formatter = SimpleDateFormat("HH:mm", Locale.US)
    return formatter.format(Date())
}

actual fun getCurrentDateFormatted(): String {
    return "Hoy"
}