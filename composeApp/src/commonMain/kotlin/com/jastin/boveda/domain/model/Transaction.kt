package com.jastin.boveda.domain.model

import kotlinx.serialization.Serializable

/* =========================================================================
 * ENTIDAD DE DOMINIO: TRANSACTION
 * Representa el núcleo del negocio (Core Domain) en Clean Architecture.
 * Esta clase es estrictamente agnóstica a la UI y a las bases de datos.
 * ========================================================================= */

// --- 1. MODELO DE DATOS PRINCIPAL ---
// Entidad serializable que actúa como DTO hacia el backend y base de la persistencia.
@Serializable
data class Transaction(
    val id: String,
    val amount: Double,
    val receiverName: String,
    val receiverAccount: String,
    val status: TransactionStatus,
    val timestamp: Long
)

// --- 2. CICLO DE VIDA DE LA OPERACIÓN ---
// Define los estados posibles del flujo financiero para garantizar la
// integridad y consistencia en sistemas distribuidos.
enum class TransactionStatus {
    PENDING,    // Estado inicial en modo offline
    COMPLETED,  // Confirmado por el servidor
    FAILED,     // Rechazo de negocio (ej. saldo insuficiente)
    EXPIRED     // Tiempo de vida superado (TTL) sin sincronización
}