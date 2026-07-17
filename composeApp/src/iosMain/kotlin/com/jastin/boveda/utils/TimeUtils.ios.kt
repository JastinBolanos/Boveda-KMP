package com.jastin.boveda.utils

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.timeIntervalSince1970

/* Actual implementation: iOS system time capture. */

// --- 1. NATIVE TIME CAPTURE ---
// ! Precision: NSDate returns seconds (Double); we convert to milliseconds (Long) to standardize with commonMain.
actual fun getCurrentTimeMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}

actual fun getCurrentTimeFormatted(): String {
    val formatter = NSDateFormatter()
    // SAFE: "en_US_POSIX" is the Apple standard to prevent
    // the iPhone's 12/24 hr settings from overriding your format.
    formatter.locale = NSLocale("en_US_POSIX")
    formatter.dateFormat = "HH:mm"
    return formatter.stringFromDate(NSDate())
}

actual fun getCurrentDateFormatted(): String {
    return "Today"
}