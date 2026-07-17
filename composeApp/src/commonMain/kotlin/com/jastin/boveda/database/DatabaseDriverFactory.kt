package com.jastin.boveda.database

import app.cash.sqldelight.db.SqlDriver

/* =========================================================================
 * DATABASE DRIVER CONTRACT (EXPECT)
 * Abstraction to initialize the SQLite engine. Allows the shared layer
 * (commonMain) to manage persistence without being coupled to the
 * storage restrictions of Android or iOS.
 * ========================================================================= */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}