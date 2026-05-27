package com.jastin.boveda.presentation.screens.transfer

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.jastin.boveda.domain.model.TransactionStatus
import com.jastin.boveda.globalTransactionRepository
import com.jastin.boveda.presentation.model.TimelineEventUi
import com.jastin.boveda.presentation.model.TransactionUiModel
import com.jastin.boveda.presentation.model.TxUiStatus
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/* =========================================================================
 * SCREEN MODEL DE TRANSFERENCIAS (MVI PATTERN)
 * Gestor de estado y lógica de negocio para la pantalla de pagos.
 * ========================================================================= */

// --- 1. CONTRATO MVI (STATE & INTENTS) ---
data class TransferState(
    val amount: String = "",
    val recipient: String = "",
    val balance: Double = 1500.00,
    val isLoading: Boolean = false,
    val successTransactionId: String? = null
)

sealed class TransferIntent {
    data class UpdateAmount(val amount: String) : TransferIntent()
    data class UpdateRecipient(val recipient: String) : TransferIntent()
    object ClearNavigation : TransferIntent()
}

class TransferScreenModel : StateScreenModel<TransferState>(TransferState()) {

    private val repository = globalTransactionRepository

    init {
        mutableState.update { it.copy(balance = repository.currentBalance.value) }
    }

    // --- 2. PROCESAMIENTO DE ACCIONES (REDUCER) ---
    fun onIntent(intent: TransferIntent) {
        when (intent) {
            is TransferIntent.UpdateAmount -> mutableState.update { it.copy(amount = intent.amount) }
            is TransferIntent.UpdateRecipient -> mutableState.update { it.copy(recipient = intent.recipient) }
            is TransferIntent.ClearNavigation -> mutableState.update { it.copy(successTransactionId = null) }
        }
    }

    // --- 3. EJECUCIÓN TRANSACCIONAL (FOREGROUND + FALLBACK) ---
    fun executeTransfer() {
        val amountNum = state.value.amount.toDoubleOrNull() ?: return
        if (amountNum <= 0 || amountNum > state.value.balance) return

        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            val safeId = "tx_${kotlin.random.Random.nextLong(100000, 999999)}"

            val newTx = TransactionUiModel(
                id = safeId,
                title = state.value.recipient,
                amount = -amountNum,
                status = TxUiStatus.PENDING,
                date = "Hoy",
                time = "Ahora",
                method = "Saldo Bóveda",
                recipient = state.value.recipient,
                reference = "REF-${kotlin.random.Random.nextInt(10000, 99999)}",
                timeline = listOf(
                    TimelineEventUi("Iniciada", "Ahora", true),
                    TimelineEventUi("Procesando en red", "--:--", false)
                )
            )

            repository.saveTransaction(newTx)

            try {
                val networkClient = com.jastin.boveda.data.remote.NetworkClient()
                val isSuccess = networkClient.processPendingTransaction(safeId)

                if (isSuccess) {
                    repository.updateTransactionStatus(safeId, TransactionStatus.COMPLETED)
                }
            } catch (e: Exception) {
                println("Fallo en primer plano. El Worker lo tomará.")
            }

            mutableState.update {
                it.copy(
                    isLoading = false,
                    amount = "",
                    recipient = "",
                    successTransactionId = safeId
                )
            }
        }
    }
}