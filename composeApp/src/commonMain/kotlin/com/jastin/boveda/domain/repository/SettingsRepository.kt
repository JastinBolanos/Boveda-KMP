package com.jastin.boveda.domain.repository

interface SettingsRepository {
    fun updateThemePreference(isDark: Boolean)
}