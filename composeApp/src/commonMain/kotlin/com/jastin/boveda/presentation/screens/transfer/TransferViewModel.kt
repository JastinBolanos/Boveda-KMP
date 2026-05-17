package com.jastin.boveda.presentation.screens.transfer

import cafe.adriel.voyager.core.model.StateScreenModel
import com.jastin.boveda.presentation.model.TimelineEventUi
import com.jastin.boveda.presentation.model.TransactionUiModel
import com.jastin.boveda.presentation.model.TxUiStatus
import kotlinx.coroutines.flow.update
import kotlin.random.Random

data class TransferState(
    val amount: String = "",
    val recipient: String = "",
    val isOffline: Boolean = false,
    val balance: Double = 1500.00
)

sealed class TransferIntent {
    data class UpdateAmount(val amount: String) : TransferIntent()
    data class UpdateRecipient(val recipient: String) : TransferIntent()
    object ToggleOffline : TransferIntent()
}

class TransferScreenModel : StateScreenModel<TransferState>(TransferState()) {
    fun onIntent(intent: TransferIntent) {
        when (intent) {
            is TransferIntent.UpdateAmount -> mutableState.update { it.copy(amount = intent.amount) }
            is TransferIntent.UpdateRecipient -> mutableState.update { it.copy(recipient = intent.recipient) }
            is TransferIntent.ToggleOffline -> mutableState.update { it.copy(isOffline = !it.isOffline) }
        }
    }

    fun executeTransfer(): TransactionUiModel? {
        val amountNum = state.value.amount.toDoubleOrNull() ?: return null
        if (amountNum <= 0 || amountNum > state.value.balance) return null

        val safeId = "tx_${Random.nextLong(100000, 999999)}"

        return TransactionUiModel(
            id = safeId,
            title = state.value.recipient,
            amount = -amountNum,
            status = if (state.value.isOffline) TxUiStatus.PENDING else TxUiStatus.COMPLETED,
            date = "Hoy",
            time = "Ahora",
            method = "Saldo Bóveda",
            recipient = state.value.recipient,
            reference = "REF-${Random.nextInt(10000, 99999)}",
            timeline = listOf(
                TimelineEventUi("Iniciada", "Ahora", true),
                if (state.value.isOffline) TimelineEventUi("Esperando conexión", "--:--", false)
                else TimelineEventUi("Completada", "Ahora", true)
            )
        )
    }
}