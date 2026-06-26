package com.jastin.boveda.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.jastin.boveda.database.BovedaDatabase
import com.jastin.boveda.domain.model.Transaction
import com.jastin.boveda.domain.model.TransactionStatus
import com.jastin.boveda.domain.repository.TransactionRepository
import com.jastin.boveda.utils.getCurrentTimeMillis
import com.jastin.boveda.utils.BovedaSyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/* =========================================================================
 * REPOSITORIO DE TRANSACCIONES (SINGLE SOURCE OF TRUTH)
 * * Implementación de [TransactionRepository] mediante SQLDelight:
 * 1. Mapeo: Extrae los datos de SQLite y los convierte ESTRICTAMENTE a
 * entidades de Dominio puro, aislando la BD de la Interfaz de Usuario.
 * ========================================================================= */
class SqlDelightTransactionRepository(
    database: BovedaDatabase,
    private val scope: CoroutineScope
) : TransactionRepository {

    private val queries = database.transactionEntityQueries

    // --- 1. LECTURA Y MAPEO DE CAPAS (ENTITY -> DOMAIN) ---
    override val transactions: StateFlow<List<Transaction>> = queries.selectAllTransactions()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { entityList ->
            entityList.map { entity ->
                Transaction(
                    id = entity.id,
                    amount = -entity.amount,
                    receiverName = entity.title,
                    receiverAccount = "Cuenta Local",
                    status = if (entity.status == TransactionStatus.PENDING.name) TransactionStatus.PENDING else TransactionStatus.COMPLETED,
                    timestamp = entity.timestamp
                )
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- MOTOR DE CÁLCULO DE SALDO ---
    override val currentBalance: StateFlow<Double> = transactions.map { list ->
        val initialBalance = 1500.00
        val totalSpent = list.sumOf { it.amount }
        initialBalance + totalSpent
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 1500.00
    )

    // --- ESCRITURA (DOMAIN -> ENTITY) ---
    override fun saveTransaction(transaction: Transaction) {
        scope.launch(Dispatchers.IO) {
            try {
                val currentTimestamp = getCurrentTimeMillis()
                val uniqueId = transaction.id.ifBlank { currentTimestamp.toString() }

                queries.insertTransaction(
                    id = uniqueId,
                    title = transaction.receiverName,
                    amount = kotlin.math.abs(transaction.amount),
                    status = transaction.status.name,
                    timestamp = currentTimestamp
                )
                BovedaSyncWorker().enqueueSync()
            } catch (e: Exception) {
                println("⚠️ Error SQL / Idempotencia: ${e.message}")
            }
        }
    }

    override fun updateTransactionStatus(id: String, status: TransactionStatus) {
        scope.launch(Dispatchers.IO) {
            queries.updateTransactionStatus(status.name, id)
        }
    }
}