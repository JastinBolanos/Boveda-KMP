package com.jastin.boveda.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*
 * =========================================================================
 * TIEMPO DEL SISTEMA NATIVO (ANDROID / JVM)
 * =========================================================================
 * Implementación de la captura de tiempo en la plataforma Android.
 * La decisión arquitectónica de crear este puente (expect/actual) en lugar
 * de usar librerías compartidas (como kotlinx.datetime) evita colisiones
 * de nombres en el compilador ('Name Clash') y garantiza acceso directo,
 * rápido y sin dependencias externas al reloj interno del dispositivo.
 */
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