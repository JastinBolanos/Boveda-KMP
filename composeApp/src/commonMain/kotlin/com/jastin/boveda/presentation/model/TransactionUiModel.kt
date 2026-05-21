package com.jastin.boveda.presentation.model

/* =========================================================================
 * MODELOS DE PRESENTACIÓN (VIEW STATE)
 * Representación puramente visual de los datos de la aplicación.
 * * Separación de Responsabilidades: A diferencia de las entidades de dominio
 * o los esquemas de BD, estos modelos contienen datos pre-procesados (como
 * fechas y textos formateados como [String]).
 * * Rendimiento UI: Al delegar el cálculo, parseo y formateo a capas inferiores
 * (o a la capa de Mapeo), garantizamos que Jetpack Compose actúe como una UI
 * "tonta", limitándose exclusivamente a renderizar componentes sin ejecutar
 * lógica pesada durante la recomposición.
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