package com.jastin.boveda.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

/* =========================================================================
 * IMPLEMENTACIÓN DE DRIVER (IOS / NATIVE)
 * Provee el motor SQLite específico para el entorno nativo de Apple.
 * ========================================================================= */

actual class DatabaseDriverFactory {

    // --- 1. CONFIGURACIÓN DEL MOTOR NATIVO ---
    // A diferencia de Android, iOS no requiere un 'Context' para acceder
    // al sistema de archivos local. Accedemos directamente a las APIs de
    // SQLite de Apple mediante 'NativeSqliteDriver'.
    actual fun createDriver(): SqlDriver {
        // ¡CRÍTICO! 'BovedaDatabase.Schema' es generado automáticamente por SQLDelight.
        // Si modificas el archivo .sq, el driver gestionará las migraciones
        // de base de datos de forma atómica en el primer acceso a la app.
        return NativeSqliteDriver(BovedaDatabase.Schema, "boveda_local.db")
    }
}