package com.jastin.boveda

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.jastin.boveda.database.BovedaDatabase
import com.jastin.boveda.database.DatabaseDriverFactory
import com.jastin.boveda.data.repository.SqlDelightTransactionRepository
import com.jastin.boveda.domain.repository.TransactionRepository
import com.jastin.boveda.presentation.screens.main.MainScreen
import com.jastin.boveda.presentation.theme.BovedaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/* =========================================================================
 * PUNTO DE ENTRADA COMPARTIDO (COMPOSITION ROOT)
 * Inicializa la inyección de dependencias global, el sistema de diseño unificado
 * y monta el árbol de navegación base (Voyager).
 * ========================================================================= */

// --- 1. SERVICE LOCATOR (MANUAL DI) ---
// ¡MINA TERRESTRE! Usamos lateinit para inyectar el repositorio globalmente.
// Esto simplifica la arquitectura al evitar frameworks pesados como Koin en etapas
// tempranas, pero exige que la inicialización ocurra estrictamente antes del primer render.
lateinit var globalTransactionRepository: TransactionRepository

@Composable
fun App(driverFactory: DatabaseDriverFactory) {

    // --- 2. BOOTSTRAP DE BASE DE DATOS ---
    if (!::globalTransactionRepository.isInitialized) {
        val driver = driverFactory.createDriver()
        val database = BovedaDatabase(driver)
        globalTransactionRepository = SqlDelightTransactionRepository(
            database = database,
            scope = CoroutineScope(Dispatchers.Default)
        )
    }

    BovedaTheme {
        // --- 3. ROOT NAVIGATOR ---
        // ¡CRÍTICO! Se inicializa el Navigator de Voyager de forma pura (sin SlideTransition).
        // Envolver la raíz en animaciones de transición globales intercepta el ciclo de vida nativo;
        // si un Tab purga su stack interno, la animación intentará dibujar una vista
        // ya liberada de memoria, causando un crash fatal ('State is DESTROYED').
        Navigator(MainScreen())
    }
}