package com.jastin.boveda.domain.repository

import com.jastin.boveda.domain.model.TransactionStatus
import com.jastin.boveda.presentation.model.TransactionUiModel
import kotlinx.coroutines.flow.StateFlow

/* =========================================================================
 * PORT: TransactionRepository
 * Contrato de persistencia (DIP). Aísla el dominio de bases de datos o red.
 * ========================================================================= */
interface TransactionRepository {

    // --- 1. LECTURAS REACTIVAS (Single Source of Truth) ---
    val transactions: StateFlow<List<TransactionUiModel>>
    val currentBalance: StateFlow<Double>

    // --- 2. MUTACIONES LOCALES ---
    fun saveTransaction(transaction: TransactionUiModel)

    // --- 3. MUTACIONES DE SINCRONIZACIÓN ---
    // ! Performance: Permite al Worker actualizar solo el estado transaccional
    // sin necesidad de reescribir toda la entidad (ej. PENDING -> COMPLETED).
    fun updateTransactionStatus(id: String, status: TransactionStatus)
}