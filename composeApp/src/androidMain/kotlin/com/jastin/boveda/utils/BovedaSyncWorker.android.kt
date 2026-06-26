package com.jastin.boveda.utils

import android.content.Context
import androidx.work.*
import com.jastin.boveda.data.remote.NetworkClient
import com.jastin.boveda.domain.usecase.SyncPendingTransactionsUseCase
import com.jastin.boveda.globalTransactionRepository

/* =========================================================================
 * 1. CLASE NATIVA DE ANDROID (WORKMANAGER)---
 * ========================================================================= */
class SyncAndroidWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    // BLINDAJE: Retry automático para no perder datos si no hay red.
    override suspend fun doWork(): Result {
        return try {
            println("⚙️ WorkManager: Despertando para sincronizar...")
            val useCase = SyncPendingTransactionsUseCase(globalTransactionRepository, NetworkClient())
            useCase()
            Result.success()
        } catch (e: Exception) {
            println("⚠️ WorkManager: Error de red. Encolando reintento... Error: ${e.message}")
            Result.retry()
        }
    }
}

/* =========================================================================
 * 2. REGISTRO DE CONTEXTO GLOBAL (Service Locator ligero)
 * ========================================================================= */
object AndroidPlatformContext {
    lateinit var applicationContext: Context
}

/* =========================================================================
 * 3. IMPLEMENTACIÓN DEL CONTRATO KMP ('actual')
 * ========================================================================= */
actual class BovedaSyncWorker actual constructor() {

    actual val syncUseCase = SyncPendingTransactionsUseCase(globalTransactionRepository, NetworkClient())

    actual fun enqueueSync() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SyncAndroidWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(AndroidPlatformContext.applicationContext).enqueue(workRequest)
        println("📦 WorkManager: Trabajo de sincronización encolado.")
    }
}