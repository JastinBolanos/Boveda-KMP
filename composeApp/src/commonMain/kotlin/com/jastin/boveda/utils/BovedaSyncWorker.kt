package com.jastin.boveda.utils

import com.jastin.boveda.domain.usecase.SyncPendingTransactionsUseCase

/* =========================================================================
 * CONTRATO DE WORKER DE SINCRONIZACIÓN (EXPECT)
 * Abstracción para tareas de fondo. Permite disparar la sincronización
 * persistente delegando la gestión del ciclo de vida al sistema nativo.
 * ========================================================================= */
expect class BovedaSyncWorker() {
    val syncUseCase: SyncPendingTransactionsUseCase
    fun enqueueSync()
}