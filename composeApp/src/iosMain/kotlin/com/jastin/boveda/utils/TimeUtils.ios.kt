package com.jastin.boveda.utils

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.timeIntervalSince1970

/* Actual implementation: iOS system time capture. */

// --- 1. CAPTURA DE TIEMPO NATIVO ---
// ! Precisión: NSDate retorna segundos (Double); convertimos a milisegundos (Long) para estandarizar con commonMain.
actual fun getCurrentTimeMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}

actual fun getCurrentTimeFormatted(): String {
    val formatter = NSDateFormatter()
    // SEGURO: "en_US_POSIX" es el estándar de Apple para evitar que
    // los ajustes de 12/24 hrs del iPhone sobreescriban tu formato.
    formatter.locale = NSLocale("en_US_POSIX")
    formatter.dateFormat = "HH:mm"
    return formatter.stringFromDate(NSDate())
}

actual fun getCurrentDateFormatted(): String {
    return "Hoy"
}