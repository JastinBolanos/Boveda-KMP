package com.jastin.boveda.presentation.model

/* =========================================================================
 * MODELOS DE PRESENTACIÓN (VIEW STATE)
 * Representación visual optimizada de los datos.
 * * Separación de Responsabilidades: Contienen datos pre-procesados (ej. Strings formateados),
 *   aislando las entidades de dominio y el esquema de BD.
 * * Rendimiento UI: Al delegar el formateo a capas inferiores, se garantiza que
 *   Compose actúe como una UI pasiva, evitando lógica pesada durante la recomposición.
 * ========================================================================= */

// --- 1. ESTADOS DE INTERFAZ ---
// Diccionario de los posibles estados visuales de una transacción.
enum class TxUiStatus { COMPLETED, PENDING }

// --- 2. SUB-MODELOS ANIDADOS ---
// Nodos de información que componen listas complejas dentro de la vista principal (ej. Auditoría).
data class TimelineEventUi(
    val status: String,
    val time: String,
    val done: Boolean
)

// --- 3. PAYLOAD PRINCIPAL ---
// El DTO final "masticado" que consume Jetpack Compose para pintar la vista de detalle.
data class TransactionUiModel(
    val id: String,
    val title: String,
    val amount: Double,
    val status: TxUiStatus,
    val date: String,
    val time: String,
    val method: String,
    val recipient: String,
    val reference: String,
    val timeline: List<TimelineEventUi>
)