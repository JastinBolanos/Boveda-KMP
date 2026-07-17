package com.jastin.boveda

/* =========================================================================
 * PLATFORM CONTRACT (EXPECT)
 * Abstraction to identify the host OS (Android/iOS) and execute
 * platform-specific logic without native coupling.
 * ========================================================================= */
interface Platform {
    val name: String
}

expect fun getPlatform(): Platform