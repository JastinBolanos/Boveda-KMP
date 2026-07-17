package com.jastin.boveda.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/* =========================================================================
 * TIME PROVIDER (ANDROID / JVM)
 * Implements native time capture for the Android platform.
 * ========================================================================= */
actual fun getCurrentTimeMillis(): Long {
    return System.currentTimeMillis()
}

actual fun getCurrentTimeFormatted(): String {
    // SAFE: We use Locale.US to ensure that numbers are always
    // Western Arabic (0-9), regardless of the device's language.
    val formatter = SimpleDateFormat("HH:mm", Locale.US)
    return formatter.format(Date())
}

actual fun getCurrentDateFormatted(): String {
    return "Today"
}