package com.jastin.boveda.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

/* =========================================================================
 * DATABASE DRIVER (ANDROID)
 * Implements the physical initialization of the SQLite engine, delegating to the OS:
 * * KMP Abstraction: Resolves the file system dependency, isolating shared
 * logic from the native environment.
 * * Security/Sandbox: Uses the [Context] to guarantee the creation of the
 * .db file within the application's sandboxed space.
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