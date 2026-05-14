package com.jastin.boveda.domain.model

import kotlinx.serialization.Serializable

/**
 * Entidad principal de Bóveda KMP.
 * Contiene la lógica central para evitar duplicidad de dinero (Idempotencia).
 */
@Serializable
data class Transaction(
    val id: String, // UUID generado en el celular: "La magia para que el banco no cobre doble"
    val amount: Double,
    val receiverName: String,
    val receiverAccount: String,
    val status: TransactionStatus,
    val timestamp: Long // Vital para calcular si pasaron las 24 horas (TTL)
)

enum class TransactionStatus {
    PENDING,    // Guardado localmente, esperando red
    COMPLETED,  // El servidor del banco respondió 200 OK
    FAILED,     // Rechazado (ej. no hay fondos reales)
    EXPIRED     // El TTL mató la transacción (Pasaron 24h sin internet)
}