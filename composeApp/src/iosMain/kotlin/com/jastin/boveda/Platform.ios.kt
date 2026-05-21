package com.jastin.boveda

import platform.UIKit.UIDevice

/* =========================================================================
 * IDENTIFICADOR DE PLATAFORMA (IOS)
 * ========================================================================= */
class IOSPlatform: Platform {

    // --- 1. METADATOS DEL SISTEMA ---
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()