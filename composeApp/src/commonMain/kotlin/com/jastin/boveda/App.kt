package com.jastin.boveda

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.flow.MutableStateFlow

/* =========================================================================
 * COMPOSITION ROOT
 * Inicializa dependencias (DI manual), tema global y navegación (Voyager).
 * ========================================================================= */

// --- 1. SERVICE LOCATOR ---
// NOTA: Se emplea `lateinit` como solución temporal para inyección de
// dependencias manual. Debe inicializarse estrictamente antes del primer
// ciclo de renderizado de la aplicación.

lateinit var globalTransactionRepository: TransactionRepository
lateinit var globalSettingsRepository: SettingsRepository

val globalIsDarkMode = MutableStateFlow(false)

@Composable
fun App(driverFactory: DatabaseDriverFactory) {

    // --- 2. BOOTSTRAP DE BASE DE DATOS ---
    if (!::globalTransactionRepository.isInitialized) {
        val driver = driverFactory.createDriver()
        val database = BovedaDatabase(driver)
        val appScope = CoroutineScope(Dispatchers.Default)

        globalTransactionRepository = SqlDelightTransactionRepository(
            database = database,
            scope = appScope
        )

        globalSettingsRepository = SqlDelightSettingsRepository(
            database = database,
        )
    }

    val isDarkMode by globalIsDarkMode.collectAsState()

    BovedaTheme(isDarkTheme = isDarkMode) {
        // --- 3. ROOT NAVIGATOR ---
        Navigator(MainScreen())
    }
}