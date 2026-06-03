package com.jastin.boveda.domain.usecase

import com.jastin.boveda.data.remote.NetworkClient
import com.jastin.boveda.domain.model.TransactionStatus
import com.jastin.boveda.domain.repository.TransactionRepository
import com.jastin.boveda.presentation.model.TxUiStatus

/* =========================================================================
 * CASO DE USO: SINCRONIZACIÓN OFFLINE
 * Orquesta la recuperación de pagos locales y su envío al servidor.
 * ========================================================================= */

class SyncPendingTransactionsUseCase(
    private val repository: TransactionRepository,
    private val networkClient: NetworkClient
) {

    // --- 1. MOTOR DE PROCESAMIENTO ---
    suspend operator fun invoke() {
        val allTransactions = repository.transactions.value
        val pendingTransactions = allTransactions.filter { it.status == TxUiStatus.PENDING }

        if (pendingTransactions.isEmpty()) return

        for (tx in pendingTransactions) {
            try {
                val isSuccess = networkClient.processPendingTransaction(tx.id)

                if (isSuccess) {
                    repository.updateTransactionStatus(tx.id, TransactionStatus.COMPLETED)
                }
            } catch (e: Exception) {
                println("Worker Error: Fallo al sincronizar tx ${tx.id} -> ${e.message}")
            }
        }
    }
}