package com.jastin.boveda.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

/* =========================================================================
 * DRIVER DE BASE DE DATOS (ANDROID)
 * Implementa la inicialización física del motor SQLite delegando al SO:
 * * Abstracción KMP: Resuelve la dependencia del sistema de archivos,
 * aislando la lógica compartida del entorno nativo.
 * * Seguridad/Sandbox: Utiliza el [Context] para garantizar la creación
 * del archivo .db dentro del espacio aislado de la aplicación.
 * ========================================================================= */
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = BovedaDatabase.Schema,
            context = context,
            name = "boveda_local.db"
        )
    }
}