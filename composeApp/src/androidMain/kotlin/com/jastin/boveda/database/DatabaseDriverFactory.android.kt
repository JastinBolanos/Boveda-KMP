package com.jastin.boveda.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

/*
 * =========================================================================
 * INYECCIÓN NATIVA DE BASE DE DATOS (ANDROID)
 * =========================================================================
 * Esta clase resuelve el contrato (expect) definido en commonMain.
 * El motivo de aislar esta capa es puramente arquitectónico: la lógica de
 * Kotlin Multiplatform desconoce el sistema de archivos del dispositivo,
 * por lo que delegamos la inicialización física de SQLite al SO nativo.
 * * Se inyecta el [Context] porque Android lo exige como medida de seguridad
 * para ubicar y construir el archivo".db" estrictamente dentro del
 * almacenamiento aislado (sandbox) de la aplicación.
 */
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = BovedaDatabase.Schema,
            context = context,
            name = "boveda_local.db"
        )
    }
}