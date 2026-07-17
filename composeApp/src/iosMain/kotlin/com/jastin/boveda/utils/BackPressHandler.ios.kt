package com.jastin.boveda.utils

import androidx.compose.runtime.Composable

/* =========================================================================
 * BACK INTERCEPTOR (IOS ACTUAL)
 * iOS platform implementation for handling the "Swipe-to-back" gesture.
 * ========================================================================= */

// --- 1. NATIVE GESTURE MANAGEMENT ---
@Composable
actual fun BackPressHandler(enabled: Boolean, onBack: () -> Unit) {
    // No-Op. iOS native navigation handles back gestures.
}