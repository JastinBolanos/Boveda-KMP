package com.jastin.boveda.utils

import android.content.Context
import androidx.work.*
import com.jastin.boveda.data.remote.NetworkClient
import com.jastin.boveda.domain.usecase.SyncPendingTransactionsUseCase
import com.jastin.boveda.globalTransactionRepository

/* =========================================================================
 * 1. NATIVE ANDROID CLASS (WORKMANAGER) ---
 * ========================================================================= */
class SyncAndroidWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    // SHIELD: Automatic retry to avoid data loss if there is no network.
    override suspend fun doWork(): Result {
        return try {
            println("⚙️ WorkManager: Waking up to sync...")
            val useCase = SyncPendingTransactionsUseCase(globalTransactionRepository, NetworkClient())
            useCase()
            Result.success()
        } catch (e: Exception) {
            println("⚠️ WorkManager: Network error. Enqueueing retry... Error: ${e.message}")
            Result.retry()
        }
    }
}

/* =========================================================================
 * 2. GLOBAL CONTEXT REGISTRY (Lightweight Service Locator)
 * ========================================================================= */
object AndroidPlatformContext {
    lateinit var applicationContext: Context
}

/* =========================================================================
 * 3. KMP CONTRACT IMPLEMENTATION ('actual')
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
        println("📦 WorkManager: Synchronization work enqueued.")
    }
}