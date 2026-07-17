package com.jastin.boveda.utils

import com.jastin.boveda.domain.usecase.SyncPendingTransactionsUseCase

/* =========================================================================
 * SYNCHRONIZATION WORKER CONTRACT (EXPECT)
 * Abstraction for background tasks. Allows triggering persistent
 * synchronization by delegating lifecycle management to the native system.
 * ========================================================================= */
expect class BovedaSyncWorker() {
    val syncUseCase: SyncPendingTransactionsUseCase
    fun enqueueSync()
}