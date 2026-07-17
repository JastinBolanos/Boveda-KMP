package com.jastin.boveda

import androidx.compose.ui.window.ComposeUIViewController
import com.jastin.boveda.database.DatabaseDriverFactory

/* =========================================================================
 * MainViewController: Bridge between UIKit (iOS) and Compose Multiplatform.
 * ========================================================================= */

// --- 1. IOS ENTRY POINT ---
// NOTE: This function is invoked by the 'ContentView' of SwiftUI/UIKit.
// If you delete it, the app will not launch the UI.
fun MainViewController() = ComposeUIViewController {
    App(driverFactory = DatabaseDriverFactory())
}