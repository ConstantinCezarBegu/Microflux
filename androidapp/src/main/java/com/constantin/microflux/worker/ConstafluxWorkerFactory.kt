package com.constantin.microflux.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider

class ConstafluxWorkerFactory @Inject constructor(
    private val providers: Map<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<WorkerAssistedInjectFactory>>
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val workerClass = try {
            Class.forName(workerClassName)
        } catch (e: ClassNotFoundException) {
            return null
        }
        val workerProvider = requireNotNull(providers[workerClass]) {
            "No provider found for worker: $workerClassName"
        }
        return workerProvider.get().create(appContext, workerParameters)
    }
}
