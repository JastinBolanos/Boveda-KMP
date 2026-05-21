package com.jastin.boveda.domain.repository

import com.jastin.boveda.presentation.model.TransactionUiModel
import kotlinx.coroutines.flow.StateFlow

/*
 * =========================================================================
 * CONTRATO DE REPOSITORIO (DOMAIN PORT)
 * =========================================================================
 * Interfaz central del dominio que define las operaciones de persistencia.
 * * Inversión de Dependencias (DIP): Al pertenecer a la capa de Dominio, esta
 * abstracción garantiza que las reglas de negocio no se acoplen a tecnologías
 * específicas (como SQLDelight, Room o Firebase). Las capas externas deben
 * adaptarse e implementar este contrato.
 * * Diseño Reactivo: El uso de [StateFlow] impone un flujo de datos unidireccional
 * (UDF), permitiendo que la UI observe pasivamente las mutaciones de la base de datos.
 */
interface TransactionRepository {
    val transactions: StateFlow<List<TransactionUiModel>>
    val currentBalance: StateFlow<Double>
    fun saveTransaction(transaction: TransactionUiModel)
}