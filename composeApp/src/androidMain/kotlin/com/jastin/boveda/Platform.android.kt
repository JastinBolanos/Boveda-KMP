package com.jastin.boveda

import android.os.Build

/* =========================================================================
 * PLATFORM IDENTIFIER (ANDROID)
 * 'actual' implementation that extracts system metadata (e.g., SDK Version):
 * * Abstraction: Exposes OS information to the shared layer in an agnostic way.
 * * Encapsulation: Allows platform-based conditional logic without
 * polluting the domain with direct [android.os] dependencies.
 * ========================================================================= */
class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()