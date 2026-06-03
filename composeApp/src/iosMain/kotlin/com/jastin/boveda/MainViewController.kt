package com.jastin.boveda

import androidx.compose.ui.window.ComposeUIViewController
import com.jastin.boveda.database.DatabaseDriverFactory

/* =========================================================================
 * MainViewController: Bridge entre UIKit (iOS) y Compose Multiplatform.
 * ========================================================================= */

// --- 1. ENTRY POINT DE IOS ---
// NOTA: Esta función es invocada por el 'ContentView' de SwiftUI/UIKit.
// Si la borras, la app no iniciará la UI.
fun MainViewController() = ComposeUIViewController {
    App(driverFactory = DatabaseDriverFactory())
}