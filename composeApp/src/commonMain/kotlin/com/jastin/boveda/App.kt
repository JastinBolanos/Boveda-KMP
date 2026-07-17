package com.jastin.boveda

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import com.jastin.boveda.data.repository.SqlDelightSettingsRepository
import com.jastin.boveda.database.BovedaDatabase
import com.jastin.boveda.database.DatabaseDriverFactory
import com.jastin.boveda.data.repository.SqlDelightTransactionRepository
import com.jastin.boveda.domain.repository.SettingsRepository
import com.jastin.boveda.domain.repository.TransactionRepository
import com.jastin.boveda.presentation.screens.main.MainScreen
import com.jastin.boveda.presentation.theme.BovedaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow

/* =========================================================================
 * COMPOSITION ROOT
 * Initializes dependencies (manual DI), global theme, and navigation (Voyager).
 * ========================================================================= */

// --- 1. SERVICE LOCATOR ---
// NOTE: `lateinit` is used as a temporary solution for manual dependency
// injection. It must be initialized strictly before the application's
// first render cycle.

lateinit var globalTransactionRepository: TransactionRepository
lateinit var globalSettingsRepository: SettingsRepository

val globalIsDarkMode = MutableStateFlow(false)

@Composable
fun App(driverFactory: DatabaseDriverFactory) {

    // --- 2. SECURED DATABASE BOOTSTRAP ---
    // We use remember to ensure thread safety during UI recompositions.
    remember {
        if (!::globalTransactionRepository.isInitialized) {
            val driver = driverFactory.createDriver()
            val database = BovedaDatabase(driver)

            // SupervisorJob ensures that a database failure does not permanently kill the scope.
            val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

            globalTransactionRepository = SqlDelightTransactionRepository(
                database = database,
                scope = appScope
            )

            globalSettingsRepository = SqlDelightSettingsRepository(
                database = database,
            )
        }
    }

    val isDarkMode by globalIsDarkMode.collectAsState()

    BovedaTheme(isDarkTheme = isDarkMode) {
        // --- 3. ROOT NAVIGATOR ---
        Navigator(MainScreen())
    }
}