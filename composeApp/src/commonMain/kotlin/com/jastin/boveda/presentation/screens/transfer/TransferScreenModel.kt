package com.jastin.boveda.presentation.screens.transfer

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.jastin.boveda.domain.model.Transaction
import com.jastin.boveda.domain.model.TransactionStatus
import com.jastin.boveda.globalTransactionRepository
import com.jastin.boveda.utils.getCurrentTimeMillis
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
            is TransferIntent.UpdateAmount -> {
                var input = intent.amount.replace(",", ".")

                if (input.startsWith(".")) {
                    input = "0$input"
                }

                // EL BLINDAJE FINANCIERO (REGEX):
                // ARREGLO LINTER: Se simplificaron los caracteres de escape (\$)
                // ^(0|[1-9]\d*) -> Empieza con 0, o con un número del 1 al 9
                // (\.\d{0,2})?$ -> Puede tener un punto opcional con máximo 2 decimales.
                val isValidMoneyFormat = input.isEmpty() || input.matches(Regex("""^(0|[1-9]\d*)(\.\d{0,2})?$"""))

                if (isValidMoneyFormat) {
                    mutableState.update { it.copy(amount = input) }
                }
            }
            is TransferIntent.UpdateRecipient -> mutableState.update { it.copy(recipient = intent.recipient) }
            is TransferIntent.ClearNavigation -> mutableState.update { it.copy(successTransactionId = null) }
        }
    }

    // --- 3. EJECUCIÓN TRANSACCIONAL (FOREGROUND + FALLBACK) ---
    fun executeTransfer() {
        val amountNum = state.value.amount.toDoubleOrNull() ?: return
        // Bloqueo matemático duro: Ni montos negativos, ni sobregiros, ni más de 500
        if (amountNum <= 0 || amountNum > state.value.balance || amountNum > 500.00) return

        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            val safeId = "tx_${kotlin.random.Random.nextLong(100000, 999999)}"

            // ARREGLO ARQUITECTÓNICO: Mapeo UI -> DOMAIN
            // El ScreenModel empaqueta los datos de la UI en una Entidad de Dominio
            // pura para enviarla al Repositorio. Toda la "basura visual" se descartó.
            val newTx = Transaction(
                id = safeId,
                amount = -amountNum,
                receiverName = state.value.recipient,
                receiverAccount = "Cuenta Local",
                status = TransactionStatus.PENDING,
                timestamp = getCurrentTimeMillis()
            )
            repository.saveTransaction(newTx)

            try {
                val networkClient = com.jastin.boveda.data.remote.NetworkClient()
                val isSuccess = networkClient.processPendingTransaction(safeId)

                if (isSuccess) {
                    repository.updateTransactionStatus(safeId, TransactionStatus.COMPLETED)
                }
            } catch (_: Exception) {
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