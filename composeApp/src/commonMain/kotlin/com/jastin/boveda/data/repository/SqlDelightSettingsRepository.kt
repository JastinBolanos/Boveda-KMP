package com.jastin.boveda.data.repository

import com.jastin.boveda.database.BovedaDatabase
import com.jastin.boveda.domain.repository.SettingsRepository

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