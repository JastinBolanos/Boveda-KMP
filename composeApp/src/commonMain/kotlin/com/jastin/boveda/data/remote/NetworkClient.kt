package com.jastin.boveda.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.delay

/* =========================================================================
 * SIMULADOR DE RED (NETWORK CLIENT)
 * Emula la capa HTTP para validar la lógica de sincronización offline.
 * ========================================================================= */
class NetworkClient {
    private val pingClient = HttpClient()

    suspend fun processPendingTransaction(transactionId: String): Boolean {
        // --- EL PING DE LA VERDAD ---
        // Es superrápido porque devuelve una respuesta vacía. Si no hay internet, esto explota.
        try {
            pingClient.get("https://clients3.google.com/generate_204")
        } catch (e: Exception) {
            throw Exception("Dispositivo sin conexión real a internet: ${e.message}")
        }

        // --- SIMULADOR DEL BANCO ---
        // Si el código llega hasta aquí, ES PORQUE SÍ HAY INTERNET.
        // Ahora sí, simulamos el tiempo que tardaría el banco en procesar el pago.
        delay(1500)

        return true
    }
}