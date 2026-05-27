package com.jastin.boveda.utils

import com.jastin.boveda.data.remote.NetworkClient
import com.jastin.boveda.domain.usecase.SyncPendingTransactionsUseCase
import com.jastin.boveda.globalTransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

/* =========================================================================
 * IMPLEMENTACIÓN DEL CONTRATO KMP ('actual') PARA iOS
 * ========================================================================= */
actual class BovedaSyncWorker actual constructor() {

    actual val syncUseCase = SyncPendingTransactionsUseCase(globalTransactionRepository, NetworkClient())

    actual fun enqueueSync() {
        CoroutineScope(Dispatchers.IO).launch {
            println("🍎 iOS Background: Iniciando sincronización...")
            syncUseCase()
        }
    }
}