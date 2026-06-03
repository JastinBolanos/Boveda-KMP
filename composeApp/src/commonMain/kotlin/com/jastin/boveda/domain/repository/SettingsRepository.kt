package com.jastin.boveda.domain.repository

/* =========================================================================
 * REPOSITORIO DE AJUSTES (REPOSITORY PATTERN)
 * Define el contrato para persistencia de configuraciones globales,
 * desacoplando la capa de dominio de la infraestructura de almacenamiento.
 * ========================================================================= */
interface SettingsRepository {
    fun updateThemePreference(isDark: Boolean)
}