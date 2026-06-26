package com.jastin.boveda.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.delay

/* =========================================================================
 * SIMULADOR DE RED (NETWORK CLIENT)
 * Emula la capa HTTP para validar la lógica de sincronización offline.
 * ========================================================================= */
class NetworkClient {

    // ✅ BLINDAJE DE MEMORIA (SINGLETON)
    // Al usar 'companion object', garantizamos que solo exista UN motor HTTP
    // en toda la vida de la app, sin importar cuántas veces instancies NetworkClient.
    // Esto evita el colapso de RAM y Thread Leaks durante reintentos del Worker.
    companion object {
        private val httpClient = HttpClient()
    }

    suspend fun processPendingTransaction(transactionId: String): Boolean {
        // --- EL PING DE LA VERDAD ---
        // Es superrápido porque devuelve una respuesta vacía. Si no hay internet, esto explota.
        try {
            httpClient.get("https://clients3.google.com/generate_204")
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