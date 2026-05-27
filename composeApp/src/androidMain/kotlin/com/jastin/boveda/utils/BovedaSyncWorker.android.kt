package com.jastin.boveda.utils

import android.content.Context
import androidx.work.*
import com.jastin.boveda.data.remote.NetworkClient
import com.jastin.boveda.domain.usecase.SyncPendingTransactionsUseCase
import com.jastin.boveda.globalTransactionRepository

/* =========================================================================
 * 1. CLASE NATIVA DE ANDROID (WORKMANAGER)
 * ========================================================================= */
class SyncAndroidWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        println("⚙️ WorkManager: Despertando para sincronizar...")
        val useCase = SyncPendingTransactionsUseCase(globalTransactionRepository, NetworkClient())
        useCase()
        return Result.success()
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