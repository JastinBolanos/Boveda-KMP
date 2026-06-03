package com.jastin.boveda.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.jastin.boveda.database.BovedaDatabase
import com.jastin.boveda.domain.model.TransactionStatus
import com.jastin.boveda.domain.repository.TransactionRepository
import com.jastin.boveda.presentation.model.TransactionUiModel
import com.jastin.boveda.presentation.model.TxUiStatus
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
 * 1. SSOT: La UI observa exclusivamente estados reactivos (StateFlow).
 * 2. Mapeo: Aísla el modelo de persistencia (SQL) del modelo de vista (UI).
 * 3. Reactividad: `WhileSubscribed(5000)` evita consultas redundantes ante
 * cambios de configuración (rotación/pausa).
 * ========================================================================= */
class SqlDelightTransactionRepository(
    database: BovedaDatabase,
    private val scope: CoroutineScope
) : TransactionRepository {

    private val queries = database.transactionEntityQueries

    // --- 1. LECTURA Y MAPEO DE CAPAS ---
    override val transactions: StateFlow<List<TransactionUiModel>> = queries.selectAllTransactions()
        .asFlow()
        // NOTA: La operación `mapToList` debe ejecutarse en `Dispatchers.IO` para
        // evitar el bloqueo del hilo principal (UI Thread), garantizando la
        // fluidez de los 120 Hz durante consultas pesadas a la base de datos.
        .mapToList(Dispatchers.IO)
        .map { entityList ->
            entityList.map { entity ->
                TransactionUiModel(
                    id = entity.id,
                    title = entity.title,
                    amount = entity.amount,
                    status = if (entity.status == TxUiStatus.PENDING.name) TxUiStatus.PENDING else TxUiStatus.COMPLETED,
                    date = "Hoy",
                    time = "00:00",
                    method = "Saldo Bóveda",
                    recipient = "Transferencia Local",
                    reference = "REF-${entity.id.take(6)}",
                    timeline = emptyList()
                )
            }
        }.stateIn(
            scope = scope,
            // NOTA: Se emplea WhileSubscribed(5000) para evitar la reinicialización
            // del flujo y consultas redundantes a la BD durante cambios de configuración
            // del dispositivo (ej. rotación).
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

    // --- ESCRITURA (IDEMPOTENCIA Y ENCOLAMIENTO) ---
    override fun saveTransaction(transaction: TransactionUiModel) {
        scope.launch(Dispatchers.IO) {
            val currentTimestamp = getCurrentTimeMillis()
            val uniqueId = transaction.id.ifBlank { currentTimestamp.toString() }

            queries.insertTransaction(
                id = uniqueId,
                title = transaction.title,
                amount = transaction.amount,
                status = TxUiStatus.PENDING.name,
                timestamp = currentTimestamp
            )
            BovedaSyncWorker().enqueueSync()
        }
    }
    override fun updateTransactionStatus(id: String, status: TransactionStatus) {
        scope.launch(Dispatchers.IO) {
            queries.updateTransactionStatus(status.name, id)
        }
    }
}