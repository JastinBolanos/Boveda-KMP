package com.jastin.boveda.utils

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

/* Actual implementation: iOS system time capture. */

// --- 1. CAPTURA DE TIEMPO NATIVO ---
// ! Precisión: NSDate retorna segundos (Double); convertimos a milisegundos (Long) para estandarizar con commonMain.
actual fun getCurrentTimeMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}