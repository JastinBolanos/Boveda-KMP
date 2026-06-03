package com.jastin.boveda.data.repository

import com.jastin.boveda.database.BovedaDatabase
import com.jastin.boveda.domain.repository.SettingsRepository

/* =========================================================================
 * REPOSITORIO DE AJUSTES (SQLDELIGHT)
 * Gestiona la persistencia de configuraciones globales:
 * * Escritura Síncrona: Asegura el guardado inmediato mediante transacciones.
 * * Tolerancia a Fallos: Silencia errores de I/O para garantizar la
 * continuidad del arranque de la UI.
 * ========================================================================= */
class SqlDelightSettingsRepository(
    private val database: BovedaDatabase
) : SettingsRepository {

    private val queries = database.appConfigQueries

    init {
        try {
            val savedTheme = queries.getThemePreference().executeAsOneOrNull()
            if (savedTheme != null) {
                com.jastin.boveda.globalIsDarkMode.value = savedTheme == 1L
            }
        } catch (e: Exception) {
        }
    }

    override fun updateThemePreference(isDark: Boolean) {
        com.jastin.boveda.globalIsDarkMode.value = isDark
        try {
            database.transaction {
                queries.saveThemePreference(if (isDark) 1L else 0L)
            }
        } catch (e: Exception) {
        }
    }
}