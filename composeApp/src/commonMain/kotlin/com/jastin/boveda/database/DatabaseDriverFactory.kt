package com.jastin.boveda.database

import app.cash.sqldelight.db.SqlDriver

/*
 * =========================================================================
 * CONTRATO DE DRIVER DE BASE DE DATOS (EXPECT)
 * =========================================================================
 * Define la abstracción para la inicialización del motor SQLite.
 * Al usar el patrón 'expect/actual' nativo de KMP, garantizamos que la lógica
 * de negocio compartida (commonMain) pueda operar la base de datos sin acoplarse
 * a las restricciones de almacenamiento de archivos de iOS (Foundation) o
 * Android (Context).
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}