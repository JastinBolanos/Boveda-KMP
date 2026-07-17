package com.jastin.boveda

import platform.UIKit.UIDevice

/* =========================================================================
 * PLATFORM IDENTIFIER (IOS)
 * ========================================================================= */
class IOSPlatform: Platform {

    // --- 1. SYSTEM METADATA ---
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()