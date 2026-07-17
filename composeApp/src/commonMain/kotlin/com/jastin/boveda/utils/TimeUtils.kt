package com.jastin.boveda.utils

/* =========================================================================
* TIME CONTRACT (EXPECT)
 * Abstraction for timestamp capture. Native delegation to the hardware
 * engine to minimize binary size and avoid dependencies.
 * ========================================================================= */
expect fun getCurrentTimeMillis(): Long
expect fun getCurrentTimeFormatted(): String
expect fun getCurrentDateFormatted(): String