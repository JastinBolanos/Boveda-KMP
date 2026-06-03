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

/**
 * =========================================================================
 * REPOSITORIO DE TRANSACCIONES (SINGLE SOURCE OF TRUTH)
 * =========================================================================
 * Implementa [TransactionRepository] con SQLDelight bajo 3 principios:
 * 1. SSOT: La UI solo observa pasivamente los StateFlows.
 * 2. Mapeo de Capas: Aísla los datos de SQLite usando modelos de vista.
 * 3. Reactividad: `WhileSubscribed(5000)` previene re-consultas en la BD
 * al rotar o minimizar la aplicación.
 */
class SqlDelightTransactionRepository(
    database: BovedaDatabase,
    private val scope: CoroutineScope
) : TransactionRepository {

    private val queries = database.transactionEntityQueries

    // --- 1. LECTURA Y MAPEO DE CAPAS ---
    override val transactions: StateFlow<List<TransactionUiModel>> = queries.selectAllTransactions()
        .asFlow()
        // ¡CRÍTICO! El mapToList debe ir en Dispatchers.IO.
        // Si lo dejas en el hilo principal (Main), la app congelará los 120 Hz de la pantalla
        // al leer bases de datos grandes.
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
            // ¡MINA TERRESTRE! WhileSubscribed(5000) evita que el StateFlow se destruya y
            // vuelva a consultar la BD si el usuario simplemente rota la pantalla del celular.
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- 2. MOTOR DE CÁLCULO DE SALDO ---
    override val currentBalance: StateFlow<Double> = transactions.map { list ->
        val initialBalance = 1500.00
        val totalSpent = list.sumOf { it.amount }
        initialBalance + totalSpent
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 1500.00
    )

    // --- 3. ESCRITURA (IDEMPOTENCIA Y ENCOLAMIENTO) ---
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