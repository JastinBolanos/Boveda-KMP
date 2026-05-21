package com.jastin.boveda.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.jastin.boveda.database.BovedaDatabase
import com.jastin.boveda.domain.repository.TransactionRepository
import com.jastin.boveda.presentation.model.TransactionUiModel
import com.jastin.boveda.presentation.model.TxUiStatus
import com.jastin.boveda.utils.getCurrentTimeMillis
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
 * Implementación concreta del contrato [TransactionRepository] utilizando SQLDelight.
 * * Decisiones Arquitectónicas:
 * 1. Single Source of Truth: La UI nunca guarda estados locales, observa pasivamente
 * los StateFlows expuestos por esta clase.
 * 2. Layer Mapping: Se aíslan los datos crudos de SQLite hacia modelos de vista limpios
 * para evitar acoplamiento de capas.
 * 3. Reactividad: StateFlow con caché de 5 segundos ('WhileSubscribed(5000)') protege
 * la memoria y previene re-consultas durante cambios de configuración (rotación/minimizado).
 */
class SqlDelightTransactionRepository(
    database: BovedaDatabase,
    private val scope: CoroutineScope
) : TransactionRepository {

    private val queries = database.transactionEntityQueries

    // --- 1. LECTURA Y MAPEO DE CAPAS ---
    // Transforma entidades crudas de la BD a modelos limpios para que la UI no crashee
    // intentando leer formatos no soportados.
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
    // Recalcula el dinero disponible cada vez que la tabla de transacciones muta.
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
    // Inyecta registros en SQLite. Si no hay internet, se guardan como PENDING.
    override fun saveTransaction(transaction: TransactionUiModel) {
        scope.launch(Dispatchers.IO) {

            // Usamos nuestra función nativa KMP para evitar colisiones del compilador con java.time
            val currentTimestamp = getCurrentTimeMillis()

            // Si la transacción viene sin ID (nueva), le asignamos el timestamp como llave única temporal
            val uniqueId = transaction.id.ifBlank { currentTimestamp.toString() }

            queries.insertTransaction(
                id = uniqueId,
                title = transaction.title,
                amount = transaction.amount,
                status = transaction.status.name,
                timestamp = currentTimestamp
            )
        }
    }
}