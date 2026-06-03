package com.jastin.boveda.database

import app.cash.sqldelight.db.SqlDriver

/* =========================================================================
 * CONTRATO DE DRIVER DE BASE DE DATOS (EXPECT)
 * Abstracción para inicializar el motor SQLite. Permite que la capa
 * compartida (commonMain) gestione la persistencia sin acoplarse a las
 * restricciones de almacenamiento de Android o iOS.
 * ========================================================================= */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}