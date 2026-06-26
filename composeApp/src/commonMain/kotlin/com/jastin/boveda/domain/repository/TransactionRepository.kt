package com.jastin.boveda.domain.repository

import com.jastin.boveda.domain.model.Transaction
import com.jastin.boveda.domain.model.TransactionStatus
import kotlinx.coroutines.flow.StateFlow

/* =========================================================================
 * PORT: TransactionRepository
 * Contrato de persistencia (DIP). Aísla el dominio de bases de datos o red.
 * ========================================================================= */
interface TransactionRepository {

    // --- 1. LECTURAS REACTIVAS (Single Source of Truth) ---
    val transactions: StateFlow<List<Transaction>>
    val currentBalance: StateFlow<Double>

    // --- 2. MUTACIONES LOCALES ---
    fun saveTransaction(transaction: Transaction)

    // --- 3. MUTACIONES DE SINCRONIZACIÓN ---
    fun updateTransactionStatus(id: String, status: TransactionStatus)
}