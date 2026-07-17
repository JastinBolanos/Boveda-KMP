package com.jastin.boveda.utils

import androidx.compose.runtime.Composable

/* =========================================================================
* NAVIGATION INTERCEPTOR CONTRACT (EXPECT)
 * Abstraction for handling the "Back" gesture. Allows defining exit rules
 * in commonMain without coupling to the native Android API.
 * ========================================================================= */
@Composable
expect fun BackPressHandler(enabled: Boolean = true, onBack: () -> Unit)