package com.jastin.boveda.utils

import com.jastin.boveda.domain.usecase.SyncPendingTransactionsUseCase

expect class BovedaSyncWorker() {
    val syncUseCase: SyncPendingTransactionsUseCase
    fun enqueueSync()
}