package com.jastin.boveda.utils

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

/* =========================================================================
 * NAVIGATION INTERCEPTOR (ANDROID)
 * Implements native logic for handling the "Back" button (gestures/physical):
 * * KMP Abstraction: Decouples shared navigation logic from Android-specific APIs.
 * * State Control: Allows managing the closing of modals and menus from
 * commonMain without direct dependencies on third-party libraries.
 * ========================================================================= */
@Composable
actual fun BackPressHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled = enabled, onBack = onBack)
}